package com.jsh.erp.service.InvoiceRecord;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsh.erp.datasource.entities.InOutItem;
import com.jsh.erp.datasource.mappers.InvoiceRecordMapper;
import com.jsh.erp.datasource.entities.InvoiceRecord;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author ysok
 * @since 2025-04-25 21:33:55
 */
@Service
@Slf4j
public class InvoiceRecordServiceImpl extends ServiceImpl<InvoiceRecordMapper, InvoiceRecord> implements IService<InvoiceRecord> {

    @Autowired
    private InvoiceRecordMapper invoiceRecordMapper;

    /**
     * 根据传入的 InOutItem 列表中的 projectId 查询对应的含税金额发票，并将结果赋值到对应对象中。
     *
     * @param inOutItemList 包含 projectId 的 InOutItem 对象列表
     */
    public void assignTaxInclusiveInvoiceAmount(List<InOutItem> inOutItemList) {
        if (inOutItemList == null || inOutItemList.isEmpty()) {
            return;
        }


        // 调用 mapper 方法查询发票信息
        var queryWrapper = new QueryWrapper<InvoiceRecord>();
        var ids = inOutItemList.stream().map(InOutItem::getId).toArray();
        queryWrapper.in("project_id", ids);
        queryWrapper.eq("delete_flag", "0");
        queryWrapper.eq("status", "1");

        List<InvoiceRecord> invoiceRecordList = invoiceRecordMapper.selectList(queryWrapper);

        // 遍历 InOutItem 列表，根据 projectId 查询对应的发票信息，并赋值到 InOutItem 对象中
        for (InOutItem inOutItem : inOutItemList) {
            for (InvoiceRecord invoiceRecord : invoiceRecordList) {
                if (inOutItem.getId().equals(invoiceRecord.getProjectId())) {
                    inOutItem.setTaxAmount(invoiceRecord.getTaxAmount());
                    break;
                }
            }
        }
    }

    public List<InvoiceRecord> getListByProjectId(Long projectId) {
        LambdaQueryWrapper<InvoiceRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InvoiceRecord::getProjectId, projectId);
        queryWrapper.eq(InvoiceRecord::getDeleteFlag, "0");
        queryWrapper.eq(InvoiceRecord::getStatus, "1");
        List<InvoiceRecord> list = invoiceRecordMapper.selectList(queryWrapper);
        return list;
    }
}
