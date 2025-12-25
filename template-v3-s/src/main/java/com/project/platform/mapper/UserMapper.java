package com.project.platform.mapper;

import com.project.platform.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

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
    @Select("SELECT * FROM user")
    List<User> list();

    /**
     * 根据用户名查询
     * @param username
     * @return
     */
    @Select("SELECT * FROM user WHERE username =#{username}")
    // @Select("SELECT * FROM user WHERE username LIKE CONCAT('%',#{username},'%'")
    User selectByUsername(String username);

    /**
     * 根据id 查询
     * @param id
     * @return
     */
    @Select("SELECT * FROM user WHERE id =#{id}")
    User selectById(Integer id);

    /**
     * 根据电话查询
     * @param tel
     * @return
     */
    @Select("SELECT * FROM user WHERE tel =#{tel}")
    User selectByTel(String tel);

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
