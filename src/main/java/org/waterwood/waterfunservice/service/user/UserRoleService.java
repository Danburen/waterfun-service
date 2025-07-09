package org.waterwood.waterfunservice.service.user;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.entity.permission.Role;
import org.waterwood.waterfunservice.entity.permission.UserRole;
import org.waterwood.waterfunservice.entity.user.User;
import org.waterwood.waterfunservice.repository.UserRoleRepo;
import org.waterwood.waterfunservice.service.dto.OpResult;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserRoleService {
    @Autowired
    private UserRoleRepo userRoleRepo;
    @Autowired
    RoleService roleService;
    @Autowired
    UserService userService;
    public List<Role> getUserRoles(long userId) {
        return userRoleRepo.findByUserId(userId).stream().map(UserRole::getRole).collect(Collectors.toList());
    }
    public List<User> getRoleUsers(int roleId) {
        return userRoleRepo.findByRoleId(roleId).stream().map(UserRole::getUser).collect(Collectors.toList());
    }

    @Transactional
    public OpResult<Void> addUserRole(long userId,int roleId){
        return userService.getUserById(userId).map(user ->
                roleService.getRole(roleId).map(role->{
                    UserRole userRole = new UserRole();
                    userRole.setUser(user);
                    userRole.setRole(role);
                    userRoleRepo.save(userRole);
                    return OpResult.success();
        }).orElse(OpResult.failure(ResponseCode.ROLE_NOT_FOUND)))
                .orElse(OpResult.failure(ResponseCode.USER_NOT_FOUND));
    }

    @Transactional
    public OpResult<Void> removeUserRole(long userId, int roleId) {
        return userService.getUserById(userId).map(user->
                roleService.getRole(roleId).map(role->{
                    if(!userRoleRepo.existsByUserIdAndRoleId(userId, roleId)){
                        return OpResult.failure(ResponseCode.ROLE_NOT_FOUND,
                                "User " + user.getId() + " does not have role " + roleId);
                    }
                    userRoleRepo.deleteByUserIdAndRoleId(userId, roleId);
                    return OpResult.success();
                }).orElse(OpResult.failure(ResponseCode.ROLE_NOT_FOUND)))
                .orElse(OpResult.failure(ResponseCode.USER_NOT_FOUND));
    }
}
