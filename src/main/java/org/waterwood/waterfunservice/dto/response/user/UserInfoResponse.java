package org.waterwood.waterfunservice.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.waterwood.waterfunservice.entity.user.AccountStatus;
import org.waterwood.waterfunservice.entity.user.User;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link User}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponse implements Serializable {
    private Long id;
    private String uid;
    private String username;
    private AccountStatus accountStatus;
    private Instant createdAt;
}