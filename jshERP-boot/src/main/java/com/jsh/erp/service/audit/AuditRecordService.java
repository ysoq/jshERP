package com.jsh.erp.service.audit;

import com.jsh.erp.constants.BusinessTypeEnum;
import com.jsh.erp.datasource.entities.AuditRecord;
import com.jsh.erp.datasource.mappers.AuditRecordMapper;
import com.jsh.erp.service.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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
    public int createRecord(BusinessTypeEnum businessType, String businessId) {
        try {
            AuditRecord record = new AuditRecord();
            record.setBusinessType(businessType);
            record.setBusinessId(Long.parseLong(businessId));
            String username = userService.getCurrentUser().getUsername();
            record.setAuditor(username);
            record.setAuditTime(new Date());
            return auditRecordMapper.insertSelective(record);
        } catch (Exception e) {
            return 0;
        }
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
    public int batchDelete(String businessType, List<Long> ids) {
        return auditRecordMapper.deleteByBusinessTypeAndIds(businessType, ids);
    }
}