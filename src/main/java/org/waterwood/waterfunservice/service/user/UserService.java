package org.waterwood.waterfunservice.service.user;

import org.waterwood.waterfunservice.entity.Permission;
import org.waterwood.waterfunservice.entity.user.User;
import org.waterwood.waterfunservice.dto.response.user.UserInfoResponse;
import org.waterwood.waterfunservice.dto.request.user.UserPwdUpdateRequestBody;
import org.waterwood.waterfunservice.infrastructure.exception.business.BusinessException;

import java.util.Set;

public interface UserService {
    User getUserByUsername(String username);

    /**
     * Get user by id
     * @param id user id
     * @throws BusinessException if user not found
     * @return userinfo response dto of {@link UserInfoResponse}
     */
    User getUserById(long id);

    boolean activateUser(long id);

    boolean deactivateUser(long id);

    boolean suspendUser(long id);

    boolean deleteUser(long id);

    boolean isUserExist(long userId);

    User addUser(User user);

    User update(User user);

    void updatePwd(UserPwdUpdateRequestBody userPwdUpdateRequestBody);

    /**
     * Get user permissions
     * @param userId user id
     * @return Set of permissions.
     */
    Set<Permission> getUserPermissions(long userId);
}
