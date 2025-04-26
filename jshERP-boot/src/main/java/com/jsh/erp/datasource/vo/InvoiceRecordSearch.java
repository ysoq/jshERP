package com.jsh.erp.datasource.vo;

import lombok.Data;

import java.util.Date;

@Data
public class InvoiceRecordSearch {
    /**
     *  项目ID
     */
    private Long projectId;

    /**
     *  来往单位ID
     */
    private Long supplierId;

    /**
     *  单据编号
     */
    private String invoiceNumber;

    /**
     *  状态(0-未审核 1-已审核)
     */
    private String status;

    /**
     *  备注
     */
    private String remark;

    private Date beginTime;
    private Date endTime;
}
