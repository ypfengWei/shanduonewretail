package com.shanduo.newretail.mapper;

import com.shanduo.newretail.entity.UserToken;

public interface UserTokenMapper {
    int deleteByPrimaryKey(String token);

    int insert(UserToken record);

    int insertSelective(UserToken record);

    UserToken selectByPrimaryKey(String token);

    int updateByPrimaryKeySelective(UserToken record);

    int updateByPrimaryKey(UserToken record);
    
    int saveToken(String token,String userId);
}