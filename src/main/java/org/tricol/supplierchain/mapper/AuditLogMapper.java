package org.tricol.supplierchain.mapper;

import org.mapstruct.Mapper;
import org.tricol.supplierchain.dto.response.AuditLogResponse;
import org.tricol.supplierchain.entity.AuditLog;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {
    AuditLogResponse toResponse(AuditLog auditLog);
}

