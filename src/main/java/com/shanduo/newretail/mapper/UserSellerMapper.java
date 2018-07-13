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
    
    int insertSeller(String id,String sellerName,String phone,String parentId);
    
    List<String> selectNearbySellerType(double lon,double lat);
    
    List<UserSeller> selectNearbySeller(Map<String, Object> params);
    
    int updateBusinessSign(String businessSign,String id);
    
    UserSeller selectBusinessSign(String id);

}