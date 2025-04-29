package com.jsh.erp.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsh.erp.constants.BusinessTypeEnum;
import com.jsh.erp.datasource.entities.InvoiceDetail;
import com.jsh.erp.datasource.entities.InvoiceRecord;
import com.jsh.erp.datasource.entities.ProjectAmount;
import com.jsh.erp.datasource.entities.WorkTeam;
import com.jsh.erp.datasource.mappers.InvoiceDetailMapper;
import com.jsh.erp.datasource.mappers.InvoiceRecordMapper;
import com.jsh.erp.datasource.vo.InvoiceRecordSearch;
import com.jsh.erp.datasource.vo.InvoiceRecordVo;
import com.jsh.erp.datasource.vo.QueryVo;
import com.jsh.erp.service.audit.AuditRecordService;
import com.jsh.erp.service.user.UserService;
import com.jsh.erp.utils.*;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;
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
        queryWrapper.eq(InvoiceRecord::getDeleteFlag, 0);
        queryWrapper.like(StringUtil.isNotEmpty(params.getRemark()), InvoiceRecord::getRemark, params.getRemark());

        queryWrapper.eq(params.getProjectId() != null, InvoiceRecord::getProjectId, params.getProjectId());
        queryWrapper.eq(params.getSupplierId() != null, InvoiceRecord::getSupplierId, params.getSupplierId());
        queryWrapper.eq(StringUtil.isNotEmpty(params.getInvoiceNumber()), InvoiceRecord::getInvoiceNumber, params.getInvoiceNumber());
        queryWrapper.eq(StringUtil.isNotEmpty(params.getStatus()), InvoiceRecord::getStatus, params.getStatus());
        queryWrapper.ge(params.getBeginTime() != null, InvoiceRecord::getInvoiceDate, params.getBeginTime());
        queryWrapper.le(params.getEndTime() != null, InvoiceRecord::getInvoiceDate, Tools.getDate235959(params.getEndTime()));
        queryWrapper.orderByDesc(InvoiceRecord::getUpdateTime);
        IPage<InvoiceRecord> list = invoiceRecordMapper.selectPage(page, queryWrapper);
        IPage<InvoiceRecordVo> result = new Page<>();
        List<InvoiceRecordVo> resultList = new ArrayList<>();

        if (!list.getRecords().isEmpty()) {
            var ids = list.getRecords().stream().map(InvoiceRecord::getId).collect(Collectors.toList());
            var auditList = auditRecordService.getByBusinessTypeAndIds(BusinessTypeEnum.Invoice_Record, ids);
            for (InvoiceRecord record : list.getRecords()) {
                var vo = new InvoiceRecordVo(record);
                var audit = auditList.stream().filter(a -> a.getBusinessId().equals(record.getId())).findFirst().orElse(null);
                if (audit != null) {
                    vo.setAuditor(audit.getAuditor());
                    vo.setAuditTime(audit.getAuditTime());
                }
                resultList.add(vo);
            }
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


    /**
     * 新增
     */
    @PostMapping("/insert")
    @Transactional
    public String insert(@RequestBody InvoiceRecordVo invoiceRecordVo) throws Exception {

        var invoiceRecord = invoiceRecordVo.to();
        invoiceRecord.setId(null);
        invoiceRecord.setDeleteFlag("0");
        invoiceRecord.setUpdater(userService.getCurrentUser().getId());
        invoiceRecord.setUpdateTime(new Date());
        invoiceRecord.setCreateTime(new Date());

        invoiceRecordMapper.insert(invoiceRecord);

        for (var item : invoiceRecordVo.getItems()) {
            item.setInvoiceRecordId(invoiceRecord.getId());
            invoiceDetailMapper.insert(item);
        }

        if ("1".equals(invoiceRecord.getStatus())) {
            auditRecordService.batchCreateRecord(BusinessTypeEnum.Invoice_Record, invoiceRecord.getId());
        }
        Map<String, Object> objectMap = new HashMap<>();
        return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    @Transactional
    public String update(@RequestBody InvoiceRecordVo invoiceRecordVo) throws Exception {
        InvoiceRecord oldRecord = invoiceRecordMapper.selectById(invoiceRecordVo.getId());
        Map<String, Object> objectMap = new HashMap<>();

        if (oldRecord == null) {
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
        var invoiceRecord = invoiceRecordVo.to();
        invoiceRecord.setUpdater(userService.getCurrentUser().getId());
        invoiceRecord.setUpdateTime(new Date());
        invoiceRecord.setCreateTime(oldRecord.getCreateTime());
        invoiceRecordMapper.updateById(invoiceRecord);

        // 删除原有明细
        invoiceDetailMapper.delete(Wrappers.<InvoiceDetail>lambdaQuery().eq(InvoiceDetail::getInvoiceRecordId, invoiceRecord.getId()));

        for (var item : invoiceRecordVo.getItems()) {
            item.setInvoiceRecordId(invoiceRecord.getId());
            invoiceDetailMapper.insert(item);
        }

        if ("0".equals(oldRecord.getStatus()) && "1".equals(invoiceRecord.getStatus())) {
            auditRecordService.batchCreateRecord(BusinessTypeEnum.Invoice_Record, invoiceRecord.getId());
        }
        return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
    }

    @PostMapping("/batchSetStatus")
    @Transactional
    public String batchSetStatus(@RequestBody JSONObject jsonObject) throws Exception {
        String status = jsonObject.getString("status");
        var ids = jsonObject.getString("ids").split(",");
        Map<String, Object> objectMap = new HashMap<>();
        String whereStatus = "0".equals(status) ? "1" : "1".equals(status) ? "0" : "2".equals(status) ? "0" : "-100";
        var where = Wrappers.<InvoiceRecord>lambdaQuery().in(InvoiceRecord::getId, ids).eq(InvoiceRecord::getStatus, whereStatus);

        var list = invoiceRecordMapper.selectList(where);
        if (!list.isEmpty()) {
            for (var record : list) {
                record.setStatus(status);
                invoiceRecordMapper.updateById(record);
            }
            if ("0".equals(status)) {
                auditRecordService.batchDelete(BusinessTypeEnum.Invoice_Record, list.stream().map(InvoiceRecord::getId).collect(Collectors.toList()));
            } else if ("1".equals(status)) {
                auditRecordService.batchCreateRecords(BusinessTypeEnum.Invoice_Record, list.stream().map(InvoiceRecord::getId).collect(Collectors.toList()));
            }
        }

        return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
    }

    @DeleteMapping("/delete")
    public String delete(@RequestParam Long id) throws Exception {
        return batchDeleteResource(id.toString());
    }

    @DeleteMapping(value = "/deleteBatch")
    public String batchDeleteResource(@RequestParam("ids") String ids) throws Exception {
        var idList = ids.split(",");
        Map<String, Object> objectMap = new HashMap<>();
        var where = Wrappers.<InvoiceRecord>lambdaQuery().in(InvoiceRecord::getId, idList);
        var list = invoiceRecordMapper.selectList(where);

        if (!list.isEmpty()) {
            for (var record : list) {
                record.setDeleteFlag("1");
                record.setUpdateTime(new Date());
                record.setUpdater(userService.getCurrentUser().getId());
                invoiceRecordMapper.updateById(record);
            }
        }

        return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
    }
}