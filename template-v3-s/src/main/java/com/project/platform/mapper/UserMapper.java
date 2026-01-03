package com.project.platform.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.project.platform.entity.User;

public interface UserMapper {

    /**
     * 分页模糊查询
     * @param offset
     * @param pageSize
     * @param query
     * @return
     */
    List<User> queryPage(@Param("offset")  Integer offset,@Param("pageSize") Integer pageSize, @Param("query") Map<String,Object> query);

    /**
     * 查询总数
     * @param query
     * @return
     */
    int queryCount(@Param("query") Map<String,Object> query);
    /**
     * 查询全部
     * @return
     */
    @Select("SELECT id, username, password_hash, avatar_url as avatarUrl, phone, email, is_active, is_admin, last_login, created_at, updated_at FROM users")
    List<User> list();

    /**
     * 根据用户名查询
     * @param username
     * @return
     */
    @Select("SELECT id, username, password_hash, avatar_url as avatarUrl, phone, email, is_active, is_admin, last_login, created_at, updated_at FROM users WHERE username =#{username}")
    User selectByUsername(String username);

    /**
     * 根据id 查询
     * @param id
     * @return
     */
    @Select("SELECT id, username, password_hash, avatar_url as avatarUrl, phone, email, is_active, is_admin, last_login, created_at, updated_at FROM users WHERE id =#{id}")
    User selectById(Integer id);

    /**
     * 根据电话查询
     * @param phone
     * @return
     */
    @Select("SELECT id, username, password_hash, avatar_url as avatarUrl, phone, email, is_active, is_admin, last_login, created_at, updated_at FROM users WHERE phone =#{phone}")
    User selectByTel(String phone);

    /**
     * 新增
     * @param user
     * @return
     */
    int insert(User user);

    /**
     * 编辑
     * @param entity
     * @return
     */
    int updateById(User entity);

    /**
     * 删除
     * @param ids
     * @return
     */
    boolean removeByIds(List<Integer> ids);
}
