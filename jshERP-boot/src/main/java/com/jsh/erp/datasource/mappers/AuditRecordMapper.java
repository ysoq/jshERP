package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.AuditRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AuditRecordMapper {
    List<AuditRecord> selectByBusinessTypeAndIds(@Param("businessType") String businessType,
                                                 @Param("ids") List<Long> ids);
    
    int insertSelective(AuditRecord record);
    
    int batchInsertSelective(List<AuditRecord> records);
    
    int deleteByBusinessTypeAndIds(@Param("businessType") String businessType,
                                 @Param("ids") List<Long> ids);
}