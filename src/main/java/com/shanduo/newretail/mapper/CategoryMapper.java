package com.shanduo.newretail.mapper;

import java.util.List;
import java.util.Map;

import com.shanduo.newretail.entity.Category;

public interface CategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);
    
    List<Map<String,Object>> selectSellerType();
    
    List<Map<String,Object>> selectCommodityType(Integer start,Integer end);
    
    List<Map<String,Object>> selectDistributionType();
}