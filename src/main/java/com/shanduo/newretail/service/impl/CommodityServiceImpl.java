package com.shanduo.newretail.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shanduo.newretail.consts.ErrorConsts;
import com.shanduo.newretail.controller.CommodityController;
import com.shanduo.newretail.entity.Relations;
import com.shanduo.newretail.mapper.RelationsMapper;
import com.shanduo.newretail.service.CommodityService;
@Service
public class CommodityServiceImpl implements CommodityService {
	private static final Logger Log = LoggerFactory.getLogger(CommodityServiceImpl.class);
	@Autowired
	private RelationsMapper relationsMapper;

	@Override
	public List<Integer> selectSellerCommodityType(String id) {
		
		return relationsMapper.selectSellerCommodityType(id);
	}

	@Override
	public boolean selectCommodityStock(String id, String commodityId, int commoditynum) {
		Relations relations = relationsMapper.selectCommodityStock(id, commodityId);
		if(null==relations || relations.getStock()-commoditynum<0){
			return false;
		}
		return true;
	}

	@Override
	public int updateCommodityStock(String id, String commodityId, int commoditynum,String type) {
		Relations relations = new Relations();
		relations.setCommodityId(commodityId);
		relations.setUserId(id);
		Relations relation = relationsMapper.selectCommodityStock(id, commodityId);
		if(null==relation){
			return 0;
		}
		if("0".equals(type)){//购买商品
			relations.setStock(relation.getStock()-commoditynum);
			relations.setSalesVolume(relation.getSalesVolume()+commoditynum);
		}else{//拒单
			relations.setStock(relation.getStock()+commoditynum);
			relations.setSalesVolume(relation.getSalesVolume()-commoditynum);
		}
		int count = relationsMapper.updateCommodityStock(relations);
		if(count < 1) {
			Log.warn("修改库存失败");
			throw new RuntimeException();
		}
		return count;
	}

}
