package com.shanduo.newretail.service;

import java.util.List;
import java.util.Map;

import com.shanduo.newretail.entity.Commodity;


public interface CommodityService {
	/**
	 * 查询店铺商品种类
	 * 
	 */
	List<Map<String,Object>> selectSellerCommodityType(String id,String typeId);
	/*
	 * 检测购买数量是否大于库存
	 */
	boolean selectCommodityStock(String id,String commodityId,int commoditynum);
	/*
	 * 修改库存和销量
	 */
	int updateCommodityStock(String id,String commodityId,int commoditynum,String type);
	/*
	 * 查询店铺同一类别的所有商品
	 */
	Map<String, Object> selectCommodity(Integer categoryId,String id,Integer pageNum, Integer pageSize,String typeId);
	/*
	 * 商品上下架
	 */
	int updateCommodityVisible(String commodityId,String id,Integer visible);
	
	/*
	 * 商品上传
	 */
	int insertCommodity(String name,String picture,String price,String stock,String categoryId,String userId);
	
	/*
	 * 查询商品所有类别
	 */
	List<Map<String,Object>> selectCommodityType(String id);
}
