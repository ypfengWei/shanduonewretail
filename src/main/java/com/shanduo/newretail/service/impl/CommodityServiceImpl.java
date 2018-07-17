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
import com.shanduo.newretail.util.Page;
@Service
public class CommodityServiceImpl implements CommodityService {
	private static final Logger Log = LoggerFactory.getLogger(CommodityServiceImpl.class);
	@Autowired
	private RelationsMapper relationsMapper;
	@Autowired
	private CommodityMapper commodityMapper;

	@Override
	public List<Map<String,Object>> selectSellerCommodityType(String id,String typeId) {
		if("1".equals(typeId)){
			return relationsMapper.selectSellerCommodityType(id);
		}
		return null;
		
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
	public Map<String, Object> selectCommodity(Integer categoryId, String id,Integer pageNum, Integer pageSize) {
		List<CommodityInfo> commodityInfo = new ArrayList<CommodityInfo>(); 
		int totalRecord = relationsMapper.selectCommodityNum(id, categoryId);
		Map<String, Object> resultMap = new HashMap<String, Object>(3);
		if(0==totalRecord){
			return resultMap;
		}
		Page page = new Page(totalRecord, pageSize, pageNum);
		pageNum = (page.getPageNum() - 1) * page.getPageSize();
		commodityInfo = commodityMapper.selectCommodity(categoryId, id,pageNum, page.getPageSize());
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalPage", page.getTotalPage());
		resultMap.put("commodityInfoList", commodityInfo);
		return resultMap;
	}

	@Override
	public int updateCommodityVisible(String commodityId, String id, Integer visible) {
		
		return relationsMapper.updateCommodityVisible(commodityId, id, visible);
	}

}
