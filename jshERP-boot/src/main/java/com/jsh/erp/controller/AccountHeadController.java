package com.jsh.erp.controller;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.ExceptionConstants;
import com.jsh.erp.datasource.entities.AccountHead;
import com.jsh.erp.datasource.entities.AccountHeadVo4Body;
import com.jsh.erp.datasource.entities.AccountHeadVo4ListEx;
import com.jsh.erp.datasource.mappers.AccountHeadMapperEx;
import com.jsh.erp.datasource.vo.DepotHeadVo4InDetail;
import com.jsh.erp.service.accountHead.AccountHeadService;
import com.jsh.erp.utils.BaseResponseInfo;
import com.jsh.erp.utils.ErpInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jsh.erp.utils.ResponseJsonUtil.returnJson;

/**
 * @author jishenghua 752*718*920
 */
@RestController
@RequestMapping(value = "/accountHead")
@Api(tags = {"财务管理"})
public class AccountHeadController {
    private Logger logger = LoggerFactory.getLogger(AccountHeadController.class);

    @Resource
    private AccountHeadService accountHeadService;

    @Resource
    private AccountHeadMapperEx accountHeadMapperEx;

    /**
     * 批量设置状态-审核或者反审核
     *
     * @param jsonObject
     * @param request
     * @return
     */
    @PostMapping(value = "/batchSetStatus")
    @ApiOperation(value = "批量设置状态-审核或者反审核")
    public String batchSetStatus(@RequestBody JSONObject jsonObject,
                                 HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        String status = jsonObject.getString("status");
        String ids = jsonObject.getString("ids");
        int res = accountHeadService.batchSetStatus(status, ids);
        if (res > 0) {
            return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }

    /**
     * 新增财务主表及财务子表信息
     *
     * @param body
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/addAccountHeadAndDetail")
    @ApiOperation(value = "新增财务主表及财务子表信息")
    public Object addAccountHeadAndDetail(@RequestBody AccountHeadVo4Body body, HttpServletRequest request) throws Exception {
        JSONObject result = ExceptionConstants.standardSuccess();
        String beanJson = body.getInfo();
        String rows = body.getRows();
        accountHeadService.addAccountHeadAndDetail(beanJson, rows, request);
        return result;
    }

    /**
     * 更新财务主表及财务子表信息
     *
     * @param body
     * @param request
     * @return
     * @throws Exception
     */
    @PutMapping(value = "/updateAccountHeadAndDetail")
    @ApiOperation(value = "更新财务主表及财务子表信息")
    public Object updateAccountHeadAndDetail(@RequestBody AccountHeadVo4Body body, HttpServletRequest request) throws Exception {
        JSONObject result = ExceptionConstants.standardSuccess();
        String beanJson = body.getInfo();
        String rows = body.getRows();
        accountHeadService.updateAccountHeadAndDetail(beanJson, rows, request);
        return result;
    }

    /**
     * 根据编号查询单据信息
     *
     * @param billNo
     * @param request
     * @return
     */
    @GetMapping(value = "/getDetailByNumber")
    @ApiOperation(value = "根据编号查询单据信息")
    public BaseResponseInfo getDetailByNumber(@RequestParam("billNo") String billNo,
                                              HttpServletRequest request) throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        AccountHeadVo4ListEx ahl = new AccountHeadVo4ListEx();
        try {
            List<AccountHeadVo4ListEx> list = accountHeadService.getDetailByNumber(billNo);
            if (list.size() > 0) {
                ahl = list.get(0);
            }
            res.code = 200;
            res.data = ahl;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }


    @GetMapping(value = "/getIncomeExpendDetail")
    @ApiOperation(value = "查询项目收支明细")
    public BaseResponseInfo getIncomeExpendDetail(@RequestParam("currentPage") Integer currentPage,
                                                              @RequestParam("pageSize") Integer pageSize,
                                                              @RequestParam("beginTime") String beginTime,
                                                              @RequestParam("endTime") String endTime,
                                                              @RequestParam(value = "creator", required = false) Long creator,
                                                              @RequestParam(value = "inOutItemId", required = false) String inOutItemId,
                                                              HttpServletRequest request) throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        Map<String, Object> map = new HashMap<String, Object>();
        List<DepotHeadVo4InDetail> resList = new ArrayList<>();
        List<DepotHeadVo4InDetail> list  = accountHeadMapperEx.getIncomeExpendDetail((currentPage-1)*pageSize, pageSize, beginTime,endTime,creator,inOutItemId  );
        int total = accountHeadMapperEx.getIncomeExpendDetailCount( beginTime,endTime,creator,inOutItemId);
        List<DepotHeadVo4InDetail> statistic = accountHeadMapperEx.getIncomeExpendStatistic( beginTime,endTime,creator,inOutItemId);
        //存放数据json数组
        if (null != list) {
            resList.addAll(list);
        }
        map.put("total", total);
        map.put("rows", resList);
        map.put("eachAmountTotal", statistic.get(0));
        res.code = 200;
        res.data = map;
        return res;
    }

    /**
     * 根据出入库单据id查询收付款单号
     *
     * @param billId
     * @param request
     * @return
     */
    @GetMapping(value = "/getFinancialBillNoByBillId")
    @ApiOperation(value = "根据编号查询单据信息")
    public BaseResponseInfo getFinancialBillNoByBillId(@RequestParam("billId") Long billId,
                                                       HttpServletRequest request) throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            List<AccountHead> list = accountHeadService.getFinancialBillNoByBillId(billId);
            res.code = 200;
            res.data = list;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }
}
