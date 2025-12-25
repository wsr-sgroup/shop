package com.project.platform.service;

import com.alibaba.fastjson2.JSONObject;
import com.project.platform.dto.CurrentUserDTO;
import com.project.platform.dto.RetrievePasswordDTO;
import com.project.platform.dto.UpdatePasswordDTO;

import java.util.Map;

public interface CommonService<T> {
    /**
     * 登录
     * @param username 用户名
     * @param password 密码
     * @return 返回
     */
    CurrentUserDTO login(String username, String password);

    /**
     * 注册
     * @param data 日期
     */

    void register(JSONObject data);

    /**
     * 更新当前用户信息
     * @param currentUserDTO 当前
     */

    void updateCurrentUserInfo(CurrentUserDTO currentUserDTO);

    /**
     * 修改当前用户密码
     * @param updatePassword 更新
     */

    void updateCurrentUserPassword(UpdatePasswordDTO updatePassword);

    /**
     * 重置密码
     * @param id id
     */
    void resetPassword(Integer id);

    /**
     * 忘记密码
     * @param retrievePasswordDTO 忘记
     */

    void retrievePassword(RetrievePasswordDTO retrievePasswordDTO);

    /**
     * 查询当前用户信息
     * @param id id
     * @return 返回
     */

    T selectById(Integer id);

}
