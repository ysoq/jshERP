package com.jsh.erp.datasource.vo;

import com.jsh.erp.datasource.entities.InvoiceDetail;
import com.jsh.erp.datasource.entities.InvoiceRecord;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class InvoiceRecordVo extends InvoiceRecord {

    public InvoiceRecordVo() {}
    public InvoiceRecordVo(InvoiceRecord record) {
        this.setId(record.getId());
        this.setProjectId(record.getProjectId());
        this.setInvoiceDate(record.getInvoiceDate());
        this.setInvoiceNumber(record.getInvoiceNumber());
        this.setSupplierId(record.getSupplierId());
        this.setAccountId(record.getAccountId());
        this.setTaxAmount(record.getTaxAmount());
        this.setStatus(record.getStatus());
        this.setOperatorId(record.getOperatorId());
        this.setRemark(record.getRemark());
        this.setDeleteFlag(record.getDeleteFlag());
        this.setCreateTime(record.getCreateTime());
        this.setUpdateTime(record.getUpdateTime());
        this.setUpdater(record.getUpdater());
    }

    private List<InvoiceDetail> items;

    private String auditor;     // 审核员
    private Date auditTime;
    private String userName;     // 审核员

}
