package com.project.platform.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id; // 使用 Integer 而不是 int，以便它可以为 null
    private String name;
    private String slug;
    private Integer parentId; // 使用 Integer 而不是 int，以便它可以为 null
    private String description;
    private Integer sortOrder;
    private Boolean isVisible; // 使用 Boolean 而不是 int，以便它可以为 null
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
