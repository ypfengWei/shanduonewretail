package com.shanduo.newretail.mapper;

import java.util.List;

import com.shanduo.newretail.entity.ToOrderDetails;

public interface ToOrderDetailsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ToOrderDetails record);

    int insertSelective(ToOrderDetails record);

    ToOrderDetails selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ToOrderDetails record);

    int updateByPrimaryKey(ToOrderDetails record);
    
    List<ToOrderDetails> listOrderId(String orderId);
}