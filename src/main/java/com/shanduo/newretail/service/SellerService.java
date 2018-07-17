package com.shanduo.newretail.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.shanduo.newretail.entity.UserSeller;
import com.shanduo.newretail.entity.service.SellerInfo;


public interface SellerService {
	
	Map<String, List<SellerInfo>> selectNearbySellerOneType(double lon,double lat,String sellerType);
	
	List<Map<String,Object>> selectNearbySellerType(double lon,double lat);
	
	int insertSeller(String id,String sellerName,String phone,String parentId);
	
	UserSeller selectSellerDetails(String id);
	
	int updateSellerDetails(Map<String, Object> userSellerMap);
	
	int updateBusinessSign(String businessSign,String id);
	//判断该店铺是否营业中
	boolean selectBusinessSign(String id);
	//修改金额
	int updateMoney (BigDecimal money,String id,String type);
	//查询是否可以提现
	int selectMoney (BigDecimal money,String id);
}
