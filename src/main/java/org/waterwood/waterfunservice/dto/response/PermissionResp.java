package org.waterwood.waterfunservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.waterwood.api.enums.PermissionType;
import org.waterwood.waterfunservicecore.entity.Permission;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link Permission}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionResp implements Serializable {
    private Integer id;
    private String code;
    private String name;
    private String description;
    private PermissionType type;
    private String resource;
    private Integer parentId;
    private Instant createdAt;
}