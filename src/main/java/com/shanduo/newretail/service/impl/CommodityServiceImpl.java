package com.shanduo.newretail.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.newretail.entity.Commodity;
import com.shanduo.newretail.entity.Relations;
import com.shanduo.newretail.entity.UserSeller;
import com.shanduo.newretail.entity.service.CommodityInfo;
import com.shanduo.newretail.mapper.CategoryMapper;
import com.shanduo.newretail.mapper.CommodityMapper;
import com.shanduo.newretail.mapper.RelationsMapper;
import com.shanduo.newretail.mapper.UserSellerMapper;
import com.shanduo.newretail.service.CommodityService;
import com.shanduo.newretail.util.Page;
import com.shanduo.newretail.util.UUIDGenerator;
@Service
public class CommodityServiceImpl implements CommodityService {
	private static final Logger Log = LoggerFactory.getLogger(CommodityServiceImpl.class);
	@Autowired
	private RelationsMapper relationsMapper;
	@Autowired
	private CommodityMapper commodityMapper;
	@Autowired
	private UserSellerMapper userSellerMapper;
	@Autowired
	private CategoryMapper categoryMapper;

	@Override
	public List<Map<String,Object>> selectSellerCommodityType(String id,String typeId) {
		if("1".equals(typeId)){
			return relationsMapper.selectSellerCommodityType(id);
		}
		if("2".equals(typeId)){
			return relationsMapper.selectSellerCommodityTypes(id);
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
	public Map<String, Object> selectCommodity(Integer categoryId, String id,Integer pageNum, Integer pageSize,String typeId) {
		List<CommodityInfo> commodityInfo = new ArrayList<CommodityInfo>();
		int totalRecord = 0;
		if("2".equals(typeId)){
			totalRecord = relationsMapper.selectCommodityNums(id, categoryId);
		}
		if("1".equals(typeId)){
			totalRecord = relationsMapper.selectCommodityNum(id, categoryId);
		}
		Map<String, Object> resultMap = new HashMap<String, Object>(3);
		if(0==totalRecord){
			resultMap.put("totalPage", pageNum-1);
			resultMap.put("commodityInfoList", commodityInfo);
			return resultMap;
		}
		Page page = new Page(totalRecord, pageSize, pageNum);
		pageNum = (page.getPageNum() - 1) * page.getPageSize();
		if("2".equals(typeId)){
			commodityInfo = commodityMapper.selectCommoditys(categoryId, id,pageNum, page.getPageSize());
		}else{
			commodityInfo = commodityMapper.selectCommodity(categoryId, id,pageNum, page.getPageSize());
		}
		resultMap.put("totalPage", page.getTotalPage());
		resultMap.put("commodityInfoList", commodityInfo);
		return resultMap;
	}

	@Override
	public int updateCommodityVisible(String commodityId, String id, Integer visible) {
		
		return relationsMapper.updateCommodityVisible(commodityId, id, visible);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int insertCommodity(String name, String picture, String price, String stock, String categoryId,String userId) {
		String id= UUIDGenerator.getUUID();
		Commodity commodity = new Commodity();
		commodity.setId(id);
		commodity.setName(name);
		commodity.setPicture(picture);
		commodity.setPrice(new BigDecimal(price));
		int count = commodityMapper.insertSelective(commodity);
		if(count<1){
			throw new RuntimeException();
		}
		Relations relations = new Relations();
		relations.setCommodityId(id);
		relations.setCategoryId(Integer.valueOf(categoryId));
		relations.setUserId(userId);
		relations.setStock(Integer.valueOf(stock));
		count = relationsMapper.insertSelective(relations);
		if(count<1){
			throw new RuntimeException();
		}
		return count;
	}

	@Override
	public List<Map<String, Object>> selectCommodityType(String id) {
		UserSeller userSeller =userSellerMapper.selectByPrimaryKey(id);
		List<Map<String, Object>> map = new ArrayList<Map<String, Object>>();
		Integer sellerType = userSeller.getSellerType();
		if(0==sellerType){
			map=categoryMapper.selectCommodityType(99, 999);
		}
		if(1==sellerType){
			map=categoryMapper.selectCommodityType(999, 9999);
		}
		if(2==sellerType){
			map=categoryMapper.selectCommodityType(9999, 99999);
		}
		if(3==sellerType) {
			map=categoryMapper.selectCommodityType(999999, 9999999);
		}
		return map;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateCommodity(String name, String picture, String price, String stock, String categoryId,
			String userId,String commodityId) {
		Commodity commodity = new Commodity();
		commodity.setId(commodityId);
		commodity.setName(name);
		commodity.setPicture(picture);
		commodity.setPrice(new BigDecimal(price));
		int count = commodityMapper.updateByPrimaryKeySelective(commodity);
		Relations relations = new Relations();
		relations.setCommodityId(commodityId);
		relations.setCategoryId(Integer.valueOf(categoryId));
		relations.setUserId(userId);
		relations.setStock(Integer.valueOf(stock));
		count = relationsMapper.updateRelations(relations);
		return count;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int deleteCommodity(String id, String commodityId) {
		int count = commodityMapper.deleteCommodity(commodityId);
		if(count<1){
			throw new RuntimeException();
		}
		count = relationsMapper.deleteRelations(commodityId, id);
		if(count<1){
			throw new RuntimeException();
		}
		return count;
	}

	@Override
	public int insertWarehouseCommodity(List<String> commodityIdList,String userId) {
		int count =0;
		for(int i=0;i<commodityIdList.size();i++){
			String commodityId = commodityIdList.get(i);
			Commodity commodity =  commodityMapper.selectByPrimaryKey(commodityId);
			String id = UUIDGenerator.getUUID();
			commodity.setId(id);
			count = commodityMapper.insertSelective(commodity);
			if(count<1){
				throw new RuntimeException();
			}
			Relations relations = relationsMapper.selectCommodity(commodityId);
			relations.setCommodityId(id);
			relations.setUserId(userId);
			count = relationsMapper.insertSelective(relations);
			if(count<1){
				throw new RuntimeException();
			}
		}
		return count;
	}

	@Override
	public CommodityInfo selectOneCommodity(String id,String commodityId) {
		
		return commodityMapper.selectOneCommodity(id,commodityId);
	}

	@Override
	public Map<String, Object> selectWarehouseCommodity(Integer categoryId, String id, Integer pageNum,
			Integer pageSize, String userId) {
		Map<String, Object> resultMap = new HashMap<String, Object>(3);
		List<CommodityInfo> list = commodityMapper.selectWarehouseCommodity(categoryId, id);
		List<String> lists = commodityMapper.selectWarehouseCommoditys(categoryId, userId);
		for(int i=list.size()-1;i>=0;i--){
			CommodityInfo commodityInfo = new CommodityInfo();
			commodityInfo=list.get(i);
			if(lists.contains(commodityInfo.getName())){
				list.remove(commodityInfo);
			}
		}
		List<CommodityInfo> commodityInfoList = new ArrayList<CommodityInfo>();
		Page page = new Page(list.size(), pageSize, pageNum);
		if(list.size()<(pageNum-1)*pageSize){
			resultMap.put("totalPage", page.getTotalPage());
			resultMap.put("commodityInfoList", commodityInfoList);
			return resultMap;
		}else if(list.size()>pageNum*pageSize){
			commodityInfoList=list.subList((pageNum-1)*pageSize,pageNum*pageSize);
		}else{
			
			for(int i=(pageNum-1)*pageSize;i<list.size();i++){
				commodityInfoList.add(list.get(i));
			}
			resultMap.put("totalPage", page.getTotalPage());
			resultMap.put("commodityInfoList", commodityInfoList);
			return resultMap;
		}
		resultMap.put("totalPage",page.getTotalPage());
		resultMap.put("commodityInfoList", commodityInfoList);
		return resultMap;
	}

}
