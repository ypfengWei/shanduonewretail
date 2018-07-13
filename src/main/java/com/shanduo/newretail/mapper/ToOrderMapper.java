package com.shanduo.newretail.mapper;

import com.shanduo.newretail.entity.ToOrder;

public interface ToOrderMapper {
    int deleteByPrimaryKey(String id);

    int insert(ToOrder record);

    int insertSelective(ToOrder record);

    ToOrder selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ToOrder record);

    int updateByPrimaryKey(ToOrder record);
    
    ToOrder getUnpaidOrder(String id);
}