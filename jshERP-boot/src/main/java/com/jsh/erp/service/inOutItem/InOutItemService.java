package com.jsh.erp.service.inOutItem;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.constants.BusinessTypeEnum;
import com.jsh.erp.constants.ExceptionConstants;
import com.jsh.erp.datasource.entities.*;
import com.jsh.erp.datasource.mappers.*;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.service.InvoiceRecord.InvoiceRecordServiceImpl;
import com.jsh.erp.service.ProjectAmountService;
import com.jsh.erp.service.audit.AuditRecordService;
import com.jsh.erp.service.log.LogService;
import com.jsh.erp.service.user.UserService;
import com.jsh.erp.utils.StringUtil;
import com.jsh.erp.utils.Tools;
import lombok.var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InOutItemService {
    private Logger logger = LoggerFactory.getLogger(InOutItemService.class);

    @Resource
    private InOutItemMapper inOutItemMapper;

    @Resource
    private InOutItemMapperEx inOutItemMapperEx;

    @Resource
    private InOutItemFlowMapper inOutItemFlowMapper;
    @Resource
    private UserService userService;
    @Resource
    private LogService logService;
    @Resource
    private AccountItemMapperEx accountItemMapperEx;

    @Autowired
    private InvoiceRecordServiceImpl invoiceRecordServiceImpl;

    @Autowired
    private ProjectAmountMapper projectAmountMapper;
    @Autowired
    private WorkTeamMapper workTeamMapper;
    @Autowired
    private AuditRecordService auditRecordService;
    @Autowired
    private ProjectAmountService projectAmountService;

    public InOutItem getInOutItem(long id) throws Exception {
        InOutItem result = null;
        try {
            result = inOutItemMapper.selectByPrimaryKey(id);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<InOutItem> getInOutItemListByIds(String ids) throws Exception {
        List<Long> idList = StringUtil.strToLongList(ids);
        List<InOutItem> list = new ArrayList<>();
        try {
            InOutItemExample example = new InOutItemExample();
            example.createCriteria().andIdIn(idList);
            list = inOutItemMapper.selectByExample(example);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    public List<InOutItem> getInOutItem() throws Exception {
        InOutItemExample example = new InOutItemExample();
        example.createCriteria().andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<InOutItem> list = null;
        try {
            list = inOutItemMapper.selectByExample(example);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    public List<InOutItem> select(
            String name,
            String type,
            String remark,
            String code,
            String manager,
            String supplierId,
            int offset, int rows) throws Exception {
        List<InOutItem> list = null;
        try {

            if (StringUtil.isEmpty(manager)) {
                User user = userService.getCurrentUser();
                String roleType = userService.getRoleTypeByUserId(user.getId()).getType(); //角色类型
                if (BusinessConstants.ROLE_TYPE_PRIVATE.equals(roleType)) {
                    manager = user.getId().toString();
                }
            }

            list = inOutItemMapperEx.selectByConditionInOutItem(name, type, remark, offset, rows, null, manager,
                    code, supplierId
            );

            var projectAmountList = projectAmountService.getProjectAmountByProjectList(list.stream().map(InOutItem::getId).collect(Collectors.toList()));
            for (InOutItem item : list) {
                var projectAmounts = projectAmountList.stream().filter(projectAmount -> projectAmount.getProjectId().equals(item.getId())).collect(Collectors.toList());
                item.setProjectAmount(projectAmounts.stream().map(ProjectAmount::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            }

            invoiceRecordServiceImpl.assignTaxInclusiveInvoiceAmount(list);

        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    public Long countInOutItem(String name, String type, String remark) throws Exception {
        Long result = null;
        try {
            String manager = null;
            User user = userService.getCurrentUser();
            String roleType = userService.getRoleTypeByUserId(user.getId()).getType(); //角色类型
            if (BusinessConstants.ROLE_TYPE_PRIVATE.equals(roleType)) {
                manager = user.getId().toString();
            }

            result = inOutItemMapperEx.countsByInOutItem(name, type, remark, manager);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertInOutItem(JSONObject obj, HttpServletRequest request) throws Exception {
        InOutItem inOutItem = JSONObject.parseObject(obj.toJSONString(), InOutItem.class);
        verifyNameAndCode(inOutItem.getId(), inOutItem.getName(), "");
        int result = 0;
        try {
            inOutItem.setEnabled(true);
            inOutItemMapper.insertSelective(inOutItem);
            List<InOutItem> list = this.findBySelect("");
            result = Math.toIntExact(list.get(0).getId());
            logService.insertLog("收支项目",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_ADD).append(inOutItem.getName()).toString(), request);
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    private void verifyNameAndCode(Long id, String name, String code) throws Exception {
        int count = checkIsNameExist(id, name);
        if (count > 0) {
            throw new Exception("名称已存在");
        }
        if (StringUtil.isNotEmpty(code)) {
            count = checkIsCodeExist(id, code);
        }
        if (count > 0) {
            throw new Exception("编号已存在");
        }
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateInOutItem(JSONObject obj, HttpServletRequest request) throws Exception {
        InOutItem inOutItem = JSONObject.parseObject(obj.toJSONString(), InOutItem.class);
        verifyNameAndCode(inOutItem.getId(), inOutItem.getName(), inOutItem.getCode());
        int result = 0;
        try {
            result = inOutItemMapper.updateByPrimaryKeySelective(inOutItem);
            logService.insertLog("收支项目",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT).append(inOutItem.getName()).toString(), request);
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int deleteInOutItem(Long id, HttpServletRequest request) throws Exception {
        return batchDeleteInOutItemByIds(id.toString());
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteInOutItem(String ids, HttpServletRequest request) throws Exception {
        return batchDeleteInOutItemByIds(ids);
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteInOutItemByIds(String ids) throws Exception {
        int result = 0;
        String[] idArray = ids.split(",");
        //校验财务子表	jsh_accountitem
        List<AccountItem> accountItemList = null;
        try {
            accountItemList = accountItemMapperEx.getAccountItemListByInOutItemIds(idArray);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        if (accountItemList != null && accountItemList.size() > 0) {
            logger.error("异常码[{}],异常提示[{}],参数,InOutItemIds[{}]",
                    ExceptionConstants.DELETE_FORCE_CONFIRM_CODE, ExceptionConstants.DELETE_FORCE_CONFIRM_MSG, ids);
            throw new BusinessRunTimeException(ExceptionConstants.DELETE_FORCE_CONFIRM_CODE,
                    ExceptionConstants.DELETE_FORCE_CONFIRM_MSG);
        }
        //校验通过执行删除操作
        StringBuffer sb = new StringBuffer();
        sb.append(BusinessConstants.LOG_OPERATION_TYPE_DELETE);
        List<InOutItem> list = getInOutItemListByIds(ids);
        for (InOutItem inOutItem : list) {
            sb.append("[").append(inOutItem.getName()).append("]");
        }
        logService.insertLog("收支项目", sb.toString(),
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
        User userInfo = userService.getCurrentUser();
        try {
            result = inOutItemMapperEx.batchDeleteInOutItemByIds(new Date(), userInfo == null ? null : userInfo.getId(), idArray);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    public int checkIsNameExist(Long id, String name) throws Exception {
        InOutItemExample example = new InOutItemExample();
        example.createCriteria().andIdNotEqualTo(id).andNameEqualTo(name).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<InOutItem> list = null;
        try {
            list = inOutItemMapper.selectByExample(example);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }

        return list == null ? 0 : list.size();
    }

    public int checkIsCodeExist(Long id, String code) throws Exception {
        InOutItemExample example = new InOutItemExample();
        example.createCriteria().andIdNotEqualTo(id).andCodeEqualTo(code).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<InOutItem> list = null;
        try {
            list = inOutItemMapper.selectByExample(example);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }

        return list == null ? 0 : list.size();
    }

    public List<InOutItem> findBySelect(String type) throws Exception {
        LambdaQueryWrapper<InOutItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InOutItem::getEnabled, true)
                .ne(InOutItem::getDeleteFlag, BusinessConstants.DELETE_FLAG_DELETED);
        /*
        InOutItemExample example = new InOutItemExample();
        if (type.equals("excludeFinish")) {
            example.createCriteria()
                    .addStatusNotEqualTo("1")
        } else if (type.equals("clearPackage")) {
            example.createCriteria()
                    .andTypeEqualTo("清包")
        } else {
            example.createCriteria()
        }
        example.setOrderByClause("sort asc, id desc");
        * */

        if (type.equals("excludeFinish")) {
            // 不等于1，等于null需要查询到
            queryWrapper.ne(InOutItem::getStatus, "1");
        } else if (type.equals("clearPackage")) {
            queryWrapper.eq(InOutItem::getType, "清包");
        } else if (type.equals("hasCode")) {
            queryWrapper.isNotNull(InOutItem::getCode);
        }
        try {
            queryWrapper.eq(InOutItem::getTenantId, userService.getCurrentUser().getTenantId());
        } catch (Exception e) {

        }

        queryWrapper.orderByAsc(InOutItem::getSort)
                .orderByDesc(InOutItem::getId);

        List<InOutItem> list = null;
        try {
            list = inOutItemMapper.selectList(queryWrapper);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchSetStatus(String status, String ids) throws Exception {
        logService.insertLog("收支项目",
                new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_ENABLED).toString(),
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
        List<Long> inOutItemIds = StringUtil.strToLongList(ids);
        InOutItem inOutItem = new InOutItem();
        if (status.equals("examine")) {
            inOutItem.setStatus("1");
        } else if (status.equals("counter-audit")) {
            inOutItem.setStatus("0");
        } else if (status.equals("enable")) {
            inOutItem.setEnabled(true);
        } else {
            inOutItem.setEnabled(false);
        }

        InOutItemExample example = new InOutItemExample();
        example.createCriteria().andIdIn(inOutItemIds);
        int result = 0;
        try {
            result = inOutItemMapper.updateByExampleSelective(inOutItem, example);
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    public List<InOutItemFlow> projectFlow(Long id) {
        var list = inOutItemMapperEx.selectInOutItemByFlow(id);

        var invoiceList = invoiceRecordServiceImpl.getListByProjectId(id);
        for (var invoice : invoiceList) {
            var inOutItemFlow = new InOutItemFlow();
            inOutItemFlow.setCreateTime(Tools.dateToStr(invoice.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            inOutItemFlow.setHeaderId(invoice.getId());
            inOutItemFlow.setNumber(invoice.getInvoiceNumber());
            inOutItemFlow.setType("发票");
            inOutItemFlow.setTotalPrice(invoice.getTaxAmount());
            list.add(inOutItemFlow);
        }

        LambdaQueryWrapper<ProjectAmount> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(ProjectAmount::getProjectId, id)
                .eq(ProjectAmount::getDeleteFlag, 0)
                .eq(ProjectAmount::getStatus, "1");

        var projectAmountList = projectAmountMapper.selectList(queryWrapper);
        if (projectAmountList != null && !projectAmountList.isEmpty()) {
            var ids = projectAmountList.stream().map(ProjectAmount::getId).collect(Collectors.toList());
            var auditList = auditRecordService.getByBusinessTypeAndIds(BusinessTypeEnum.PROJECT_AMOUNT, ids);

            for (var projectAmount : projectAmountList) {
                LambdaQueryWrapper<WorkTeam> workTeamQueryWrapper = Wrappers.lambdaQuery();
                workTeamQueryWrapper.eq(WorkTeam::getId, projectAmount.getTeamId());
                var workTeam = workTeamMapper.selectOne(workTeamQueryWrapper);
                var audit = auditList.stream().filter(a -> a.getBusinessId().equals(projectAmount.getId())).findFirst().orElse(null);
                var flow = new InOutItemFlow();
                if (audit != null) {
                    flow.setCreateTime(Tools.dateToStr(audit.getAuditTime(), "yyyy-MM-dd HH:mm:ss"));
                }
                flow.setHeaderId(projectAmount.getId());
                flow.setTotalPrice(projectAmount.getAmount());
                flow.setSubType("金额分配");
                flow.setType(workTeam.getTeamName());
                list.add(flow);
            }
        }
        return list;
    }
}
