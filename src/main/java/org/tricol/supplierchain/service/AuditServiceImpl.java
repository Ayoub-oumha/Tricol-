package org.tricol.supplierchain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tricol.supplierchain.dto.request.AuditLogSearchCriteria;
import org.tricol.supplierchain.entity.AuditLog;
import org.tricol.supplierchain.repository.AuditLogRepository;
import org.tricol.supplierchain.service.inter.AuditService;
import org.tricol.supplierchain.specification.AuditLogSpecification;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Async
    @Transactional
    public void logAudit(Long userId, String username, String action, String resource, String details, String ipAddress) {
        try {
            AuditLog auditLog = AuditLog
                    .builder()
                    .userId(userId)
                    .username(username)
                    .action(action)
                    .resource(resource)
                    .details(details)
                    .ipAddress(ipAddress)
                    .actionTimestamp(LocalDateTime.now())
                    .build();
            auditLogRepository.save(auditLog);
            log.debug("Audit log created: {} - {} - {}", username, action, resource);
        } catch (Exception e) {
            log.error("Failed to create audit log: {}", e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLog> searchAuditLogs(AuditLogSearchCriteria criteria, Pageable pageable) {
        Specification<AuditLog> specification = AuditLogSpecification.withSearchCriteria(criteria);
        return auditLogRepository.findAll(specification, pageable);
    }
}
