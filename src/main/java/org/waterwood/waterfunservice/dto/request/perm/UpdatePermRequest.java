package org.waterwood.waterfunservice.dto.request.perm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.waterwood.api.enums.PermissionType;
import org.waterwood.waterfunservicecore.entity.Permission;

import java.io.Serializable;

/**
 * DTO for {@link Permission}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePermRequest implements Serializable {
    private String code;
    private String name;
    private String description;
    private PermissionType type;
    private String resource;
    private Integer parentId;
}