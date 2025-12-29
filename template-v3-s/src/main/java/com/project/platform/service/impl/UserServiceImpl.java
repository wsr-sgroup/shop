package com.project.platform.service.impl;

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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        if (user == null || !user.getPassword().equals(password)) {
            throw new CustomException("用户名或密码错误");
        }
        if ("禁用".equals(user.getStatus())) {
            throw new CustomException("用户已禁用");
        }
        CurrentUserDTO currentUserDTO = new CurrentUserDTO();
        BeanUtils.copyProperties(user, currentUserDTO);
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
        user.setUsername(date.getString("username"));
        user.setPassword(date.getString("password"));
        user.setNickname(date.getString("nickname"));
        user.setAvatarUrl(date.getString("avatarUrl"));
        // 设置时间
        user.setCreate_time(LocalDateTime.now());
        // 设置用户状态
        user.setStatus("启用");
        insert(user);
    }

    @Override
    public void updateCurrentUserInfo(CurrentUserDTO currentUserDTO) {
        User user = userMapper.selectById(currentUserDTO.getId());
        user.setId(currentUserDTO.getId());
        user.setNickname(currentUserDTO.getNickname());
        user.setAvatarUrl(currentUserDTO.getAvatarUrl());
        user.setTel(currentUserDTO.getTel());
        user.setEmail(currentUserDTO.getEmail());
        userMapper.updateById(user);
    }

    @Override
    public void updateCurrentUserPassword(UpdatePasswordDTO updatePassword) {
        // 用户修改
        User user = userMapper.selectById(CurrentUserThreadLocal.getCurrentUser().getId());
        if (!user.getPassword().equals(updatePassword.getOldPassword())) {
            throw new CustomException("旧密码不正确");
        }
        user.setPassword(updatePassword.getNewPassword());
        userMapper.updateById(user);

    }

    @Override
    public void resetPassword(Integer id) {
        User user = userMapper.selectById(id);
        user.setPassword(resetPassword);
        userMapper.updateById(user);

    }

    @Override
    public void retrievePassword(RetrievePasswordDTO retrievePasswordDTO) {
        // 忘记密码
        User user = userMapper.selectByTel(retrievePasswordDTO.getTel());
        if (user == null) {
            throw new CustomException("手机号不存在");
        }
        user.setPassword(retrievePasswordDTO.getPassword());
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
        entity.setCreate_time(LocalDateTime.now());
        if (entity.getPassword() == null) {
            entity.setPassword(resetPassword);
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
