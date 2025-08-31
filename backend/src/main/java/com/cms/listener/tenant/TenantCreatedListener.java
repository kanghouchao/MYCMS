package com.cms.listener.tenant;

import com.cms.event.tenant.TenantCreatedEvent;
import com.cms.model.central.tenant.Tenant;
import com.cms.service.mail.MailService;
import com.cms.service.central.tenant.TenantRegistrationService;
import com.cms.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TenantCreatedListener {

    private static final Logger log = LoggerFactory.getLogger(TenantCreatedListener.class);

    private final MailService mailService;
    private final TenantRegistrationService registrationService;
    private final AppProperties appProperties;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTenantCreated(TenantCreatedEvent ev) {
        Tenant t = ev.getTenant();
        if (t.getEmail() == null || t.getEmail().isBlank()) {
            log.info("tenant {} has no email, skipping registration mail", t.getId());
            return;
        }

        var token = registrationService.createToken(t.getId());
        String subject = "[MYCMS] 租户登録のご案内";
        String link = String.format("%s/central/register?token=%s", appProperties.getUrl(), token);
        String body = String.format("您好 %s，\n\n您的租户 %s 已成功注册。\n\n登録を完了するには次のリンクをクリックしてください： %s \n\nこのリンクは7日間有効です。",
                t.getName(), t.getName(), link);

        try {
            mailService.send(t.getEmail(), subject, body);
        } catch (Exception e) {
            log.error("failed to send tenant registration email to {}: {}", t.getEmail(), e.getMessage());
        }
    }
}
