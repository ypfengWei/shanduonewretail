package com.shanduo.newretail.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.shanduo.newretail.entity.Relations;
import com.shanduo.newretail.entity.service.CommodityInfo;
import com.shanduo.newretail.mapper.CommodityMapper;
import com.shanduo.newretail.mapper.RelationsMapper;
import com.shanduo.newretail.service.CommodityService;
@Service
public class CommodityServiceImpl implements CommodityService {
	private static final Logger Log = LoggerFactory.getLogger(CommodityServiceImpl.class);
	@Autowired
	private RelationsMapper relationsMapper;
	@Autowired
	private CommodityMapper commodityMapper;

	@Override
<<<<<<< HEAD
	public List<Integer> selectSellerCommodityType(String id) {
=======
	public List<Map<String,Object>> selectSellerCommodityType(String id) {
>>>>>>> 292a584f24f9808b854bb1946a748c8c915d6d14
		
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
	@Transactional(rollbackFor = Exception.class)
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

	@Override
<<<<<<< HEAD
	public Map<Integer, List<CommodityInfo>> selectCommodity(List<Integer> categoryIdList, String id) {
		Map<Integer, List<CommodityInfo>> commodityMap = new HashMap<Integer, List<CommodityInfo>>();
		for(int i=0;i<categoryIdList.size();i++){
			List<CommodityInfo> commodityInfo = new ArrayList<CommodityInfo>();
			Integer categoryId = categoryIdList.get(i); 
			commodityInfo = commodityMapper.selectCommodity(categoryId, id);
			if(!commodityInfo.isEmpty()){
				commodityMap.put(categoryId, commodityInfo);
			}
		}
		return commodityMap;
=======
	public List<CommodityInfo> selectCommodity(Integer categoryId, String id) {
		List<CommodityInfo> commodityInfo = new ArrayList<CommodityInfo>(); 
		commodityInfo = commodityMapper.selectCommodity(categoryId, id);
		return commodityInfo;
>>>>>>> 292a584f24f9808b854bb1946a748c8c915d6d14
	}

	@Override
	public int updateCommodityVisible(String commodityId, String id, Integer visible) {
		
		return relationsMapper.updateCommodityVisible(commodityId, id, visible);
	}

}
