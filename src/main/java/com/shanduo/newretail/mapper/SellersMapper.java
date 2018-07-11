package com.shanduo.newretail.mapper;

import com.shanduo.newretail.entity.Sellers;

public interface SellersMapper {
    int deleteByPrimaryKey(String id);

    int insert(Sellers record);

    int insertSelective(Sellers record);

    Sellers selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Sellers record);

    int updateByPrimaryKey(Sellers record);
}