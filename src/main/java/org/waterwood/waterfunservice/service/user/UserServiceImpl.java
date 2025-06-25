package org.waterwood.waterfunservice.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.entity.User.AccountStatus;
import org.waterwood.waterfunservice.entity.User.User;
import org.waterwood.waterfunservice.entity.permission.Permission;
import org.waterwood.waterfunservice.entity.permission.Role;
import org.waterwood.waterfunservice.entity.permission.UserPermission;
import org.waterwood.waterfunservice.entity.permission.UserRole;
import org.waterwood.waterfunservice.repository.UserPermRepo;
import org.waterwood.waterfunservice.repository.UserRepository;
import org.waterwood.waterfunservice.repository.UserRoleRepo;
import org.waterwood.waterfunservice.utils.PasswordUtil;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserRoleRepo userRoleRepo;
    @Autowired
    UserPermRepo userPermRepo;

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getUserById(long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> getUserByRole(String role) {
        return List.of();
    }

    @Override
    public List<Role> getUserRoles(long userId) {
        return userRoleRepo.findByUserId(userId)
                .stream()
                .map(UserRole::getRole)
                .toList();
    }

    @Override
    public List<Permission> getUserPermissions(long userId) {
        return userPermRepo.findByUserId(userId)
                .stream()
                .map(UserPermission::getPermission)
                .toList();
    }

    @Override
    public void activateUser(long id) {
        userRepository.findById(id).ifPresent(user ->{
            user.setAccountStatus(AccountStatus.ACTIVE);
            userRepository.save(user);
        });
    }

    @Override
    public void deactivateUser(long id) {
        userRepository.findById(id).ifPresent(user->{
            user.setAccountStatus(AccountStatus.DEACTIVATED);
            userRepository.save(user);
        });
    }

    @Override
    public void suspendUser(long id) {
        userRepository.findById(id).ifPresent(user->{
            user.setAccountStatus(AccountStatus.SUSPENDED);
            userRepository.save(user);
        });
    }

    @Override
    public void deleteUser(long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setAccountStatus(AccountStatus.DELETED);
            userRepository.save(user);
            log.info("User with ID {} has been deleted.", id);
        });
    }

    @Override
    public void ChangeUserRole(long userId, long roleId) {

    }

    private boolean checkPassword(String rawPassword, String hashedPassword) {
        return PasswordUtil.matchPassword(rawPassword, hashedPassword);
    }
}
