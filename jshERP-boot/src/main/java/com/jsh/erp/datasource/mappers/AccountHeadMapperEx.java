package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.*;
import com.jsh.erp.datasource.vo.DepotHeadVo4InDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface AccountHeadMapperEx {

    List<AccountHeadVo4ListEx> selectByConditionAccountHead(
            @Param("type") String type,
            @Param("creatorArray") String[] creatorArray,
            @Param("billNo") String billNo,
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime,
            @Param("organId") Long organId,
            @Param("creator") Long creator,
            @Param("handsPersonId") Long handsPersonId,
            @Param("accountId") Long accountId,
            @Param("status") String status,
            @Param("remark") String remark,
            @Param("number") String number,
            @Param("inOutItemId") String inOutItemId,
            @Param("offset") Integer offset,
            @Param("rows") Integer rows);

    Long countsByAccountHead(
            @Param("type") String type,
            @Param("creatorArray") String[] creatorArray,
            @Param("billNo") String billNo,
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime,
            @Param("organId") Long organId,
            @Param("creator") Long creator,
            @Param("handsPersonId") Long handsPersonId,
            @Param("accountId") Long accountId,
            @Param("status") String status,
            @Param("remark") String remark,
            @Param("number") String number);

    List<AccountHeadVo4ListEx> getDetailByNumber(
            @Param("billNo") String billNo);

    int batchDeleteAccountHeadByIds(@Param("updateTime") Date updateTime, @Param("updater") Long updater, @Param("ids") String[] ids);

    List<AccountHead> getAccountHeadListByAccountIds(@Param("accountIds") String[] accountIds);

    List<AccountHead> getAccountHeadListByOrganIds(@Param("organIds") String[] organIds);

    List<AccountHead> getAccountHeadListByHandsPersonIds(@Param("handsPersonIds") String[] handsPersonIds);

    List<AccountItem> getFinancialBillNoByBillIdList(
            @Param("idList") List<Long> idList);

    List<AccountHead> getFinancialBillNoByBillId(
            @Param("billId") Long billId);

    BigDecimal getFinancialAllPriceByOrganId(
            @Param("organId") Long organId);

    List<DepotHeadVo4InDetail> getIncomeExpendDetail(@Param("offset") Integer offset,
                                                     @Param("rows") Integer rows,
                                                     @Param("beginTime") String beginTime,
                                                     @Param("endTime") String endTime,
                                                     @Param("creator") Long creator,
                                                     @Param("inOutItemId") String inOutItemId);

    int getIncomeExpendDetailCount(
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime,
            @Param("creator") Long creator,
            @Param("inOutItemId") String inOutItemId);
}