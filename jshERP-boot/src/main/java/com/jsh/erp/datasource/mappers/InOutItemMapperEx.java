package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.InOutItem;
import com.jsh.erp.datasource.entities.InOutItemExample;
import com.jsh.erp.datasource.entities.InOutItemFlow;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface InOutItemMapperEx {

    List<InOutItem> selectByConditionInOutItem(
            @Param("name") String name,
            @Param("type") String type,
            @Param("remark") String remark,
            @Param("offset") Integer offset,
            @Param("rows") Integer rows,
            @Param("ids") String[] ids,
            @Param("manager") String manager,
            @Param("code") String code,
            @Param("supplierId") String supplierId,
            @Param("enabled") String enabled
    );

    List<InOutItem> selectProjectByStatus(@Param("status") String[] status);

    List<InOutItemFlow> selectInOutItemByFlow(@Param("id") Long id);

    Long countsByInOutItem(
            @Param("name") String name,
            @Param("type") String type,
            @Param("remark") String remark,
            @Param("manager") String manager);

    int batchDeleteInOutItemByIds(@Param("updateTime") Date updateTime, @Param("updater") Long updater, @Param("ids") String ids[]);
}