package org.tricol.supplierchain.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tricol.supplierchain.dto.request.AuditLogSearchCriteria;
import org.tricol.supplierchain.entity.AuditLog;
import org.tricol.supplierchain.security.RequirePermission;
import org.tricol.supplierchain.service.inter.AuditService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/admin/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @GetMapping("/logs")
    @RequirePermission("AUDIT_VIEW")
    public ResponseEntity<Page<AuditLog>> searchAuditLogs(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String resource,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateEnd,
            @PageableDefault(size = 20, sort = "actionTimestamp") Pageable pageable
    ) {
        AuditLogSearchCriteria criteria = AuditLogSearchCriteria
                .builder()
                .userId(userId)
                .resource(resource)
                .action(action)
                .dateStart(dateStart)
                .dateEnd(dateEnd)
                .build();
        
        Page<AuditLog> logs = auditService.searchAuditLogs(criteria, pageable);
        return ResponseEntity.ok(logs);
    }

}
