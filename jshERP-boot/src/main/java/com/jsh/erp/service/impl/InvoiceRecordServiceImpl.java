package com.jsh.erp.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsh.erp.datasource.entities.InvoiceRecord;
import com.jsh.erp.datasource.mappers.InvoiceRecordMapper;
import com.jsh.erp.datasource.vo.InvoiceRecordSearch;
import com.jsh.erp.datasource.vo.QueryVo;
import com.jsh.erp.service.InvoiceRecordService;
import com.jsh.erp.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ysok
 * @since 2025-04-25 21:33:55
 */
@Service
@Slf4j
public class InvoiceRecordServiceImpl extends ServiceImpl<InvoiceRecordMapper, InvoiceRecord> implements InvoiceRecordService {

//    @Autowired
//    private InvoiceRecordMapper invoiceRecordMapper;

//    @Override
//    public IPage<InvoiceRecord> findPage(QueryVo query) {
//        String search = query.getSearch();
//        InvoiceRecordSearch params = JSON.parseObject(search, InvoiceRecordSearch.class);
//
//        Page<InvoiceRecord> page = new Page<>(query.getCurrentPage(), query.getPageSize());
//        LambdaQueryWrapper<InvoiceRecord> queryWrapper = Wrappers.lambdaQuery();
//
//        queryWrapper.like(StringUtil.isNotEmpty(params.getRemark()), InvoiceRecord::getRemark, params.getRemark());
//
//        queryWrapper.eq(params.getProjectId()!= null, InvoiceRecord::getProjectId, params.getProjectId());
//        queryWrapper.eq(params.getSupplierId()!= null, InvoiceRecord::getSupplierId, params.getSupplierId());
//        queryWrapper.eq(StringUtil.isNotEmpty(params.getInvoiceNumber()), InvoiceRecord::getInvoiceNumber, params.getInvoiceNumber());
//        queryWrapper.eq(StringUtil.isNotEmpty(params.getStatus()), InvoiceRecord::getStatus, params.getStatus());
//        queryWrapper.ge(params.getBeginTime() != null, InvoiceRecord::getInvoiceDate, params.getBeginTime());
//        queryWrapper.le(params.getEndTime() != null, InvoiceRecord::getInvoiceDate, params.getEndTime());
//
//        return invoiceRecordMapper.selectPage(page, queryWrapper);
//    }

//    @Override
//    public InvoiceRecord findById(Long id) {
//       // 查询主表并且查询子表信息
//        InvoiceRecord invoiceRecord = invoiceRecordMapper.selectById(id);
//        invoiceRecord
//    }
//
//    @Override
//    public boolean insert(Supplier.InvoiceRecord invoiceRecord) {
//        return save(invoiceRecord);
//    }
//
//    @Override
//    public boolean update(Supplier.InvoiceRecord invoiceRecord) {
//        return updateById(invoiceRecord);
//    }
//
//    @Override
//    public int delete(Long id) {
//        return invoiceRecordMapper.deleteById(id);
//    }

}