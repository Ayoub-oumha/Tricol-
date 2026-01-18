package org.tricol.supplierchain.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserPermissionResponse {
    private Long id;
    private Long userId;
    private String username;
    private Long permissionId;
    private String permissionName;
    private boolean active;
    private Long grantedBy;
    private LocalDateTime grantedAt;
    private LocalDateTime revokedAt;
}
