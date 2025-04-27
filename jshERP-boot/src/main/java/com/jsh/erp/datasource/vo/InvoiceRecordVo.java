package com.jsh.erp.datasource.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
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
        this.setRemark(record.getRemark());
        this.setDeleteFlag(record.getDeleteFlag());
        this.setCreateTime(record.getCreateTime());
        this.setUpdateTime(record.getUpdateTime());
        this.setUpdater(record.getUpdater());
        this.setFileName(record.getFileName());
    }

    private List<InvoiceDetail> items;

    private String auditor;     // 审核员
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date auditTime;

    public InvoiceRecord to() {
        InvoiceRecord record = new InvoiceRecord();
        record.setId(this.getId());
        record.setProjectId(this.getProjectId());
        record.setInvoiceDate(this.getInvoiceDate());
        record.setInvoiceNumber(this.getInvoiceNumber());
        record.setSupplierId(this.getSupplierId());
        record.setAccountId(this.getAccountId());
        record.setTaxAmount(this.getTaxAmount());
        record.setStatus(this.getStatus());
        record.setRemark(this.getRemark());
        record.setDeleteFlag(this.getDeleteFlag());
        record.setCreateTime(this.getCreateTime());
        record.setUpdateTime(this.getUpdateTime());
        record.setUpdater(this.getUpdater());
        record.setFileName(this.getFileName());
        return record;
    }
}
