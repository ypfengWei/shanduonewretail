package com.shanduo.newretail.mapper;

import com.shanduo.newretail.entity.AccessToken;

public interface AccessTokenMapper {
    int deleteByPrimaryKey(Integer tokenid);

    int insert(AccessToken record);

    int insertSelective(AccessToken record);

    AccessToken selectByPrimaryKey(Integer tokenid);

    int updateByPrimaryKeySelective(AccessToken record);

    int updateByPrimaryKey(AccessToken record);
    
    AccessToken selectAccessToken(String appid);
    
    int updateAccessToken(AccessToken accessToken);
}