package org.waterwood.waterfunservice.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfo {
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
}
