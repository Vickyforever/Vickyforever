package com.sofestar.jmsys.bl.customer.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.sofestar.jmsys.bl.customer.bean.dto.cus0060101.Cus0060101SearchDto;
import com.sofestar.jmsys.bl.customer.bean.io.cus0060101.Cus0060101SearchInput;

public interface Cus0060101DAO {

    /**
     * 契約一覧を検索します。
     *
     * @param criteria 検索条件
     * @return List
     */

// Cus0060101DAO.xml 连接
    List<Cus0060101SearchDto> search(@Param("criteria") Cus0060101SearchInput criteria);

    int infolistCount(@Param("criteria") Cus0060101SearchInput criteria);

    List<Cus0060101SearchDto> infoListcsv(@Param("criteria") Cus0060101SearchInput criteria);

}
