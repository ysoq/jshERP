package com.jsh.erp.datasource.entities;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName(value = "jsh_account_item")
public class AccountItem {
    private Long id;

    private Long headerId;

    private Long accountId;

    private Long inOutItemId;

    private Long billId;

    private BigDecimal needDebt;

    private BigDecimal finishDebt;

    private BigDecimal eachAmount;

    private String remark;

    private Long tenantId;

    private String deleteFlag;


}