package com.cms.filter;

import com.cms.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.lang.Collections;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Log4j2
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final RedisTemplate<String, Object> redisTemplate;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    String authHeader = request.getHeader("Authorization");
    if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      Claims claims = jwtUtil.getClaims(token);
      if (Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:tokens:" + token))) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is revoked");
        return;
      }
      if (new Date().before(claims.getExpiration())) {
        // Scope/issuer check based on request path, do NOT trust client-provided
        // headers
        String issuer = claims.getIssuer();
        String path = request.getRequestURI();
        boolean isTenantPath =
            path != null && (path.startsWith("/tenant/") || path.equals("/tenant"));
        boolean isCentralPath =
            path != null && (path.startsWith("/central/") || path.equals("/central"));
        boolean scopeOk =
            (isTenantPath && "TenantAuth".equals(issuer))
                || (isCentralPath && "CentralAuth".equals(issuer))
                ||
                // allow non-namespaced endpoints when issuer exists
                (!isTenantPath && !isCentralPath);
        if (!scopeOk) {
          response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token scope for role");
          return;
        }
        String username = claims.getSubject();
        List<?> authorities = claims.get("authorities", List.class);
        if (!Collections.isEmpty(authorities)) {
          PreAuthenticatedAuthenticationToken authentication =
              new PreAuthenticatedAuthenticationToken(
                  username,
                  token,
                  authorities.stream()
                      .map(a -> new SimpleGrantedAuthority(String.valueOf(a)))
                      .toList());
          authentication.setAuthenticated(true);
          authentication.setDetails(claims);
          SecurityContextHolder.getContext().setAuthentication(authentication);
        }
      }
    }
    filterChain.doFilter(request, response);
  }
}
