package org.waterwood.waterfunservice.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.DTO.common.ServiceResult;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.entity.user.AccountStatus;
import org.waterwood.waterfunservice.entity.user.User;
import org.waterwood.waterfunservice.repository.*;
import org.waterwood.waterfunservice.utils.security.PasswordUtil;

import java.time.Instant;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository, RoleService roleService) {
        this.userRepository = userRepository;
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserById(long id) {
        return userRepository.findById(id);
    }

    public ServiceResult<Void> activateUser(long id) {
        return findUserAndUpdateStatus(id, AccountStatus.ACTIVE);
    }

    public ServiceResult<Void> deactivateUser(long id) {
        return findUserAndUpdateStatus(id, AccountStatus.DEACTIVATED);
    }

    public ServiceResult<Void> suspendUser(long id) {
        return findUserAndUpdateStatus(id, AccountStatus.SUSPENDED);
    }

    public ServiceResult<Void> deleteUser(long id) {
        return findUserAndUpdateStatus(id, AccountStatus.DELETED);

    }

    public boolean isUserExist(long userId) {
        return userRepository.existsById(userId);
    }

    private boolean checkPassword(String rawPassword, String hashedPassword) {
        return PasswordUtil.matchPassword(rawPassword, hashedPassword);
    }

    public ServiceResult<Void> addUser(User user) {
        userRepository.save(user);
        return ServiceResult.accept();
    }

    private ServiceResult<Void> findUserAndUpdateStatus(long userId, AccountStatus status) {
        return findUserAndUpdate(userId, user -> {
            user.setAccountStatus(status);
            user.setStatusChangedAt(Instant.now());
            user.setStatusChangeReason("Status changed to " + status.name());
        });
    }

    private ServiceResult<Void> findUserAndUpdate(long userId, Consumer<User> updater) {
        return userRepository.findById(userId).map(user -> {
            updater.accept(user);
            userRepository.save(user);
            return ServiceResult.accept();
        }).orElse(
                ServiceResult.failure(ResponseCode.USER_NOT_FOUND, "User "+ userId + " does not exist.")
        );
    }
}
