package com.project.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.platform.entity.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 购物车主表Mapper
 */
@Mapper
public interface CartMapper extends BaseMapper<Cart> {
    /**
     * 根据用户ID查询购物车（已登录用户）
     */
    Cart selectByUserId(@Param("userId") Integer userId);

    /**
     * 根据会话ID查询购物车（未登录用户）
     */
    Cart selectBySessionId(@Param("sessionId") String sessionId);
}