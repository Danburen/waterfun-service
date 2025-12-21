package org.waterwood.waterfunservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.waterwood.waterfunservicecore.api.resp.CloudResourcePresignedUrlResp;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPublicCardResp implements Serializable {
    private Long userUid;
    private String username;
    private CloudResourcePresignedUrlResp avatar;
    private String nickname;
    private Byte level;
    private Short visible;
}
