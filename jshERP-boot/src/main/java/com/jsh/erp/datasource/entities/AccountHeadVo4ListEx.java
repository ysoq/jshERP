package com.jsh.erp.datasource.entities;

import java.math.BigDecimal;
import java.util.Date;

public class AccountHeadVo4ListEx extends AccountHead{

    private String organName;

    private String handsPersonName;

    private String userName;

    private String accountName;

    private String billTimeStr;
    private String projectName;
    private Long inOutItemId;

    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
    }

    public String getHandsPersonName() {
        return handsPersonName;
    }

    public void setHandsPersonName(String handsPersonName) {
        this.handsPersonName = handsPersonName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getBillTimeStr() {
        return billTimeStr;
    }

    public void setBillTimeStr(String billTimeStr) {
        this.billTimeStr = billTimeStr;
    }

    public Long getInOutItemId() {
        return inOutItemId;
    }

    public void setInOutItemId(Long inOutItemId) {
        this.inOutItemId = inOutItemId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}