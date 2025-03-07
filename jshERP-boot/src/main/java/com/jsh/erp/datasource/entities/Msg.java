package com.jsh.erp.datasource.entities;

import java.util.Date;

public class Msg {
    private Long id;

    private String msgTitle;

    private String msgContent;

    private Date createTime;

    private String type;

    private Long userId;

    private String status;

    private Long tenantId;

    private String deleteFlag;
    private String recoverContent;
    private String recoverFile;
    private String projectStatus;
    private Long inOutItemId;


    public String getRecoverContent() {
        return recoverContent;
    }

    public void setRecoverContent(String recoverContent) {
        this.recoverContent = recoverContent;
    }

    public String getRecoverFile() {
        return recoverFile;
    }

    public void setRecoverFile(String recoverFile) {
        this.recoverFile = recoverFile;
    }

    public Long getInOutItemId() {
        return inOutItemId;
    }

    public void setInOutItemId(Long inOutItemId) {
        this.inOutItemId = inOutItemId;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMsgTitle() {
        return msgTitle;
    }

    public void setMsgTitle(String msgTitle) {
        this.msgTitle = msgTitle == null ? null : msgTitle.trim();
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent == null ? null : msgContent.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag == null ? null : deleteFlag.trim();
    }

    public String getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(String projectStatus) {
        this.projectStatus = projectStatus;
    }
}