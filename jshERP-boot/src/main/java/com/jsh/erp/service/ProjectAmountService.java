package com.jsh.erp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsh.erp.datasource.entities.InvoiceRecord;
import com.jsh.erp.datasource.entities.ProjectAmount;
import com.jsh.erp.datasource.mappers.InvoiceRecordMapper;
import com.jsh.erp.datasource.mappers.ProjectAmountMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
public class ProjectAmountService extends ServiceImpl<ProjectAmountMapper, ProjectAmount> implements IService<ProjectAmount> {

    @Autowired
    private ProjectAmountMapper projectAmountMapper;


    public List<ProjectAmount> getProjectAmountByProjectList(List<Long> projectIds){
        LambdaQueryWrapper<ProjectAmount> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ProjectAmount::getProjectId, projectIds);
        queryWrapper.eq(ProjectAmount::getDeleteFlag, 0);
        queryWrapper.eq(ProjectAmount::getStatus, "1");

        List<ProjectAmount> projectAmountList = projectAmountMapper.selectList(queryWrapper);
        return projectAmountList;
    }
}
