package com.shanduo.newretail.service;

import java.util.List;

import com.shanduo.newretail.entity.SellerInfo;


public interface SellerService {
	
	List<List<SellerInfo>> selectNearbySeller(double lon,double lat);

}
