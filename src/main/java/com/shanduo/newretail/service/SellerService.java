package com.shanduo.newretail.service;

import java.util.List;
import java.util.Map;

import com.shanduo.newretail.entity.UserSeller;
import com.shanduo.newretail.entity.serice.SellerInfo;


public interface SellerService {
	
	List<Map<String, List<SellerInfo>>> selectNearbySeller(double lon,double lat,List<String> sellerType);
	
	List<String> selectNearbySellerType(double lon,double lat);
	
	int insertSeller(String id,String sellerName,String phone,String parentId);
	
	UserSeller selectSellerDetails(String id);
	
	int updateSellerDetails(Map<String, Object> userSellerMap);
	
	int updateBusinessSign(String businessSign,String id);
	//判断该店铺是否营业中
	boolean selectBusinessSign(String id);
}
