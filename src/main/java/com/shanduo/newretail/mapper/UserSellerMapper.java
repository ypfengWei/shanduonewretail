package com.shanduo.newretail.mapper;

import java.util.List;
import java.util.Map;
import com.shanduo.newretail.entity.UserSeller;

public interface UserSellerMapper {
    int deleteByPrimaryKey(String id);

    int insert(UserSeller record);

    int insertSelective(UserSeller record);

    UserSeller selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(UserSeller record);

    int updateByPrimaryKey(UserSeller record);
    
    List<Object> selectNearbySeller(Map<String, Object> params);

}