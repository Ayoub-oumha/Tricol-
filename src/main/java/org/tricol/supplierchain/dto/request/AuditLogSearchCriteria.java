package org.tricol.supplierchain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogSearchCriteria {
    private Long userId;
    private String resource;
    private String action;
    private LocalDateTime dateStart;
    private LocalDateTime dateEnd;
}
