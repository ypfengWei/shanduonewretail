package com.shanduo.newretail.service;

import java.util.List;


public interface CommodityService {
	/**
	 * 查询店铺商品种类
	 * 
	 */
	List<Integer> selectSellerCommodityType(String id);
	/*
	 * 检测购买数量是否大于库存
	 */
	boolean selectCommodityStock(String id,String commodityId,int commoditynum);
	
	int updateCommodityStock(String id,String commodityId,int commoditynum,String type);

}
