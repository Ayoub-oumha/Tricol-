package org.tricol.supplierchain.dto.request;

import lombok.Data;

@Data
public class AssignRoleRequest {
    private Long userId;
    private Long roleId;
}
