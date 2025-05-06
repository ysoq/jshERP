package com.jsh.erp.datasource.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsh.erp.datasource.entities.DataRecycle;
import org.apache.ibatis.annotations.Mapper;
/**
 * 数据回收站表
 *
 * @author ysok
 * @since 2025-05-06 10:19:02
 *
 */
@Mapper
public interface DataRecycleMapper extends BaseMapper<DataRecycle> {

}