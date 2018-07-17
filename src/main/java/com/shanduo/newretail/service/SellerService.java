package com.shanduo.newretail.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.shanduo.newretail.entity.UserSeller;
import com.shanduo.newretail.entity.service.SellerInfo;


public interface SellerService {
	
<<<<<<< HEAD
	List<Map<String, List<SellerInfo>>> selectNearbySeller(double lon,double lat,List<String> sellerType);
	
	List<String> selectNearbySellerType(double lon,double lat);
=======
	Map<String, List<SellerInfo>> selectNearbySellerOneType(double lon,double lat,String sellerType);
	
	List<Map<String,Object>> selectNearbySellerType(double lon,double lat);
>>>>>>> 292a584f24f9808b854bb1946a748c8c915d6d14
	
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
