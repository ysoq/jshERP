package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.InOutItemFlow;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InOutItemFlowMapper {

    List<InOutItemFlow> selectInOutItemByFlow2(@Param("id") Long id);

}