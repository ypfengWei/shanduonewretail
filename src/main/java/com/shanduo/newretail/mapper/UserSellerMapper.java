package com.shanduo.newretail.mapper;

import java.math.BigDecimal;
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
    
<<<<<<< HEAD
    List<String> selectNearbySellerType(double lon,double lat);
=======
    List<Map<String,Object>> selectNearbySellerType(double lon,double lat);
>>>>>>> 292a584f24f9808b854bb1946a748c8c915d6d14
    
    List<UserSeller> selectNearbySeller(Map<String, Object> params);
    
    int updateBusinessSign(String businessSign,String id);
    
    UserSeller selectBusinessSign(String id);
    
    int updateMoney (BigDecimal money,String id);

}