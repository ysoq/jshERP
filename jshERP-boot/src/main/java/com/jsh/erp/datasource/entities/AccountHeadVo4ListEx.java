package com.jsh.erp.datasource.entities;

import lombok.Data;
import java.util.Date;


@Data
public class AccountHeadVo4ListEx extends AccountHead {

    private String organName;

    private String handsPersonName;

    private String userName;

    private String accountName;

    private String billTimeStr;
    private String projectName;
    private Long inOutItemId;

    private String auditor;
    private String auditTime;
}