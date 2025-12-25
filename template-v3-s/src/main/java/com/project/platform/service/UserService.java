package com.project.platform.service;

import com.project.platform.entity.User;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

public interface UserService extends CommonService{

    PageVO<User> page(Map<String,Object> query,Integer pageNum,Integer pageSize);
    /**
     * 列表返回
     * @return 返回
     */
    List<User> list();

    /**
     * 通过id查询
     * @param id id
     * @return 返回
     */
    User selectById(Integer id);

    /**
     * 通过用户名查询
     * @param username 用户名
     * @return 返回
     */
    User selectByUsername(String username);

    /**
     * 新增
     * @param user 用户
     */
    void insert(User user);

    /**
     * 编辑
     * @param entity 编辑
     */
    void updateById(User entity);

    /**
     * 删除
     * @param ids ids
     */
    void removeByIds(List<Integer> ids);
}

