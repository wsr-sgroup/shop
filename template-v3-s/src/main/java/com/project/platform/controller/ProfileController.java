package com.project.platform.controller;

import com.project.platform.dto.CurrentUserDTO;
import com.project.platform.dto.UpdatePasswordDTO;
import com.project.platform.entity.UserAddress;
import com.project.platform.service.UserService;
import com.project.platform.utils.CurrentUserThreadLocal;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {

    @Resource
    private UserService userService;

    // 获取个人信息
    @GetMapping
    public ResponseVO<CurrentUserDTO> getProfile() {
        return ResponseVO.ok(CurrentUserThreadLocal.getCurrentUser());
    }

    // 修改个人信息（昵称、头像、电话、邮箱）
    @PutMapping
    public ResponseVO<?> updateProfile(@RequestBody CurrentUserDTO dto) {
        dto.setId(CurrentUserThreadLocal.getCurrentUser().getId());
        userService.updateCurrentUserInfo(dto);
        return ResponseVO.ok("更新成功");
    }

    // 修改密码
    @PostMapping("/change-password")
    public ResponseVO<?> changePassword(@RequestBody UpdatePasswordDTO dto) {
        userService.updateCurrentUserPassword(dto);
        return ResponseVO.ok("密码修改成功");
    }

    // 获取地址列表
    @GetMapping("/addresses")
    public ResponseVO<List<UserAddress>> getAddresses() {
        return ResponseVO.ok(userService.getAddressList());
    }

    // 新增地址
    @PostMapping("/addresses")
    public ResponseVO<?> addAddress(@RequestBody UserAddress address) {
        userService.addAddress(address);
        return ResponseVO.ok("添加成功");
    }

    // 修改地址
    @PutMapping("/addresses")
    public ResponseVO<?> updateAddress(@RequestBody UserAddress address) {
        userService.updateAddress(address);
        return ResponseVO.ok("修改成功");
    }

    // 删除地址
    @DeleteMapping("/addresses/{id}")
    public ResponseVO<?> deleteAddress(@PathVariable Integer id) {
        userService.deleteAddress(id);
        return ResponseVO.ok("删除成功");
    }

    // 设置默认地址
    @PatchMapping("/addresses/{id}/default")
    public ResponseVO<?> setDefault(@PathVariable Integer id) {
        userService.setDefaultAddress(id);
        return ResponseVO.ok("设置默认成功");
    }
}