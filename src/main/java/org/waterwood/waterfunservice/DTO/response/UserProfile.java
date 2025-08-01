package org.waterwood.waterfunservice.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfile {
    private String nickname;
    private String avatar;
    private String email_des;
    private String phone_des;
    private String bio;
}
