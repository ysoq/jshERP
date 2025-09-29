package com.jsh.erp.datasource.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("jsh_in_out_item")
public class InOutItem implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     *  主键ID
     */

    @TableId(type = IdType.ID_WORKER)
    private Long id;

    private String name;

    private String type;

    private String remark;

    private Boolean enabled;

    private String sort;

    private String deleteFlag;

    private String teamList;

    @TableField("code")
    private String code;

    @TableField("manager")
    private Long manager;

    @TableField("file_list")
    private String fileList;

    @TableField("finish_time")
    private Date finishTime;

    @TableField("status")
    private String status;

    @TableField("supplier_id")
    private Long supplierId;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("contract_price")
    private BigDecimal contractPrice;

    // 父级项目ID
    private Long parentId;

    @TableField(exist = false)
    private String parentName;

    @TableField("project_status")
    private String projectStatus;

    @TableField(exist = false)
    private String username;

    @TableField(exist = false)
    private String phonenum;

    @TableField(exist = false)
    private String supplierName;

    @TableField(exist = false)
    private BigDecimal totalInAccount;

    @TableField(exist = false)
    private BigDecimal totalOutAccount;

    @TableField(exist = false)
    private BigDecimal taxAmount;

    @TableField(exist = false)
    private BigDecimal projectAmount;
}