package com.shanduo.newretail.mapper;

import java.util.List;

import com.shanduo.newretail.entity.Commodity;
import com.shanduo.newretail.entity.service.CommodityInfo;

public interface CommodityMapper {
    int deleteByPrimaryKey(String id);

    int insert(Commodity record);

    int insertSelective(Commodity record);

    Commodity selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Commodity record);

    int updateByPrimaryKey(Commodity record);
    
    List<CommodityInfo> selectCommodity(Integer categoryId,String id,Integer pageNum, Integer pageSize);
    
    List<CommodityInfo> selectCommoditys(Integer categoryId,String id,Integer pageNum, Integer pageSize);
    
    List<CommodityInfo> selectWarehouseCommodity(Integer categoryId,String id);
    
    List<String> selectWarehouseCommoditys(Integer categoryId,String userId);
    
    int deleteCommodity(String commodityId);
    
    CommodityInfo selectOneCommodity(String id,String commodityId);
}