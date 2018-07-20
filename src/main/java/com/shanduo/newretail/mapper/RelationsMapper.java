package com.shanduo.newretail.mapper;

import java.util.List;
import java.util.Map;

import com.shanduo.newretail.entity.Relations;
import com.shanduo.newretail.entity.RelationsKey;
import com.shanduo.newretail.entity.service.CommodityInfo;

public interface RelationsMapper {
    int deleteByPrimaryKey(RelationsKey key);

    int insert(Relations record);

    int insertSelective(Relations record);

    Relations selectByPrimaryKey(RelationsKey key);

    int updateByPrimaryKeySelective(Relations record);

    int updateByPrimaryKey(Relations record);
    
    List<Map<String,Object>> selectSellerCommodityType(String id);
    
    List<Map<String,Object>> selectSellerCommodityTypes(String id);
    
    Relations selectCommodityStock(String id,String commodityId);
    
    int updateCommodityStock(Relations relations);
    
    List<CommodityInfo> selectCommodity(Integer categoryId,String id);
    
    int updateCommodityVisible(String commodityId,String id,Integer visible);
    
    Integer selectCommodityNum(String id,Integer categoryId);
    
    Integer selectCommodityNums(String id,Integer categoryId);
    
    int updateRelations(Relations relations);
    
    int deleteRelations(String commodityId,String id);
    
    Relations selectCommodity(String commodityId);
}