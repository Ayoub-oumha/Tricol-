package org.tricol.supplierchain.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import org.tricol.supplierchain.dto.response.AuditLogResponse;
import org.tricol.supplierchain.entity.AuditLog;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-18T17:46:12+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.17 (Eclipse Adoptium)"
)
@Component
public class AuditLogMapperImpl implements AuditLogMapper {

    @Override
    public AuditLogResponse toResponse(AuditLog auditLog) {
        if ( auditLog == null ) {
            return null;
        }

        AuditLogResponse.AuditLogResponseBuilder auditLogResponse = AuditLogResponse.builder();

        auditLogResponse.id( auditLog.getId() );
        auditLogResponse.userId( auditLog.getUserId() );
        auditLogResponse.username( auditLog.getUsername() );
        auditLogResponse.action( auditLog.getAction() );
        auditLogResponse.resource( auditLog.getResource() );
        auditLogResponse.resourceId( auditLog.getResourceId() );
        auditLogResponse.details( auditLog.getDetails() );
        auditLogResponse.createdAt( auditLog.getCreatedAt() );

        return auditLogResponse.build();
    }
}
