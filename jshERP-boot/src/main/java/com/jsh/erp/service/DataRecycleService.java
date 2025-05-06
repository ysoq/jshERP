package com.jsh.erp.service;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsh.erp.datasource.entities.DataRecycle;
import com.jsh.erp.datasource.mappers.DataRecycleMapper;
import com.jsh.erp.service.user.UserService;
import com.jsh.erp.utils.Tools;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ysok
 * @since 2025-05-06 10:19:02
 */
@Service
@Slf4j
public class DataRecycleService extends ServiceImpl<DataRecycleMapper, DataRecycle> {

    @Autowired
    private DataRecycleMapper dataRecycleMapper;

    @Autowired
    private UserService userService;

    public int insertRecycle(Object data, Long originalId, Long newId) throws Exception {
        var dataRecycle = new DataRecycle();
        dataRecycle.setDataJson(JSON.toJSONString(data));
        dataRecycle.setOriginalId(originalId);
        dataRecycle.setNewId(newId);
        dataRecycle.setOperatorId(userService.getCurrentUser().getId());
        dataRecycle.setOperateTime(Tools.getNowDate());
        return dataRecycleMapper.insert(dataRecycle);
    }
}
