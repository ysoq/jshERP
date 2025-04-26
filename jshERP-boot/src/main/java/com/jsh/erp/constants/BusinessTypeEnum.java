package com.jsh.erp.constants;

import lombok.Getter;

@Getter
public enum BusinessTypeEnum {
    ACCOUNT_HEAD("account_head"),
    Invoice_Record("invoice_record"),;

    private final String type;

    BusinessTypeEnum(String purchase) {
        this.type = purchase;
    }
}