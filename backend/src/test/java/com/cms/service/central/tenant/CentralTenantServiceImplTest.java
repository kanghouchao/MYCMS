package com.cms.service.central.tenant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cms.dto.central.tenant.CreateTenantRequest;
import com.cms.dto.central.tenant.TenantDto;
import com.cms.dto.central.tenant.UpdateTenantRequest;
import com.cms.event.tenant.TenantCreatedEvent;
import com.cms.model.central.tenant.Tenant;
import com.cms.repository.central.TenantRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class CentralTenantServiceImplTest {

  @Mock TenantRepository tenantRepository;
  @Mock ApplicationEventPublisher publisher;

  @Captor ArgumentCaptor<Tenant> tenantCaptor;
  @Captor ArgumentCaptor<TenantCreatedEvent> eventCaptor;

  @InjectMocks CentralTenantServiceImpl service;

  @Test
  void listReturnsPaginatedDtos() {
    Tenant t1 = new Tenant();
    t1.setId(1L);
    t1.setName("A");
    t1.setDomain("a.test");
    t1.setEmail("a@x.com");
    Tenant t2 = new Tenant();
    t2.setId(2L);
    t2.setName("B");
    t2.setDomain("b.test");
    t2.setEmail("b@x.com");
    Page<Tenant> page = new PageImpl<>(List.of(t1, t2), PageRequest.of(0, 10), 2);
    when(tenantRepository.findByNameContainingIgnoreCaseOrDomainContainingIgnoreCase(
            any(), any(), any()))
        .thenReturn(page);

    var res = service.list(1, 10, "");
    assertThat(res.getData()).hasSize(2);
    assertThat(res.getTotal()).isEqualTo(2);
    assertThat(res.getCurrentPage()).isEqualTo(1);
  }

  @Test
  void getByIdAndDomain() {
    Tenant t = new Tenant();
    t.setId(3L);
    t.setName("C");
    t.setDomain("c.test");
    when(tenantRepository.findById(3L)).thenReturn(Optional.of(t));
    when(tenantRepository.findByDomain("c.test")).thenReturn(Optional.of(t));

    Optional<TenantDto> byId = service.getById("3");
    Optional<TenantDto> byDomain = service.getByDomain("c.test");
    assertThat(byId).isPresent();
    assertThat(byDomain).isPresent();
    assertThat(byId.get().getId()).isEqualTo("3");
  }

  @Test
  void createSavesTenantAndPublishesEvent() {
    CreateTenantRequest req = new CreateTenantRequest();
    req.setName("D");
    req.setDomain("d.test");
    req.setEmail("d@x.com");
    Tenant saved = new Tenant();
    saved.setId(10L);
    saved.setName("D");
    saved.setDomain("d.test");
    when(tenantRepository.save(any(Tenant.class))).thenReturn(saved);

    service.create(req);

    verify(tenantRepository).save(tenantCaptor.capture());
    Tenant toSave = tenantCaptor.getValue();
    assertThat(toSave.getName()).isEqualTo("D");
    assertThat(toSave.getDomain()).isEqualTo("d.test");
    verify(publisher).publishEvent(any(TenantCreatedEvent.class));
  }

  @Test
  void updateModifiesNameOrThrowsWhenMissing() {
    UpdateTenantRequest req = new UpdateTenantRequest();
    req.setName("E2");
    Tenant existing = new Tenant();
    existing.setId(11L);
    existing.setName("E");
    when(tenantRepository.findById(11L)).thenReturn(Optional.of(existing));

    service.update("11", req);
    verify(tenantRepository).save(existing);
    assertThat(existing.getName()).isEqualTo("E2");

    when(tenantRepository.findById(12L)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> service.update("12", req))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void deleteAndStats() {
    service.delete("13");
    verify(tenantRepository).deleteById(13L);

    when(tenantRepository.count()).thenReturn(5L);
    var stats = service.stats();
    assertThat(stats.getTotal()).isEqualTo(5);
    assertThat(stats.getActive()).isEqualTo(5);
  }
}
