package com.jsh.erp.datasource.vo;

import lombok.Data;

@Data
public class QueryVo {
    private String column;
    private Integer currentPage;
    private String field;
    private String order;
    private Integer pageSize;
    private String search;
}
