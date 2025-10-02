package com.cms.config;

import com.cms.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  private static final RequestMatcher[] CSRF_IGNORED_MATCHERS = {
      PathPatternRequestMatcher.withDefaults().matcher("/central/login"),
      PathPatternRequestMatcher.withDefaults().matcher("/tenant/login"),
      PathPatternRequestMatcher.withDefaults().matcher("/tenant/register"),
      request -> {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        return StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ");
      }
  };

  public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    RequestMatcher tenantDomainLookupMatcher = request -> HttpMethod.GET.matches(request.getMethod())
        && "/central/tenants".equals(request.getRequestURI())
        && StringUtils.hasText(request.getParameter("domain"));

    http.csrf(
        csrf -> csrf.ignoringRequestMatchers(CSRF_IGNORED_MATCHERS)
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth -> auth.requestMatchers(HttpMethod.OPTIONS, "/**")
                .permitAll()
                .requestMatchers(HttpMethod.POST, "/central/login")
                .permitAll()
                .requestMatchers(HttpMethod.POST, "/tenant/login")
                .permitAll()
                .requestMatchers(HttpMethod.POST, "/tenant/register")
                .permitAll()
                .requestMatchers("/actuator/health", "/actuator/health/**")
                .permitAll()
                .requestMatchers(tenantDomainLookupMatcher)
                .permitAll()
                .anyRequest()
                .authenticated())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }
}
