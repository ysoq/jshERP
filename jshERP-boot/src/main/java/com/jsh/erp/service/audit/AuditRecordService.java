package com.jsh.erp.service.audit;

import com.jsh.erp.constants.BusinessTypeEnum;
import com.jsh.erp.datasource.entities.AuditRecord;
import com.jsh.erp.datasource.mappers.AuditRecordMapper;
import com.jsh.erp.service.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditRecordService {
    @Resource
    private AuditRecordMapper auditRecordMapper;

    @Resource
    private UserService userService;

    public List<AuditRecord> getByBusinessTypeAndIds(BusinessTypeEnum businessType, List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        return auditRecordMapper.selectByBusinessTypeAndIds(businessType.getType(), ids);
    }

    @Transactional
    public int batchCreateRecord(BusinessTypeEnum businessType, Long businessId) throws Exception {
        List<Long> ids = new ArrayList<>();
        ids.add(businessId);
        return batchCreateRecords(businessType, ids);
    }

    @Transactional
    public int batchCreateRecords(BusinessTypeEnum businessType, List<Long> businessIds) throws Exception {
        String username = userService.getCurrentUser().getUsername();
        Date now = new Date();

        List<AuditRecord> records = businessIds.stream().map(id -> {
            AuditRecord record = new AuditRecord();
            record.setBusinessType(businessType);
            record.setBusinessId(id);
            record.setAuditor(username);
            record.setAuditTime(now);
            return record;
        }).collect(Collectors.toList());

        return auditRecordMapper.batchInsertSelective(records);
    }

    @Transactional
    public int batchDelete(BusinessTypeEnum businessType, List<Long> ids) {
        return auditRecordMapper.deleteByBusinessTypeAndIds(businessType.getType(), ids);
    }
}