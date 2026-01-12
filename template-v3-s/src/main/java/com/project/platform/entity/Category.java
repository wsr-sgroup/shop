package com.project.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName(value = "categories")
public class Category implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private String slug;

    private Integer parentId;

    private String description;

    private Integer sortOrder;

    private Boolean isVisible;

    private String image;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}