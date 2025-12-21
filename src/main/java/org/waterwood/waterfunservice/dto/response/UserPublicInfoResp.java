package org.waterwood.waterfunservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.waterwood.waterfunservicecore.api.resp.CloudResourcePresignedUrlResp;
import org.waterwood.waterfunservicecore.entity.user.Gender;

import java.io.Serializable;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPublicInfoResp implements Serializable {
    private Long userUid;
    private String username;
    private CloudResourcePresignedUrlResp avatar;
    private Instant createdAt;
    private Instant lastActiveAt;
}
