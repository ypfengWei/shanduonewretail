package com.shanduo.newretail.mapper;

import com.shanduo.newretail.entity.PhoneVerifyCode;

public interface PhoneVerifyCodeMapper {
    int deleteByPrimaryKey(String id);

    int insert(PhoneVerifyCode record);

    int insertSelective(PhoneVerifyCode record);

    PhoneVerifyCode selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PhoneVerifyCode record);

    int updateByPrimaryKey(PhoneVerifyCode record);
}