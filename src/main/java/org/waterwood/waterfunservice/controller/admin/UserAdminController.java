package org.waterwood.waterfunservice.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.waterwood.waterfunservice.dto.request.user.PatchUserRoleReq;
import org.waterwood.waterfunservice.dto.request.user.UpdateUserRoleReq;
import org.waterwood.waterfunservice.dto.request.user.AssignUserRoleReq;
import org.waterwood.waterfunservice.dto.response.comm.ApiResponse;
import org.waterwood.waterfunservice.service.user.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/user")
public class UserAdminController {
    private final UserService userService;

    @PostMapping("/{id}/roles")
    public ApiResponse<Void> assignRoleToUser(@PathVariable long id, @Valid @RequestBody AssignUserRoleReq body){
        userService.assignRoles(id, body.getUserRoleItemDtos());
        return ApiResponse.success();
    }

    @PutMapping("/{id}/roles")
    public ApiResponse<Void> updateRoleToUser(@PathVariable long id, @Valid @RequestBody UpdateUserRoleReq body){
        userService.replace(id, body.getUserRoleItemDtos());
        return ApiResponse.success();
    }

    @PatchMapping("/{id}/roles")
    public ApiResponse<Void> patchRoleToUser(@PathVariable long id, @Valid @RequestBody PatchUserRoleReq body){
        userService.change(id, body.getAdds(), body.getDeletePermIds());
        return ApiResponse.success();
    }
}
