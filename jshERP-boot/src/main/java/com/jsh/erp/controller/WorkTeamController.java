package com.jsh.erp.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsh.erp.datasource.entities.InOutItem;
import com.jsh.erp.datasource.entities.WorkTeam;
import com.jsh.erp.datasource.mappers.WorkTeamMapper;
import com.jsh.erp.datasource.vo.QueryVo;
import com.jsh.erp.service.user.UserService;
import com.jsh.erp.utils.ErpInfo;
import com.jsh.erp.utils.StringUtil;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.jsh.erp.utils.ResponseJsonUtil.backJson;
import static com.jsh.erp.utils.ResponseJsonUtil.returnJson;

/**
 * 班组信息表
 *
 * @author ysok
 * @since 2025-04-27 18:58:54
 */
@RestController
@RequestMapping("/api/workTeam")
public class WorkTeamController {

    @Autowired
    private WorkTeamMapper workTeamMapper;


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
        WorkTeam params = JSON.parseObject(search, WorkTeam.class);

        Page<WorkTeam> page = new Page<>(query.getCurrentPage(), query.getPageSize());
        LambdaQueryWrapper<WorkTeam> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(WorkTeam::getDeleteFlag, 0);
        queryWrapper.like(StringUtil.isNotEmpty(params.getTeamName()), WorkTeam::getTeamName, params.getTeamName());
        queryWrapper.like(StringUtil.isNotEmpty(params.getRemark()), WorkTeam::getRemark, params.getRemark());
        queryWrapper.like(StringUtil.isNotEmpty(params.getContactPerson()), WorkTeam::getContactPerson, params.getContactPerson());

        queryWrapper.eq(WorkTeam::getTenantId, userService.getTenantId());
        queryWrapper.orderByDesc(WorkTeam::getUpdateTime);
        var result = workTeamMapper.selectPage(page, queryWrapper);
        return backJson(result, ErpInfo.OK.name, ErpInfo.OK.code);
    }

    /**
     * 新增
     */
    @PostMapping("/insert")
    public String insert(@RequestBody WorkTeam workTeam) throws Exception {
        if (checkIsNameExist(workTeam.getTeamName(), null)) {
            return returnJson(new HashMap<>(), ErpInfo.NAME_EXIST.name, ErpInfo.NAME_EXIST.code);
        }
        workTeam.setId(null);
        workTeam.setUpdateTime(new Date());
        workTeam.setUpdater(userService.getCurrentUser().getId());
        workTeamMapper.insert(workTeam);
        return returnJson(new HashMap<>(), ErpInfo.OK.name, ErpInfo.OK.code);
    }

    /**
     * 新增
     */
    @PutMapping("/update")
    public String update(@RequestBody WorkTeam workTeam) throws Exception {
        var data = getWorkTeamById(workTeam.getId());
        if (data == null) {
            return returnJson(new HashMap<>(), ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
        if (checkIsNameExist(workTeam.getTeamName(), workTeam.getId())) {
            return returnJson(new HashMap<>(), ErpInfo.NAME_EXIST.name, ErpInfo.NAME_EXIST.code);
        }
        data.setTeamName(workTeam.getTeamName());
        data.setRemark(workTeam.getRemark());
        data.setContactPerson(workTeam.getContactPerson());
        data.setPhone(workTeam.getPhone());

        data.setUpdateTime(new Date());
        data.setUpdater(userService.getCurrentUser().getId());
        workTeamMapper.updateById(data);
        return returnJson(new HashMap<>(), ErpInfo.OK.name, ErpInfo.OK.code);
    }

    @DeleteMapping("/delete")
    public String delete(@RequestParam Long id) throws Exception {
        var data = getWorkTeamById(id);
        if (data == null) {
            return returnJson(new HashMap<>(), ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
        data.setDeleteFlag("1");
        data.setUpdateTime(new Date());
        data.setUpdater(userService.getCurrentUser().getId());
        workTeamMapper.updateById(data);
        return returnJson(new HashMap<>(), ErpInfo.OK.name, ErpInfo.OK.code);
    }


    @PostMapping("/batchSetStatus")
    @Transactional
    public String batchSetStatus(@RequestBody JSONObject jsonObject) throws Exception {
        String status = jsonObject.getString("status");
        var ids = jsonObject.getString("ids").split(",");
        Map<String, Object> objectMap = new HashMap<>();
        String whereStatus = "0".equals(status) ? "1" : "1".equals(status) ? "0" : "2".equals(status) ? "0" : "-100";
        var where = Wrappers.<WorkTeam>lambdaQuery().in(WorkTeam::getId, ids).eq(WorkTeam::getStatus, whereStatus);

        var list = workTeamMapper.selectList(where);
        if (!list.isEmpty()) {
            for (var record : list) {
                record.setStatus(status);
                workTeamMapper.updateById(record);
            }
        }

        return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
    }

    @PostMapping("/findBySelect")
    @Transactional
    public String findBySelect() {
        String res;
        try {
            LambdaQueryWrapper<WorkTeam> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(WorkTeam::getDeleteFlag, 0)
                    .eq(WorkTeam::getStatus, "0")
                    .eq(WorkTeam::getTenantId, userService.getTenantId());

            List<WorkTeam> dataList = workTeamMapper.selectList(queryWrapper);

            //存放数据json数组
            JSONArray dataArray = new JSONArray();
            if (null != dataList) {
                for (var data : dataList) {
                    JSONObject item = new JSONObject();
                    item.put("id", data.getId());
                    item.put("name", data.getTeamName());
                    dataArray.add(item);
                }
            }
            res = dataArray.toJSONString();
        } catch (Exception e) {
            res = "[]";
        }
        return res;
    }


    private Boolean checkIsNameExist(String name, Long id) throws Exception {
        LambdaQueryWrapper<WorkTeam> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(WorkTeam::getTeamName, name);
        queryWrapper.eq(WorkTeam::getDeleteFlag, 0);
        queryWrapper.ne(id != null, WorkTeam::getId, id);

        int result = workTeamMapper.selectCount(queryWrapper);
        return result > 0;
    }

    private WorkTeam getWorkTeamById(Long id) throws Exception {
        var data = workTeamMapper.selectById(id);
        if (data != null && "0".equals(data.getDeleteFlag())) {
            return data;
        } else {
            return null;
        }
    }


}