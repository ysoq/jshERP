package com.jsh.erp.datasource.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsh.erp.datasource.entities.InvoiceRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 开票记录表
 *
 * @author ysok
 * @since 2025-04-25 21:33:55
 *
 */
@Mapper
public interface InvoiceRecordMapper extends BaseMapper<InvoiceRecord> {

}
