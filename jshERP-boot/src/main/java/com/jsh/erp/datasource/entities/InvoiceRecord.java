package com.jsh.erp.datasource.entities;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 开票记录表
 *
 * @author ysok
 * @since 2025-04-25 21:33:55
 */
@Data
@TableName("jsh_invoice_record")
public class InvoiceRecord implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     *  主键
     */
    private Long id;

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
     *  收入账户ID
     */
    private Long accountId;

    /**
     *  开票含税金额
     */
    private Float taxAmount;

    /**
     *  单据日期
     */
    private Date invoiceDate;

    /**
     *  状态(0-未审核 1-已审核)
     */
    private String status;

    /**
     *  操作员ID
     */
    private Long operatorId;

    /**
     *  备注
     */
    private String remark;

    /**
     *  删除标记(0-未删除 1-已删除)
     */
    private String deleteFlag;

    /**
     *  创建时间
     */
    private Date createTime;

    /**
     *  更新时间
     */
    private Date updateTime;

    /**
     *  更新人
     */
    private Long updater;


}