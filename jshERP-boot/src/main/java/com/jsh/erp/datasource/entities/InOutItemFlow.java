package com.jsh.erp.datasource.entities;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class InOutItemFlow {
    private Long projectId;
    private Long headerId;
    private String projectName;
    private String type;
    private String subType;
    private String number;
    private BigDecimal totalPrice;
    private String createTime;
    private Long tenantId;
}