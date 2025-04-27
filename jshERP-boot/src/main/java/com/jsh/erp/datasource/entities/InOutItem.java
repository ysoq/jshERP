package com.jsh.erp.datasource.entities;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("jsh_in_out_item")
public class InOutItem implements Serializable {

    private static final long serialVersionUID=1L;
    private Long id;

    private String name;

    private String type;

    private String remark;

    private Boolean enabled;

    private String sort;

    private Long tenantId;

    private String deleteFlag;

    private String code;
    private Long manager;
    private String fileList;



    private Date finishTime;

    private String status;

    private Long supplierId;
    private BigDecimal contractPrice;

    @TableField(exist = false)
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
}