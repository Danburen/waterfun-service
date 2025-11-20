package org.waterwood.waterfunservice.dto.request.role;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.waterwood.waterfunservice.infrastructure.validation.AtLeastOneNotNull;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@AtLeastOneNotNull(fields = {"updates", "deletePermIds"})
public class PatchRolePermReq implements Serializable {
    private List<RolePermItemDTO> updates;
    private List<Integer> deletePermIds;
}
