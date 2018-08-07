package com.shanduo.newretail.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.shanduo.newretail.entity.ToOrder;
import com.shanduo.newretail.entity.service.OrderInfo;

public interface ToOrderMapper {
    int deleteByPrimaryKey(String id);

    int insert(ToOrder record);

    int insertSelective(ToOrder record);

    ToOrder selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ToOrder record);

    int updateByPrimaryKey(ToOrder record);
    
    ToOrder getOrder(String orderId, String typeId);
    
    int updateOrder(String orderId, String sellerId,String state);
    
    int countSellerOrder(@Param("sellerId")String sellerId,@Param("state")String state);
    
    List<OrderInfo> listSellerOrder(@Param("sellerId")String sellerId,@Param("state")String state,
    		@Param("startDate")String startDate, @Param("endDate")String endDate,
    		@Param("pageNum")Integer pageNum,@Param("pageSize")Integer pageSize);
    
    List<String> listPending();
}