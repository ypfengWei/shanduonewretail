package com.shanduo.newretail.mapper;

import java.util.List;
import java.util.Map;

import com.shanduo.newretail.entity.ToUser;
import com.shanduo.newretail.entity.service.UserInfo;

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
    
    int countParent(String parentId);
    
    List<UserInfo> listParent(String parentId, Integer pageNum, Integer pageSize);
    
    int updateopenId(String openId, String phone);
   
}