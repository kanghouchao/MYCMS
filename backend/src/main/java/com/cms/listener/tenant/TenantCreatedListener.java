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
    Tenant tenant = ev.getTenant();
    if (tenant.getEmail() == null || tenant.getEmail().isBlank()) {
      log.warn("tenant {} has no email, skipping registration mail", tenant.getId());
      return;
    }

    if (tenant.getDomain() == null || tenant.getDomain().isBlank()) {
      log.warn("tenant {} has no domain, skipping registration mail", tenant.getId());
      return;
    }

    var token = registrationService.createToken(tenant.getId());
    String subject = "[CMS] 租户登録のご案内";
    String link = buildTenantRegisterLink(tenant.getDomain(), token);
    String body =
        String.format(
            "CMSへようこそ, %s，\n\n登録を完了するには次のリンクをクリックしてください： %s \n\nこのリンクは7日間有効です。",
            tenant.getName(), link);

    try {
      mailService.send(tenant.getEmail(), subject, body);
    } catch (Exception e) {
      log.error(
          "failed to send tenant registration email to {}: {}", tenant.getEmail(), e.getMessage());
    }
  }

  private String buildTenantRegisterLink(String domain, String token) {
    return String.format(
        "%s://%s/central/register?token=%s", appProperties.getScheme(), domain, token);
  }
}
