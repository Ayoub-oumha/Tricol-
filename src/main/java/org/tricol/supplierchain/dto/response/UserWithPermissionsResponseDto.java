package org.tricol.supplierchain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserWithPermissionsResponseDto {
    private Long id;
    private String username;
    private String email;
    private String roleName;
    private Boolean enabled;
    private Set<String> effectivePermissions;
}
