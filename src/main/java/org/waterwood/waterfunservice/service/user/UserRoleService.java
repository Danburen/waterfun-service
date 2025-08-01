package org.waterwood.waterfunservice.service.user;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.DTO.common.ApiResponse;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.entity.permission.Role;
import org.waterwood.waterfunservice.entity.permission.UserRole;
import org.waterwood.waterfunservice.entity.user.User;
import org.waterwood.waterfunservice.repository.UserRoleRepo;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserRoleService {
    private final UserRoleRepo userRoleRepo;
    private final RoleService roleService;
    private final UserService userService;

    public UserRoleService(UserRoleRepo urr, RoleService rs, UserService us) {
       this.userRoleRepo = urr;
       this.roleService = rs;
       this.userService = us;
    }

    public List<Role> getUserRoles(long userId) {
        return userRoleRepo.findByUserId(userId).stream().map(UserRole::getRole).collect(Collectors.toList());
    }
    public List<User> getRoleUsers(int roleId) {
        return userRoleRepo.findByRoleId(roleId).stream().map(UserRole::getUser).collect(Collectors.toList());
    }

    @Transactional
    public ApiResponse<Void> addUserRole(long userId, int roleId){
        return userService.getUserById(userId).map(user ->
                roleService.getRole(roleId).map(role->{
                    UserRole userRole = new UserRole();
                    userRole.setUser(user);
                    userRole.setRole(role);
                    userRoleRepo.save(userRole);
                    return ApiResponse.accept();
        }).orElse(ApiResponse.failure(ResponseCode.ROLE_NOT_FOUND)))
                .orElse(ApiResponse.failure(ResponseCode.USER_NOT_FOUND));
    }

    @Transactional
    public ApiResponse<Void> removeUserRole(long userId, int roleId) {
        return userService.getUserById(userId).map(user->
                roleService.getRole(roleId).map(role->{
                    if(!userRoleRepo.existsByUserIdAndRoleId(userId, roleId)){
                        return ApiResponse.fail(ResponseCode.ROLE_NOT_FOUND,
                                "User " + user.getId() + " does not have role " + roleId);
                    }
                    userRoleRepo.deleteByUserIdAndRoleId(userId, roleId);
                    return ApiResponse.accept();
                }).orElse(ApiResponse.failure(ResponseCode.ROLE_NOT_FOUND)))
                .orElse(ApiResponse.failure(ResponseCode.USER_NOT_FOUND));
    }
}
