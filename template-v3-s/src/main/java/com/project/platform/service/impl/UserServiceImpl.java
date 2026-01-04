package com.project.platform.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONObject;
import com.project.platform.dto.CurrentUserDTO;
import com.project.platform.dto.RetrievePasswordDTO;
import com.project.platform.dto.UpdatePasswordDTO;
import com.project.platform.entity.User;
import com.project.platform.entity.UserAddress;
import com.project.platform.exception.CustomException;
import com.project.platform.mapper.UserAddressMapper;
import com.project.platform.mapper.UserMapper;
import com.project.platform.service.UserService;
import com.project.platform.utils.CurrentUserThreadLocal;
import com.project.platform.vo.PageVO;

import jakarta.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserAddressMapper userAddressMapper;

    @Value("${resetPassword}")
    private String resetPassword;

    /**
     * 登录
     * 
     * @param username 用户名
     * @param password 密码
     * @return 返回
     */
    @Override
    public CurrentUserDTO login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null || !user.getPasswordHash().equals(password)) {
            throw new CustomException("用户名或密码错误");
        }
        if (user.getIsActive() == 0) {
            throw new CustomException("用户已禁用");
        }
        CurrentUserDTO currentUserDTO = new CurrentUserDTO();
        BeanUtils.copyProperties(user, currentUserDTO);
        // 更新最后登录时间
        user.setLastLogin(LocalDateTime.now());
        userMapper.updateById(user);
        return currentUserDTO;
    }

    /**
     * 注册
     * 
     * @param date 日期
     */
    @Override
    public void register(JSONObject date) {
        User user = new User();
        // 确保username不为空
        String username = date.getString("username");
        if (username == null || username.trim().isEmpty()) {
            throw new CustomException("用户名不能为空");
        }
        user.setUsername(username);
        // 确保password不为空
        String password = date.getString("password");
        if (password == null || password.trim().isEmpty()) {
            throw new CustomException("密码不能为空");
        }
        user.setPasswordHash(password);
        // 设置默认头像
        user.setAvatarUrl("/static/默认头像.webp");
        // 确保phone不为空
        String phone = date.getString("phone");
        if (phone == null || phone.trim().isEmpty()) {
            throw new CustomException("电话不能为空");
        }
        user.setPhone(phone);
        // 确保email不为空
        String email = date.getString("email");
        if (email == null || email.trim().isEmpty()) {
            throw new CustomException("邮箱不能为空");
        }
        user.setEmail(email);
        // 设置状态
        user.setIsActive(1);
        user.setIsAdmin(0);
        // 设置时间
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        insert(user);
    }

    @Override
    public void updateCurrentUserInfo(CurrentUserDTO currentUserDTO) {
        User user = userMapper.selectById(currentUserDTO.getId());
        user.setId(currentUserDTO.getId());
        user.setAvatarUrl(currentUserDTO.getAvatarUrl());
        user.setPhone(currentUserDTO.getTel());
        user.setEmail(currentUserDTO.getEmail());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    public void updateCurrentUserPassword(UpdatePasswordDTO updatePassword) {
        // 用户修改
        User user = userMapper.selectById(CurrentUserThreadLocal.getCurrentUser().getId());
        if (!user.getPasswordHash().equals(updatePassword.getOldPassword())) {
            throw new CustomException("旧密码不正确");
        }
        user.setPasswordHash(updatePassword.getNewPassword());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);

    }

    @Override
    public void resetPassword(Integer id) {
        User user = userMapper.selectById(id);
        user.setPasswordHash(resetPassword);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);

    }

    @Override
    public void retrievePassword(RetrievePasswordDTO retrievePasswordDTO) {
        // 忘记密码
        User user = userMapper.selectByPhone(retrievePasswordDTO.getTel());
        if (user == null) {
            throw new CustomException("手机号不存在");
        }
        user.setPasswordHash(retrievePasswordDTO.getPassword());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);

    }

    @Override
    public User selectById(Integer id) {
        return userMapper.selectById(id);
    }

    @Override
    public User selectByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public void insert(User entity) {
        check(entity);
        if (entity.getPasswordHash() == null) {
            entity.setPasswordHash(resetPassword);
        }
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
        if (entity.getUpdatedAt() == null) {
            entity.setUpdatedAt(LocalDateTime.now());
        }
        if (entity.getIsActive() == null) {
            entity.setIsActive(1);
        }
        if (entity.getIsAdmin() == null) {
            entity.setIsAdmin(0);
        }
        userMapper.insert(entity);
    }

    @Override
    public void updateById(User entity) {
        check(entity);
        userMapper.updateById(entity);
    }

    @Override
    public void removeByIds(List<Integer> ids) {
        userMapper.removeByIds(ids);

    }

    /**
     * 分页模糊查询
     * 
     * @param query    查询
     * @param pageNum  页数
     * @param pageSize 页大小
     * @return 返回
     */
    @Override
    public PageVO<User> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<User> page = new PageVO<>();
        // 获取列表数据
        List<User> list = userMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);

        page.setList(list);
        // 获取分页数据
        page.setTotal(userMapper.queryCount(query));

        return page;
    }

    /**
     * 列表返回
     * 
     * @return 返回
     */
    @Override
    public List<User> list() {
        return userMapper.list();
    }

    private void check(User entity) {
        // 判断用户名是否重复
        User user = userMapper.selectByUsername(entity.getUsername());
        if (user != null && !Objects.equals(user.getId(), entity.getId())) {
            throw new CustomException("用户名已存在");
        }
        // 判断邮箱是否重复
        User emailUser = userMapper.selectByEmail(entity.getEmail());
        if (emailUser != null && !Objects.equals(emailUser.getId(), entity.getId())) {
            throw new CustomException("邮箱已存在");
        }
        // 判断电话是否重复
        User phoneUser = userMapper.selectByPhone(entity.getPhone());
        if (phoneUser != null && !Objects.equals(phoneUser.getId(), entity.getId())) {
            throw new CustomException("电话已存在");
        }
    }

    /**
     * 获取当前登录用户ID
     */
    private Integer getCurrentUserId() {
        CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        if (currentUser == null) {
            throw new CustomException("未登录");
        }
        return currentUser.getId();
    }

    @Override
    public List<UserAddress> getAddressList() {
        return userAddressMapper.listByUserId(getCurrentUserId());
    }

    @Override
    public void addAddress(UserAddress address) {
        address.setUserId(getCurrentUserId());
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            userAddressMapper.clearDefault(getCurrentUserId());
        }
        userAddressMapper.insert(address);
    }

    @Override
    public void updateAddress(UserAddress address) {
        Integer userId = getCurrentUserId();
        UserAddress existing = userAddressMapper.selectByIdAndUserId(address.getId(), userId);
        if (existing == null) {
            throw new CustomException("地址不存在或无权限操作");
        }
        address.setUserId(userId);
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            userAddressMapper.clearDefault(userId);
        }
        userAddressMapper.update(address);
    }

    @Override
    public void deleteAddress(Integer id) {
        int rows = userAddressMapper.delete(id, getCurrentUserId());
        if (rows == 0) {
            throw new CustomException("地址不存在或无权限删除");
        }
    }

    @Override
    public void setDefaultAddress(Integer id) {
        Integer userId = getCurrentUserId();
        UserAddress addr = userAddressMapper.selectByIdAndUserId(id, userId);
        if (addr == null) {
            throw new CustomException("地址不存在或无权限设置");
        }
        userAddressMapper.clearDefault(userId);
        UserAddress temp = new UserAddress();
        temp.setId(id);
        temp.setUserId(userId);
        temp.setIsDefault(1);
        userAddressMapper.update(temp);
    }
}
