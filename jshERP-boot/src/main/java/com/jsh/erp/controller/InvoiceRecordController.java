package com.jsh.erp.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsh.erp.constants.BusinessTypeEnum;
import com.jsh.erp.datasource.entities.AuditRecord;
import com.jsh.erp.datasource.entities.InvoiceDetail;
import com.jsh.erp.datasource.entities.InvoiceRecord;
import com.jsh.erp.datasource.entities.Supplier;
import com.jsh.erp.datasource.mappers.AuditRecordMapper;
import com.jsh.erp.datasource.mappers.InvoiceDetailMapper;
import com.jsh.erp.datasource.mappers.InvoiceRecordMapper;
import com.jsh.erp.datasource.vo.InvoiceRecordSearch;
import com.jsh.erp.datasource.vo.InvoiceRecordVo;
import com.jsh.erp.datasource.vo.QueryVo;
import com.jsh.erp.service.audit.AuditRecordService;
import com.jsh.erp.service.user.UserService;
import com.jsh.erp.utils.Constants;
import com.jsh.erp.utils.ErpInfo;
import com.jsh.erp.utils.ResponseCode;
import com.jsh.erp.utils.StringUtil;
import lombok.val;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.jsh.erp.service.InvoiceRecordService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.jsh.erp.utils.ResponseJsonUtil.backJson;
import static com.jsh.erp.utils.ResponseJsonUtil.returnJson;

/**
 * 开票记录表
 *
 * @author ysok
 * @since 2025-04-25 21:33:55
 */
@RestController
@RequestMapping("/api/invoiceRecord")
public class InvoiceRecordController {

    @Autowired
    private InvoiceRecordMapper invoiceRecordMapper;

    @Autowired
    private InvoiceDetailMapper invoiceDetailMapper;

    @Autowired
    private AuditRecordService auditRecordService;

    @Autowired
    private UserService userService;

    /**
     * 分页查询
     *
     * @param query
     * @return
     */
    @PostMapping("/findPage")
    public String findPage(@RequestBody QueryVo query) throws Exception {
        String search = query.getSearch();
        InvoiceRecordSearch params = JSON.parseObject(search, InvoiceRecordSearch.class);

        Page<InvoiceRecord> page = new Page<>(query.getCurrentPage(), query.getPageSize());
        LambdaQueryWrapper<InvoiceRecord> queryWrapper = Wrappers.lambdaQuery();

        queryWrapper.like(StringUtil.isNotEmpty(params.getRemark()), InvoiceRecord::getRemark, params.getRemark());

        queryWrapper.eq(params.getProjectId()!= null, InvoiceRecord::getProjectId, params.getProjectId());
        queryWrapper.eq(params.getSupplierId()!= null, InvoiceRecord::getSupplierId, params.getSupplierId());
        queryWrapper.eq(StringUtil.isNotEmpty(params.getInvoiceNumber()), InvoiceRecord::getInvoiceNumber, params.getInvoiceNumber());
        queryWrapper.eq(StringUtil.isNotEmpty(params.getStatus()), InvoiceRecord::getStatus, params.getStatus());
        queryWrapper.ge(params.getBeginTime() != null, InvoiceRecord::getInvoiceDate, params.getBeginTime());
        queryWrapper.le(params.getEndTime() != null, InvoiceRecord::getInvoiceDate, params.getEndTime());

        IPage<InvoiceRecord> list = invoiceRecordMapper.selectPage(page, queryWrapper);
        IPage<InvoiceRecordVo> result = new Page<>();
        List<InvoiceRecordVo> resultList = new ArrayList<>();

        var ids = list.getRecords().stream().map(InvoiceRecord::getId).collect(Collectors.toList());

        var auditList = auditRecordService.getByBusinessTypeAndIds( BusinessTypeEnum.Invoice_Record, ids);
        var userList = userService.getUserListByIds(StringUtil.listToIds(ids));
        for (InvoiceRecord record : list.getRecords()) {
            var vo = new InvoiceRecordVo(record);
            var audit = auditList.stream().filter(a -> a.getBusinessId().equals(record.getId())).findFirst().orElse(null);
            if (audit != null) {
                vo.setAuditor(audit.getAuditor());
                vo.setAuditTime(audit.getAuditTime());
            }
            userList.stream().filter(u -> u.getId().equals(record.getUpdater())).findFirst().ifPresent(user -> vo.setUserName(user.getUsername()));
            resultList.add(vo);
        }
        result.setTotal(list.getTotal());
        result.setRecords(resultList);

        return backJson(result, ErpInfo.OK.name, ErpInfo.OK.code);
    }


    /**
     * 查询
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public String findById(@PathVariable("id") Long id) {
        InvoiceRecord invoiceRecord = invoiceRecordMapper.selectById(id);
        var items = invoiceDetailMapper.selectList(Wrappers.<InvoiceDetail>lambdaQuery().eq(InvoiceDetail::getInvoiceRecordId, id));
        var result = new InvoiceRecordVo(invoiceRecord);
        result.setItems(items);
        return backJson(new ResponseCode(ErpInfo.OK.code, result));
    }

//
//    /**
//     * 新增
//     *
//     * @param invoiceRecord
//     * @return
//     */
//    @PostMapping
//    public ResponseEntity<Boolean> insert( @RequestBody Supplier.InvoiceRecord invoiceRecord) {
//        boolean result = invoiceRecordService.insert(invoiceRecord);
//        return ResponseEntity.ok(result);
//    }
//
//    /**
//     * 修改
//     *
//     * @param invoiceRecord
//     * @return
//     */
//    @PutMapping
//    public ResponseEntity<Boolean> update( @RequestBody Supplier.InvoiceRecord invoiceRecord) {
//        boolean result = invoiceRecordService.update(invoiceRecord);
//        return ResponseEntity.ok(result);
//    }
//
//    /**
//     * 删除
//     *
//     * @param id
//     * @return
//     */
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Integer> delete(@PathVariable("id") Long id) {
//        int result = invoiceRecordService.delete(id);
//        return ResponseEntity.ok(result);
//    }

}