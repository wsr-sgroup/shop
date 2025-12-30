package com.project.platform.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String sku;
    private String name;
    private String subtitle;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private BigDecimal costPrice;
    private Integer stock;
    private Integer stockLowThreshold;
    private String mainImage;
    private String imageGallery; // JSON
    private String specifications; // JSON
    private String series;
    private String model;
    private String cpu;
    private String gpu;
    private String ram;
    private String storage;
    private BigDecimal screenSize;
    private BigDecimal weight;
    private Integer isOnSale;
    private Integer isFeatured;
    private Integer isHot;
    private Integer salesCount;
    private Integer viewCount;
    private BigDecimal ratingAvg;
    private Integer reviewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}