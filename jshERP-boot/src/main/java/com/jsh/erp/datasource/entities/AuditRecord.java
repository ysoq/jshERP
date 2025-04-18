package com.jsh.erp.datasource.entities;


import com.jsh.erp.constants.BusinessTypeEnum;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class AuditRecord {
    private Long id;
    private BusinessTypeEnum businessType; // 业务类型枚举
    private Long businessId;    // 业务ID
    private String auditor;     // 审核员
    private Date auditTime;    // 审核时间
    private String deleteFlag ; // 删除状态

    public String getAuditTimeStr() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(auditTime);
    }
}