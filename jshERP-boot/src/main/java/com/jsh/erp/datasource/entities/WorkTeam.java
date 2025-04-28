package com.jsh.erp.datasource.entities;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 班组信息表
 *
 * @author ysok
 * @since 2025-04-27 18:58:54
 */
@Data
@TableName("jsh_work_team")
public class WorkTeam implements Serializable{

    private static final long serialVersionUID=1L;

    /**
     *  主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     *  班组名称
     */
    private String teamName;

    /**
     *  联系人
     */
    private String contactPerson;

    /**
     *  联系电话
     */
    private String phone;

    /**
     *  备注
     */
    private String remark;


    /**
     *  删除标记(0-未删除 1-已删除)
     */
    private String deleteFlag;
    /**
     *  禁用(0-启用 1-禁用)
     */
    private String status;
    /**
     *  更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     *  更新人
     */
    private Long updater;

    private Long tenantId;

}