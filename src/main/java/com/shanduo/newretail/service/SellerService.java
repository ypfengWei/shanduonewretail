package com.shanduo.newretail.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.shanduo.newretail.entity.service.SellerDetails;
import com.shanduo.newretail.entity.service.SellerInfo;


public interface SellerService {
	
	List<SellerInfo> selectNearbySellerOneType(double lon,double lat,String sellerType);
	
	List<Map<String,Object>> selectNearbySellerType(double lon,double lat);
	
	int insertSeller(String id,String sellerName,String phone,String parentId);
	
	SellerDetails selectSellerDetails(String id);
	
	int updateSellerDetails(Map<String, Object> userSellerMap);
	
	int updateBusinessSign(String businessSign,String id);
	//判断该店铺是否营业中
	boolean selectBusinessSign(String id);
	//修改金额
	int updateMoney (BigDecimal money,String id,String type);
	//查询是否可以提现
	int selectMoney (BigDecimal money,String id);
	
	List<Map<String,Object>> selectSellerType();
	
	List<Map<String,Object>> selectSalesmanSubordinate(String id,Integer pageNum, Integer pageSize);
	
	Integer selectSubordinateCount(String id);
	
	Double selectSalesmanAchievement(String id,String startDate,String endDate);
	
	Double selectRegionAchievement(String id,String startDate,String endDate);
	
	Integer selectSellerCount();
	
	Double selectManageAchievement(String id,String startDate,String endDate);
	
	List<Map<String,Object>> selectDistributionType();
	/**
	 * 检查收货地点是否超出配送范围
	 * @Title: checkLocation
	 * @Description: TODO
	 * @param @param sellerId
	 * @param @param lat
	 * @param @param lon
	 * @param @return
	 * @return int
	 * @throws
	 */
	int checkLocation(String sellerId, String lat, String lon);
}
