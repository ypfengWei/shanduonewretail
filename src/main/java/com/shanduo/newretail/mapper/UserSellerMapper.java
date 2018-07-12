package com.shanduo.newretail.mapper;

import java.util.List;
<<<<<<< HEAD
import java.util.Map;
=======
>>>>>>> 354341fb34a556905b47185ed34514df4587b7e0

import com.shanduo.newretail.entity.UserSeller;

public interface UserSellerMapper {
    int deleteByPrimaryKey(String id);

    int insert(UserSeller record);

    int insertSelective(UserSeller record);

    UserSeller selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(UserSeller record);

    int updateByPrimaryKey(UserSeller record);
    
<<<<<<< HEAD
    List<Object> selectNearbySeller(Map<String, Object> params);
=======
    List<UserSeller> selectNearbySeller(double lon,double lat);
>>>>>>> 354341fb34a556905b47185ed34514df4587b7e0
}