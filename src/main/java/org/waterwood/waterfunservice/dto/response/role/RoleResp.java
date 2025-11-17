package org.waterwood.waterfunservice.dto.response.role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.waterwood.waterfunservice.entity.Role;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link Role}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleResp implements Serializable {
    private Integer id;
    private String name;
    private String description;
    private Integer parentId;
    private Instant createdAt;
}