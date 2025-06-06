package com.jsh.erp.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.datasource.entities.*;
import com.jsh.erp.datasource.mappers.AccountHeadMapperEx;
import com.jsh.erp.datasource.mappers.InOutItemMapperEx;
import com.jsh.erp.service.inOutItem.InOutItemService;
import com.jsh.erp.utils.BaseResponseInfo;
import com.jsh.erp.utils.ErpInfo;
import com.jsh.erp.utils.ResponseCode;
import com.jsh.erp.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.jsh.erp.utils.ResponseJsonUtil.backJson;
import static com.jsh.erp.utils.ResponseJsonUtil.returnJson;

/**
 * @author jishenghua  jshERP 2018年12月25日14:38:08
 */
@RestController
@RequestMapping(value = "/inOutItem")
@Api(tags = {"收支项目"})
public class InOutItemController {
    private Logger logger = LoggerFactory.getLogger(InOutItemController.class);

    @Resource
    private InOutItemService inOutItemService;

    @Resource
    private InOutItemMapperEx inOutItemMapperEx;

    @Resource
    private AccountHeadMapperEx accountHeadMapperEx;

    /**
     * 查找收支项目信息-下拉框
     *
     * @param request
     * @return
     */
    @GetMapping(value = "/findBySelect")
    @ApiOperation(value = "查找收支项目信息")
    public String findBySelect(@RequestParam("type") String type, HttpServletRequest request) throws Exception {
        String res = null;
        try {
            List<InOutItem> dataList = inOutItemService.findBySelect(type);
            //存放数据json数组
            JSONArray dataArray = new JSONArray();
            if (null != dataList) {
                for (InOutItem inOutItem : dataList) {
                    JSONObject item = new JSONObject();
                    item.put("id", inOutItem.getId());
                    //收支项目名称
                    item.put("name", inOutItem.getName());
                    item.put("code", inOutItem.getCode());
                    dataArray.add(item);
                }
            }
            res = dataArray.toJSONString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            res = "获取数据失败";
        }
        return res;
    }

    /**
     * 批量设置状态-启用或者禁用
     *
     * @param jsonObject
     * @param request
     * @return
     */
    @PostMapping(value = "/batchSetStatus")
    @ApiOperation(value = "批量设置状态")
    public String batchSetStatus(@RequestBody JSONObject jsonObject,
                                 HttpServletRequest request) throws Exception {
        String status = jsonObject.getString("status");
        String ids = jsonObject.getString("ids");
        Map<String, Object> objectMap = new HashMap<>();
        int res = inOutItemService.batchSetStatus(status, ids);
        if (res > 0) {
            return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }

    /**
     * 查找收支项目信息-下拉框
     *
     * @param request
     * @return
     */
    @GetMapping(value = "/projectFlow")
    @ApiOperation(value = "查找收支项目流水")
    public String projectFlow(@RequestParam("id") Long id, HttpServletRequest request) throws Exception {
        String res = null;
        try {
            List<InOutItemFlow> dataList = inOutItemService.projectFlow(id);
            return backJson(new ResponseCode(ErpInfo.OK.code, dataList));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            res = "获取数据失败";
        }
        return res;
    }


    @GetMapping(value = "/projectBonus")
    @ApiOperation(value = "查找项目奖金分配")
    public BaseResponseInfo projectBonus(@RequestParam(value = "projectId", required = false) Long projectId) {
        // 查询所有已完成或者以开票项目  id
        List<InOutItem> status5Or6 = inOutItemMapperEx.selectProjectByStatus(new String[]{"5", "6"});
        List<String> ids = new ArrayList<>();
        for(InOutItem item : status5Or6) {
            ids.add(item.getId().toString());
        }
        List<InOutItem> list = inOutItemMapperEx.selectByConditionInOutItem(null, null, null, 0 ,999, StringUtil.listToStringArray(ids), null, null, null, null);
        BaseResponseInfo res = new BaseResponseInfo();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("bonus", list);
        map.put("status", status5Or6);
        List<AccountHeadVo4ListEx> account = accountHeadMapperEx.selectByConditionAccountHead("分配金额", null, null, null, null, null, null, null, null ,null ,null, null, StringUtil.listToStringArray(ids), 0, 9999);
        map.put("account", account);
        res.code = 200;
        res.data = map;
        return res;
    }

    @GetMapping("/canBonus")
    public BaseResponseInfo selectList() {
        List<InOutItem> status5Or6 = inOutItemMapperEx.selectProjectByStatus(new String[]{"5", "6"});
        BaseResponseInfo res = new BaseResponseInfo();
        res.data = status5Or6;
        return res;
    }
}
