package com.jsh.erp.datasource.entities;


import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 数据回收站表
 *
 * @author ysok
 * @since 2025-05-06 10:19:02
 */
@Data
@TableName("jsh_data_recycle")
public class DataRecycle implements Serializable{

    private static final long serialVersionUID=1L;

    /**
     *  主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     *  被删除数据的JSON格式
     */
    private String dataJson;

    /**
     *  原数据主键ID
     */
    private Long originalId;

    /**
     *  新主键ID
     */
    private Long newId;

    /**
     *  操作人ID
     */
    private Long operatorId;

    /**
     *  操作时间
     */
    private Date operateTime;

}