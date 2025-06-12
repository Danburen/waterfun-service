package org.waterwood.waterfunservice.service.userServices;

import org.waterwood.waterfunservice.entity.User.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User register(User user);
    User authenticate(String username, String password);

    Optional<User> getUserByUsername(String username);
    Optional<User> getUserById(long id);
    List<User> getUserByRole(String role);

    void activateUser(long id);
    void deactivateUser(long id);
    void suspendUser(long id);
    void deleteUser(long id);

    void ChangeUserRole(long userId, long roleId);
}
