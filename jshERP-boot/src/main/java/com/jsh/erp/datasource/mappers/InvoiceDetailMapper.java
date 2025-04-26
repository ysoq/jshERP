package com.jsh.erp.datasource.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsh.erp.datasource.entities.InvoiceDetail;
import org.apache.ibatis.annotations.Mapper;
/**
 * 开票记录详情表
 *
 * @author ysok
 * @since 2025-04-26 09:50:01
 *
 */
@Mapper
public interface InvoiceDetailMapper extends BaseMapper<InvoiceDetail> {

}