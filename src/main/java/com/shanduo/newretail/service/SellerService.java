package com.shanduo.newretail.service;

import java.util.List;

import com.shanduo.newretail.entity.SellerInfo;


public interface SellerService {
	
	List<Object> selectNearbySeller(double lon,double lat);
	
	int insertSeller(String id,String sellerName,String phone);
}
