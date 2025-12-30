package com.project.platform.vo;


import lombok.Data;

import java.util.List;

@Data
public class PageVO<T> {
    List<T> list;
    long total;
    int pageNum;
    int pageSize;

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    
}