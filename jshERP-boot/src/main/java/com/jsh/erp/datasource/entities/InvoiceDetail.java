package com.jsh.erp.datasource.entities;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 开票记录详情表
 *
 * @author ysok
 * @since 2025-04-26 09:50:01
 */
@Data
@TableName("jsh_invoice_detail")
public class InvoiceDetail implements Serializable{

    private static final long serialVersionUID=1L;

    /**
     *  主键
     */
    private Long id;

    /**
     *  关联开票记录ID
     */
    private Long invoiceRecordId;

    /**
     *  服务名称
     */
    private String serviceName;

    /**
     *  税率
     */
    private Float taxRate;

    /**
     *  含税价
     */
    private Float taxIncludedPrice;

    /**
     *  不含税价
     */
    private Float taxExcludedPrice;

    /**
     *  区局
     */
    private String districtBureau;

    /**
     *  开票时间
     */
    private Date ticketTime;

    /**
     *  承包方式
     */
    private String contractMethod;

    /**
     *  项目负责人ID
     */
    private Long projectLeaderId;

    /**
     *  审核材料金额
     */
    private Float auditMaterialAmount;

    /**
     *  审价费
     */
    private Float valuationFee;

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
     *  创建人
     */
    private Long creator;

    /**
     *  更新时间
     */
    private Date updateTime;

    /**
     *  更新人
     */
    private Long updater;

}