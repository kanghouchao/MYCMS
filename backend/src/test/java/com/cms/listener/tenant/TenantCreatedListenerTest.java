package com.cms.listener.tenant;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.cms.config.AppProperties;
import com.cms.event.tenant.TenantCreatedEvent;
import com.cms.model.central.tenant.Tenant;
import com.cms.service.central.tenant.TenantRegistrationService;
import com.cms.service.mail.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TenantCreatedListenerTest {

  @Mock
  MailService mailService;

  @Mock
  TenantRegistrationService registrationService;

  @Mock
  AppProperties appProperties;

  @InjectMocks
  TenantCreatedListener listener;

  @BeforeEach
  void setUp() {
    // lenient in case some tests don't evaluate the URL
    lenient().when(appProperties.getUrl()).thenReturn("http://example.com");
  }

  @Test
  void onTenantCreated_skipsWhenNoEmail() {
    Tenant t = new Tenant();
    t.setId(10L);
    t.setName("Acme");
    t.setEmail(null);

    listener.onTenantCreated(new TenantCreatedEvent(t));

    verifyNoInteractions(registrationService);
    verifyNoInteractions(mailService);
  }

  @Test
  void onTenantCreated_sendsEmail_whenEmailPresent() {
    Tenant t = new Tenant();
    t.setId(20L);
    t.setName("Acme");
    t.setDomain("acme.example");
    t.setEmail("owner@example.com");
    when(registrationService.createToken(20L)).thenReturn("tok123");

    listener.onTenantCreated(new TenantCreatedEvent(t));

    verify(registrationService).createToken(20L);
    verify(mailService).send(eq("owner@example.com"), anyString(), anyString());
  }

  @Test
  void onTenantCreated_handlesMailException() {
    Tenant t = new Tenant();
    t.setId(30L);
    t.setName("Acme");
    t.setDomain("acme.example");
    t.setEmail("owner2@example.com");
    when(registrationService.createToken(30L)).thenReturn("tok456");
    doThrow(new RuntimeException("smtp down")).when(mailService).send(anyString(), anyString(), anyString());

    // should not throw
    listener.onTenantCreated(new TenantCreatedEvent(t));

    verify(registrationService).createToken(30L);
    verify(mailService).send(eq("owner2@example.com"), anyString(), anyString());
  }
}
