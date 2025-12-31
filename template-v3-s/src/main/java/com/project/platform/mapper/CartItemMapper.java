package com.project.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.platform.entity.CartItem;
import com.project.platform.vo.CartItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 购物车项明细表Mapper
 */
@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {
    /**
     * 根据购物车ID查询购物车项（关联商品信息，返回VO）
     */
    List<CartItemVO> selectCartItemVOByCartId(@Param("cartId") Integer cartId);

    /**
     * 根据购物车项ID和用户ID查询（用于权限校验）
     */
    CartItem selectByIdAndUserId(@Param("id") Integer cartItemId, @Param("userId") Integer userId);

    /**
     * 更新购物车项选中状态
     */
    int updateSelectedStatus(@Param("id") Integer cartItemId, @Param("selected") boolean selected);
}