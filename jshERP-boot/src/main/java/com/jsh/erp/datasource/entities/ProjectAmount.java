package com.jsh.erp.datasource.entities;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 *
 *
 * @author ysok
 * @since 2025-04-28 13:05:13
 */
@Data
@TableName("jsh_project_amount")
public class ProjectAmount implements Serializable{

    private static final long serialVersionUID=1L;

    /**
     *  主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     *  项目ID
     */
    private Long projectId;

    /**
     *  分配金额
     */
    private Float amount;

    /**
     *  状态(0-未审核 1-已审核)
     */
    private String status;

    /**
     *  备注
     */
    private String remark;

    /**
     *  删除标记(0-未删除 1-已删除)
     */
    private String deleteFlag;

    /**
     *  更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     *  更新人
     */
    private Long updater;

    /**
     *  租户id
     */
    private Long tenantId;

    /**
     *  班组id
     */
    private Long teamId;



}