package com.shanduo.newretail.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.shanduo.newretail.entity.ToOrderDetails;
import com.shanduo.newretail.entity.service.CommodityInfos;

public interface ToOrderDetailsMapper {
	int deleteByPrimaryKey(String id);

    int insert(ToOrderDetails record);

    int insertSelective(ToOrderDetails record);

    ToOrderDetails selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ToOrderDetails record);

    int updateByPrimaryKey(ToOrderDetails record);
    
    List<ToOrderDetails> listOrderId(String orderId);
    
    int countSellerCommodity(@Param("sellerId")String sellerId, @Param("categoryId")String categoryId,
    		@Param("startDate")String startDate, @Param("endDate")String endDate);
    
    List<CommodityInfos> listSellerCommodity(@Param("sellerId")String sellerId,
    		@Param("categoryId")String categoryId, @Param("startDate")String startDate, 
    		@Param("endDate")String endDate, @Param("pageNum")Integer pageNum,
    		@Param("pageSize")Integer pageSize);
    
    Double sumSellerMoney(@Param("sellerId")String sellerId, @Param("categoryId")String categoryId,
		   @Param("startDate")String startDate, @Param("endDate")String endDate);
}