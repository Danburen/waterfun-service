package org.waterwood.waterfunservice.dto.response.user;

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
