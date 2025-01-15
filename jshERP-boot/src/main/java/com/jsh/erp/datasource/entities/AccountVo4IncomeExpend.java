package com.jsh.erp.datasource.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AccountVo4IncomeExpend {
    private Long id;
    private Long headerId;
    private String billNo;
    private String remark;
    private String username;
    private String billTime;
    private String type;
    private Long inOutItemId;
    private BigDecimal eachAmount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHeaderId() {
        return headerId;
    }

    public void setHeaderId(Long headerId) {
        this.headerId = headerId;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBillTime() {
        return billTime;
    }

    public void setBillTime(String billTime) {
        this.billTime = billTime;
    }

    public Long getInOutItemId() {
        return inOutItemId;
    }

    public void setInOutItemId(Long inOutItemId) {
        this.inOutItemId = inOutItemId;
    }

    public BigDecimal getEachAmount() {
        return eachAmount;
    }

    public void setEachAmount(BigDecimal eachAmount) {
        this.eachAmount = eachAmount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}