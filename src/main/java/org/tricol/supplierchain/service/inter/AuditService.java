package org.tricol.supplierchain.service.inter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.tricol.supplierchain.dto.request.AuditLogSearchCriteria;
import org.tricol.supplierchain.entity.AuditLog;



public interface AuditService {
    void logAudit(Long userId, String username, String action, String resource, String details, String ipAddress);
    Page<AuditLog> searchAuditLogs(AuditLogSearchCriteria criteria, Pageable pageable);
}
