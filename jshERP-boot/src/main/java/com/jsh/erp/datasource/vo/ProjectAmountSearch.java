package com.jsh.erp.datasource.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;


@Data
public class ProjectAmountSearch {
    private String projectCode;
    private String projectName;

    /**
     *  班组id
     */
    private Long teamId;

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

    private Date auditBeginTime;
    private Date auditEndTime;
}
