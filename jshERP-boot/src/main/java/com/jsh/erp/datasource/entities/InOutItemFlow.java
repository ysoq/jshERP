package com.jsh.erp.datasource.entities;

import java.math.BigDecimal;
import java.util.Date;

public class InOutItemFlow {
    private Long id;

    private String projectName;
    private String type;

    private String number;

    private BigDecimal totalPrice;

    private Date time;

    private Long tenantId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}