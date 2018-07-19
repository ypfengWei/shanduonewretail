package com.shanduo.newretail.mapper;

import com.shanduo.newretail.entity.ToUser;

public interface ToUserMapper {
    int deleteByPrimaryKey(String id);

    int insert(ToUser record);

    int insertSelective(ToUser record);

    ToUser selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ToUser record);

    int updateByPrimaryKey(ToUser record);
    
    ToUser getPhone(String mobilePhone);
    
    ToUser getLogin(String mobilePhone,String password);
    
    int updatePassswordByPhone(String phone, String password);
    
    String selectAdministratorsId();
}