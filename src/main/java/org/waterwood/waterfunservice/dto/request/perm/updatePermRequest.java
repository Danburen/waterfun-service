package org.waterwood.waterfunservice.dto.request.perm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.waterwood.waterfunservice.dto.common.enums.PermissionType;

import java.io.Serializable;

/**
 * DTO for {@link org.waterwood.waterfunservice.entity.Permission}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class updatePermRequest implements Serializable {
    private String code;
    private String name;
    private String description;
    private PermissionType type;
    private String resource;
    private Integer parentId;
}