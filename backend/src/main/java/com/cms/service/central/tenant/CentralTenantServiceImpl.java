package com.cms.service.central.tenant;

import com.cms.dto.central.tenant.*;
import com.cms.model.central.tenant.Tenant;
import com.cms.repository.central.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CentralTenantServiceImpl implements CentralTenantService {

    private final TenantRepository tenantRepository;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<TenantDto> list(int page, int perPage, String search) {
        int p = Math.max(1, page);
        Pageable pageable = PageRequest.of(p - 1, perPage);
        var pageRes = tenantRepository.findByNameContainingIgnoreCaseOrDomainContainingIgnoreCase(
                search == null ? "" : search,
                search == null ? "" : search,
                pageable);

        List<TenantDto> dtos = pageRes.stream().map(this::toDto).collect(Collectors.toList());

        return new PaginatedResponse<>(
                dtos,
                p,
                (dtos.isEmpty() ? 0 : (p - 1) * perPage + 1),
                pageRes.getTotalPages(),
                perPage,
                (dtos.isEmpty() ? 0 : (p - 1) * perPage + dtos.size()),
                pageRes.getTotalElements(),
                "",
                "",
                pageRes.hasNext() ? String.valueOf(p + 1) : null,
                pageRes.hasPrevious() ? String.valueOf(p - 1) : null);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TenantDto> getById(String id) {
        return tenantRepository.findById(Long.valueOf(id)).map(this::toDto);
    }

    @Override
    @Transactional
    public TenantDto create(CreateTenantRequest req) {
        Tenant t = new Tenant();
        t.setName(req.getName());
        t.setDomain(req.getDomain());
        t.setTemplate(req.getTemplate());
        Tenant saved = tenantRepository.save(t);
        return toDto(saved);
    }

    @Override
    @Transactional
    public TenantDto update(String id, UpdateTenantRequest req) {
        var tenant = tenantRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new IllegalArgumentException("tenant not found"));
        if (req.getName() != null)
            tenant.setName(req.getName());
        if (req.getDomain() != null)
            tenant.setDomain(req.getDomain());
        if (req.getTemplate() != null)
            tenant.setTemplate(req.getTemplate());
        Tenant saved = tenantRepository.save(tenant);
        return toDto(saved);
    }

    @Override
    @Transactional
    public void delete(String id) {
        tenantRepository.deleteById(Long.valueOf(id));
    }

    @Override
    @Transactional(readOnly = true)
    public TenantStats stats() {
        long total = tenantRepository.count();
        // placeholder: active/inactive/pending not modelled yet
        return new TenantStats(total, total, 0, 0);
    }

    private TenantDto toDto(Tenant t) {
        return new TenantDto(String.valueOf(t.getId()), t.getName(), t.getDomain(), t.getTemplate());
    }
}
