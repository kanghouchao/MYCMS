package com.cms.listener.tenant;

import com.cms.config.AppProperties;
import com.cms.event.tenant.TenantCreatedEvent;
import com.cms.model.central.tenant.Tenant;
import com.cms.service.central.tenant.TenantRegistrationService;
import com.cms.service.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.StringUtils;

@Log4j2
@Component
@RequiredArgsConstructor
public class TenantCreatedListener {

  private final MailService mailService;
  private final TenantRegistrationService registrationService;
  private final AppProperties appProperties;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onTenantCreated(TenantCreatedEvent ev) {
    Tenant t = ev.getTenant();
    if (t.getEmail() == null || t.getEmail().isBlank()) {
      log.warn("tenant {} has no email, skipping registration mail", t.getId());
      return;
    }

    var token = registrationService.createToken(t.getId());
    String subject = "[MYCMS] 租户登録のご案内";
    String link = buildTenantRegisterLink(t, token);
    String body =
        String.format(
            "您好 %s，\n\n您的租户 %s 已成功注册。\n\n登録を完了するには次のリンクをクリックしてください： %s \n\nこのリンクは7日間有効です。",
            t.getName(), t.getName(), link);

    try {
      mailService.send(t.getEmail(), subject, body);
    } catch (Exception e) {
      log.error("failed to send tenant registration email to {}: {}", t.getEmail(), e.getMessage());
    }
  }

  private String buildTenantRegisterLink(Tenant tenant, String token) {
    String domain = tenant.getDomain();
    if (!StringUtils.hasText(domain)) {
      return String.format("%s/central/register?token=%s", appProperties.getUrl(), token);
    }

    String sanitized = domain.trim().replaceAll("/+$", "");
    if (!StringUtils.hasText(sanitized)) {
      return String.format("%s/central/register?token=%s", appProperties.getUrl(), token);
    }

    StringBuilder linkBuilder = new StringBuilder();
    if (sanitized.startsWith("http://") || sanitized.startsWith("https://")) {
      linkBuilder.append(sanitized);
    } else {
      String scheme = "https";
      Integer port = null;
      String baseUrl = appProperties.getUrl();
      if (StringUtils.hasText(baseUrl)) {
        try {
          var uri = java.net.URI.create(baseUrl.trim());
          if (uri.getScheme() != null) {
            scheme = uri.getScheme();
          }
          int uriPort = uri.getPort();
          if (uriPort > 0 && uriPort != 80 && uriPort != 443) {
            port = uriPort;
          }
        } catch (IllegalArgumentException ignored) {
          // fallback to defaults when app.url cannot be parsed
        }
      }
      linkBuilder.append(scheme).append("://").append(sanitized);
      if (port != null && !sanitized.contains(":")) {
        linkBuilder.append(":").append(port);
      }
    }

    linkBuilder.append("/register?token=").append(token);
    return linkBuilder.toString();
  }
}
