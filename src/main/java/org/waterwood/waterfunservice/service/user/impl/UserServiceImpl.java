package org.waterwood.waterfunservice.service.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.waterwood.waterfunservice.dto.response.ResponseCode;
import org.waterwood.waterfunservice.dto.request.user.UserPwdUpdateRequestBody;
import org.waterwood.waterfunservice.entity.Permission;
import org.waterwood.waterfunservice.entity.Role;
import org.waterwood.waterfunservice.entity.user.AccountStatus;
import org.waterwood.waterfunservice.entity.user.User;
import org.waterwood.waterfunservice.entity.user.UserPermission;
import org.waterwood.waterfunservice.entity.user.UserRole;
import org.waterwood.waterfunservice.infrastructure.persistence.user.UserPermRepo;
import org.waterwood.waterfunservice.infrastructure.persistence.user.UserRepository;
import org.waterwood.waterfunservice.infrastructure.exception.business.BusinessException;
import org.waterwood.waterfunservice.infrastructure.mapper.UserMapper;
import org.waterwood.waterfunservice.infrastructure.persistence.user.UserRoleRepo;
import org.waterwood.waterfunservice.infrastructure.utils.context.ThreadLocalUtil;
import org.waterwood.waterfunservice.service.role.RoleServiceImpl;
import org.waterwood.waterfunservice.service.user.UserService;
import org.waterwood.waterfunservice.infrastructure.utils.security.PasswordUtil;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserRoleRepo userRoleRepo;
    private final RoleServiceImpl roleService;
    private final UserPermRepo userPermRepo;

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                ()-> new BusinessException(ResponseCode.USER_NOT_FOUND)
        );
    }

    @Override
    public User getUserById(long id) {
        return  userRepository.findById(id).orElseThrow(
                ()-> new BusinessException(ResponseCode.USER_NOT_FOUND)
        );
    }

    @Override
    public boolean activateUser(long id) {
        return findUserAndUpdateStatus(id, AccountStatus.ACTIVE);
    }

    @Override
    public boolean deactivateUser(long id) {
        return findUserAndUpdateStatus(id, AccountStatus.DEACTIVATED);
    }

    @Override
    public boolean suspendUser(long id) {
        return findUserAndUpdateStatus(id, AccountStatus.SUSPENDED);
    }

    @Override
    public boolean deleteUser(long id) {
        return findUserAndUpdateStatus(id, AccountStatus.DELETED);
    }

    @Override
    public boolean isUserExist(long userId) {
        return userRepository.existsById(userId);
    }

    private boolean checkPassword(String rawPassword, String hashedPassword) {
        return PasswordUtil.matchPassword(rawPassword, hashedPassword);
    }

    @Override
    public User addUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(User user){
        return addUser(user);
    }

    @Transactional
    @Override
    public void updatePwd(UserPwdUpdateRequestBody userPwdUpdateRequestBody) {
        String oldPwd = userPwdUpdateRequestBody.getOldPwd();
        String newPwd = userPwdUpdateRequestBody.getNewPwd();
        String confirmPwd = userPwdUpdateRequestBody.getConfirmPwd();
        if(oldPwd.equals(newPwd)) throw new BusinessException(ResponseCode.PASSWORD_TWO_PASSWORD_MUST_DIFFERENT);
        if(! newPwd.equals(confirmPwd)) throw new BusinessException(ResponseCode.PASSWORD_TWO_PASSWORD_NOT_EQUAL);
        User u = userRepository.findUserById(ThreadLocalUtil.getCurrentUserId()).orElseThrow(
                ()-> new BusinessException(ResponseCode.USER_NOT_FOUND)
        );
        if(checkPassword(newPwd, u.getPasswordHash())) throw  new BusinessException(ResponseCode.PASSWORD_TWO_PASSWORD_MUST_DIFFERENT);
        if(!checkPassword(oldPwd, u.getPasswordHash())) throw new BusinessException(ResponseCode.USERNAME_OR_PASSWORD_INCORRECT);
        userRepository.updatePassword(u.getId(), PasswordUtil.encryptPassword(newPwd));
    }

    @Override
    public Set<Permission> getUserPermissions(long userId) {
        if(userId != ThreadLocalUtil.getCurrentUserId()){
           // TODO: check permission
        }
        List<Role> roles = userRoleRepo.findByUserId(userId).stream().map(UserRole::getRole).toList();

        Set<Permission> rolePermission = roles.stream()
                .flatMap(role-> roleService.getPermissions(role.getId()).stream())
                .collect(Collectors.toSet());

        Set<Permission> userPermission = userPermRepo.findByUserId(userId).stream()
                .map(UserPermission::getPermission).collect(Collectors.toSet());
        HashSet<Permission> permissions = new HashSet<>();
        permissions.addAll(rolePermission);
        permissions.addAll(userPermission);
        return permissions;
    }
    @Transactional
    protected boolean findUserAndUpdateStatus(long userId, AccountStatus status) {
        return findUserAndUpdate(userId, user -> {
            user.setAccountStatus(status);
            user.setStatusChangedAt(Instant.now());
            user.setStatusChangeReason("Status changed to " + status.name());
        });
    }

    private boolean findUserAndUpdate(long userId, Consumer<User> updater) {
        return userRepository.findById(userId).map(user -> {
            updater.accept(user);
            userRepository.save(user);
            return true;
        }).orElse(false);
    }
}
