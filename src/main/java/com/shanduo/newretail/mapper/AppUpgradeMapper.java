package com.shanduo.newretail.mapper;

import com.shanduo.newretail.entity.AppUpgrade;

public interface AppUpgradeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AppUpgrade record);

    int insertSelective(AppUpgrade record);

    AppUpgrade selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AppUpgrade record);

    int updateByPrimaryKey(AppUpgrade record);
    
    AppUpgrade selectApp(Integer appType);
}