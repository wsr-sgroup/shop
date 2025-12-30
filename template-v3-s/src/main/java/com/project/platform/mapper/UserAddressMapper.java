package com.project.platform.mapper;

import com.project.platform.entity.UserAddress;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface UserAddressMapper {

    @Select("SELECT * FROM user_addresses WHERE user_id = #{userId} ORDER BY is_default DESC, id DESC")
    List<UserAddress> listByUserId(Integer userId);

    @Select("SELECT * FROM user_addresses WHERE id = #{id} AND user_id = #{userId}")
    UserAddress selectByIdAndUserId(@Param("id") Integer id, @Param("userId") Integer userId);

    @Insert("INSERT INTO user_addresses (user_id, recipient_name, recipient_phone, province, city, district, " +
            "street_address, postal_code, is_default, tag, created_at, updated_at) " +
            "VALUES (#{userId}, #{recipientName}, #{recipientPhone}, #{province}, #{city}, #{district}, " +
            "#{streetAddress}, #{postalCode}, #{isDefault}, #{tag}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserAddress address);

    @Update("<script>UPDATE user_addresses <set>" +
            "<if test='recipientName != null'>recipient_name = #{recipientName},</if>" +
            "<if test='recipientPhone != null'>recipient_phone = #{recipientPhone},</if>" +
            "<if test='province != null'>province = #{province},</if>" +
            "<if test='city != null'>city = #{city},</if>" +
            "<if test='district != null'>district = #{district},</if>" +
            "<if test='streetAddress != null'>street_address = #{streetAddress},</if>" +
            "<if test='postalCode != null'>postal_code = #{postalCode},</if>" +
            "<if test='isDefault != null'>is_default = #{isDefault},</if>" +
            "<if test='tag != null'>tag = #{tag},</if>" +
            "updated_at = NOW()</set> WHERE id = #{id} AND user_id = #{userId}</script>")
    int update(UserAddress address);

    @Delete("DELETE FROM user_addresses WHERE id = #{id} AND user_id = #{userId}")
    int delete(@Param("id") Integer id, @Param("userId") Integer userId);

    @Update("UPDATE user_addresses SET is_default = 0 WHERE user_id = #{userId}")
    int clearDefault(Integer userId);
}