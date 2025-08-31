package com.cms.service.mail;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private static final Logger log = LoggerFactory.getLogger(MailServiceImpl.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Override
    public void send(String to, String subject, String body) {
        if (mailSender != null) {
            try {
                SimpleMailMessage msg = new SimpleMailMessage();
                msg.setTo(to);
                msg.setSubject(subject);
                msg.setText(body);
                mailSender.send(msg);
            } catch (Exception e) {
                log.error("failed to send mail to {}: {}", to, e.getMessage());
            }
        } else {
            // Fallback: just log the message so system can run without mail config
            log.info("[MAIL-FALLBACK] to={} subject={} body={} ", to, subject, body);
        }
    }
}
