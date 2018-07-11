package com.shanduo.newretail.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.newretail.mapper.SellersMapper;
import com.shanduo.newretail.service.SellerService;
@Service
@Transactional(rollbackFor = Exception.class)
public class SellerServiceImpl implements SellerService {
	@Autowired
	private SellersMapper sellersMapper;
	

}
