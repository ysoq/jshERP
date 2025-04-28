package com.jsh.erp.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsh.erp.constants.BusinessTypeEnum;
import com.jsh.erp.datasource.entities.*;
import com.jsh.erp.datasource.mappers.InOutItemMapper;
import com.jsh.erp.datasource.mappers.InOutItemMapperEx;
import com.jsh.erp.datasource.mappers.ProjectAmountMapper;
import com.jsh.erp.datasource.vo.InvoiceRecordVo;
import com.jsh.erp.datasource.vo.ProjectAmountSearch;
import com.jsh.erp.datasource.vo.ProjectAmountVO;
import com.jsh.erp.datasource.vo.QueryVo;
import com.jsh.erp.service.audit.AuditRecordService;
import com.jsh.erp.service.msg.MsgService;
import com.jsh.erp.service.user.UserService;
import com.jsh.erp.utils.ErpInfo;
import com.jsh.erp.utils.StringUtil;
import com.jsh.erp.utils.Tools;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.jsh.erp.utils.ResponseJsonUtil.backJson;
import static com.jsh.erp.utils.ResponseJsonUtil.returnJson;

/**
 * @author ysok
 * @since 2025-04-28 13:05:13
 */
@RestController
@RequestMapping("/api/projectAmount")
public class ProjectAmountController {

    @Autowired
    private ProjectAmountMapper projectAmountMapper;

    @Autowired
    private InOutItemMapper inOutItemMapper;


    @Autowired
    private UserService userService;


    @Autowired
    private AuditRecordService auditRecordService;

    @Autowired
    private InOutItemMapperEx inOutItemMapperEx;

    /**
     * 分页查询
     *
     * @param query
     * @return
     */
    @PostMapping("/findPage")
    public String findPage(@RequestBody QueryVo query) throws Exception {
        String search = query.getSearch();
        var params = JSON.parseObject(search, ProjectAmountSearch.class);

        Page<ProjectAmount> page = new Page<>(query.getCurrentPage(), query.getPageSize());
        LambdaQueryWrapper<ProjectAmount> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(ProjectAmount::getDeleteFlag, 0);
        queryWrapper.eq(ProjectAmount::getTenantId, userService.getTenantId());

        if (StringUtil.isNotEmpty(params.getProjectCode()) || StringUtil.isNotEmpty(params.getProjectName())) {
            var projectQuery = Wrappers.<InOutItem>lambdaQuery();
            projectQuery.eq(StringUtil.isNotEmpty(params.getProjectCode()), InOutItem::getCode, params.getProjectCode());
            projectQuery.eq(StringUtil.isNotEmpty(params.getProjectName()), InOutItem::getName, params.getProjectName());

            var projectList = inOutItemMapper.selectList(projectQuery);
            if (projectList.size() > 0) {
                queryWrapper.in(ProjectAmount::getProjectId, projectList.stream().map(InOutItem::getId).collect(Collectors.toList()));
            } else {
                return backJson(new Page<>(), ErpInfo.OK.name, ErpInfo.OK.code);
            }
        }

        queryWrapper.eq(params.getTeamId() != null, ProjectAmount::getTeamId, params.getTeamId());

        queryWrapper.ge(params.getBeginTime() != null, ProjectAmount::getUpdateTime, params.getBeginTime());
        queryWrapper.le(params.getEndTime() != null, ProjectAmount::getUpdateTime, Tools.getDate235959(params.getEndTime()));

        queryWrapper.ge(params.getAuditBeginTime() != null, ProjectAmount::getUpdateTime, params.getAuditBeginTime());
        queryWrapper.le(params.getAuditEndTime() != null, ProjectAmount::getUpdateTime, Tools.getDate235959(params.getAuditEndTime()));

        queryWrapper.eq(StringUtil.isNotEmpty(params.getRemark()), ProjectAmount::getRemark, params.getRemark());
        queryWrapper.eq(StringUtil.isNotEmpty(params.getStatus()), ProjectAmount::getStatus, params.getStatus());

        queryWrapper.orderByDesc(ProjectAmount::getUpdateTime);

        var list = projectAmountMapper.selectPage(page, queryWrapper);

        IPage<ProjectAmountVO> result = new Page<>();
        List<ProjectAmountVO> resultList = new ArrayList<>();

        if (!list.getRecords().isEmpty()) {
            var ids = list.getRecords().stream().map(ProjectAmount::getId).collect(Collectors.toList());
            var auditList = auditRecordService.getByBusinessTypeAndIds(BusinessTypeEnum.PROJECT_AMOUNT, ids);
            for (var record : list.getRecords()) {
                var vo = new ProjectAmountVO(record);
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
     * 新增
     */
    @PostMapping("/insert")
    public String insert(@RequestBody ProjectAmount data) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();

        if (isProjectAmountExists(data.getProjectId(), data.getTeamId(), null)) {
            return returnJson(objectMap, "该项目已分配过金额，请勿重复分配！", ErpInfo.ERROR.code);
        }
        data.setId(null);
        data.setDeleteFlag("0");
        data.setUpdater(userService.getCurrentUser().getId());
        data.setUpdateTime(new Date());

        projectAmountMapper.insert(data);

        if ("1".equals(data.getStatus())) {
            auditRecordService.batchCreateRecord(BusinessTypeEnum.PROJECT_AMOUNT, data.getId());
        }

        return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public String update(@RequestBody ProjectAmount data) throws Exception {
        var oldRecord = projectAmountMapper.selectById(data.getId());
        Map<String, Object> objectMap = new HashMap<>();

        if (oldRecord == null) {
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
        if (isProjectAmountExists(data.getProjectId(), data.getTeamId(), data.getId())) {
            return returnJson(objectMap, "该项目已分配过金额，请勿重复分配！", ErpInfo.ERROR.code);
        }
        oldRecord.setProjectId(data.getProjectId());
        oldRecord.setAmount(data.getAmount());
        oldRecord.setRemark(data.getRemark());
        oldRecord.setTeamId(data.getTeamId());

        oldRecord.setUpdater(userService.getCurrentUser().getId());
        oldRecord.setUpdateTime(new Date());

        projectAmountMapper.updateById(oldRecord);

        if ("0".equals(oldRecord.getStatus()) && "1".equals(data.getStatus())) {
            auditRecordService.batchCreateRecord(BusinessTypeEnum.PROJECT_AMOUNT, oldRecord.getId());
        }
        return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
    }

    @PostMapping("/batchSetStatus")
    public String batchSetStatus(@RequestBody JSONObject jsonObject) throws Exception {
        String status = jsonObject.getString("status");
        var ids = jsonObject.getString("ids").split(",");
        Map<String, Object> objectMap = new HashMap<>();
        String whereStatus = "0".equals(status) ? "1" : "1".equals(status) ? "0" : "2".equals(status) ? "0" : "-100";
        var where = Wrappers.<ProjectAmount>lambdaQuery().in(ProjectAmount::getId, ids).eq(ProjectAmount::getStatus, whereStatus);

        var list = projectAmountMapper.selectList(where);
        if (!list.isEmpty()) {
            for (var record : list) {
                record.setStatus(status);
                projectAmountMapper.updateById(record);
            }
            if ("0".equals(status)) {
                auditRecordService.batchDelete(BusinessTypeEnum.PROJECT_AMOUNT, list.stream().map(ProjectAmount::getId).collect(Collectors.toList()));
            } else if ("1".equals(status)) {
                auditRecordService.batchCreateRecords(BusinessTypeEnum.PROJECT_AMOUNT, list.stream().map(ProjectAmount::getId).collect(Collectors.toList()));
            }
        }

        return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
    }

    @DeleteMapping("/delete")
    private Boolean isProjectAmountExists(Long projectId, Long teamId, Long id) {
        LambdaQueryWrapper<ProjectAmount> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(ProjectAmount::getTenantId, userService.getTenantId());
        queryWrapper.eq(ProjectAmount::getProjectId, projectId);
        queryWrapper.eq(ProjectAmount::getDeleteFlag, 0);

        // 添加 (team_id = 4 OR status = 1) 的条件
        queryWrapper.and(
                wq -> wq.eq(ProjectAmount::getTeamId, teamId).or().eq(ProjectAmount::getStatus, "1")
        );

        int count = projectAmountMapper.selectCount(queryWrapper);
        return count > 0;
    }

}