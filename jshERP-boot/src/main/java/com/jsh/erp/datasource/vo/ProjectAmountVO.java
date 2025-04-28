package com.jsh.erp.datasource.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jsh.erp.datasource.entities.ProjectAmount;
import lombok.Data;

import java.util.Date;

@Data
public class ProjectAmountVO extends ProjectAmount {

    public ProjectAmountVO (){}
    public ProjectAmountVO(ProjectAmount projectAmount) {
        this.setId(projectAmount.getId());
        this.setProjectId(projectAmount.getProjectId());
        this.setAmount(projectAmount.getAmount());
        this.setStatus(projectAmount.getStatus());
        this.setRemark(projectAmount.getRemark());
        this.setDeleteFlag(projectAmount.getDeleteFlag());
        this.setUpdateTime(projectAmount.getUpdateTime());
        this.setUpdater(projectAmount.getUpdater());
        this.setTenantId(projectAmount.getTenantId());
        this.setTeamId(projectAmount.getTeamId());
    }

    private String projectCode;
    private String projectName;
    private String auditor;     // 审核员
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date auditTime;
}
