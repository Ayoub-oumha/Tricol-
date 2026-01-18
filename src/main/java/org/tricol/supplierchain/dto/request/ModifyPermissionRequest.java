package org.tricol.supplierchain.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModifyPermissionRequest {

    @NotNull(message = "Permission ID is required")
    private Long permissionId;

    @NotNull(message = "Granted flag is required")
    private Boolean granted;
}
