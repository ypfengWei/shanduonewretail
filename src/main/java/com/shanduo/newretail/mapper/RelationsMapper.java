package com.shanduo.newretail.mapper;

import java.util.List;

import com.shanduo.newretail.entity.Relations;
import com.shanduo.newretail.entity.RelationsKey;

public interface RelationsMapper {
    int deleteByPrimaryKey(RelationsKey key);

    int insert(Relations record);

    int insertSelective(Relations record);

    Relations selectByPrimaryKey(RelationsKey key);

    int updateByPrimaryKeySelective(Relations record);

    int updateByPrimaryKey(Relations record);
    
    List<Integer> selectSellerCommodityType(String id);
    
    Relations selectCommodityStock(String id,String commodityId);
    
    int updateCommodityStock(Relations relations);
}