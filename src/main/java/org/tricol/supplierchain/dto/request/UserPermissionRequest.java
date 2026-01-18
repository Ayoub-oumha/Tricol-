package org.tricol.supplierchain.dto.request;

import lombok.Data;

@Data
public class UserPermissionRequest {
    private Long userId;
    private Long permissionId;
}
