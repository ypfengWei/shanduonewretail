package com.shanduo.newretail.service;

import java.util.List;
import java.util.Map;

import com.shanduo.newretail.entity.service.CommodityInfo;


public interface CommodityService {
	/**
	 * 查询店铺商品种类
	 * 
	 */
<<<<<<< HEAD
	List<Integer> selectSellerCommodityType(String id);
=======
	List<Map<String,Object>> selectSellerCommodityType(String id);
>>>>>>> 292a584f24f9808b854bb1946a748c8c915d6d14
	/*
	 * 检测购买数量是否大于库存
	 */
	boolean selectCommodityStock(String id,String commodityId,int commoditynum);
	/*
	 * 修改库存和销量
	 */
	int updateCommodityStock(String id,String commodityId,int commoditynum,String type);
	/*
<<<<<<< HEAD
	 * 查询店铺所有商品
	 */
	Map<Integer,List<CommodityInfo>> selectCommodity(List<Integer> categoryIdList,String id);
=======
	 * 查询店铺同一类别的所有商品
	 */
	List<CommodityInfo> selectCommodity(Integer categoryId,String id);
>>>>>>> 292a584f24f9808b854bb1946a748c8c915d6d14
	/*
	 * 商品上下架
	 */
	int updateCommodityVisible(String commodityId,String id,Integer visible);
}
