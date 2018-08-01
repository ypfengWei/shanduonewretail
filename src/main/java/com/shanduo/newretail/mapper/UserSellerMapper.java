package com.shanduo.newretail.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.shanduo.newretail.entity.UserSeller;
import com.shanduo.newretail.entity.service.SellerDetails;

public interface UserSellerMapper {
    int deleteByPrimaryKey(String id);

    int insert(UserSeller record);

    int insertSelective(UserSeller record);

    UserSeller selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(UserSeller record);

    int updateByPrimaryKey(UserSeller record);
    
    int insertSeller(String id,String sellerName,String phone,String parentId);
    
    List<Map<String,Object>> selectNearbySellerType(double lon,double lat);
    
    List<UserSeller> selectNearbySeller(Map<String, Object> params);
    
    int updateBusinessSign(String businessSign,String id);
    
    UserSeller selectBusinessSign(String id);
    
    int updateMoney (BigDecimal money,String id);
    
    SellerDetails selectSellerDetails(String id);
    
    List<Map<String,Object>>  selectSalesmanSubordinate(String id,Integer pageNum, Integer pageSize);
    
    Integer selectSubordinateCount(String id);
    
    Double selectSalesmanAchievement(@Param("parentId")String id, @Param("startDate")String startDate, @Param("endDate")String endDate);

    Double selectRegionAchievement(@Param("parentId")String id, @Param("startDate")String startDate, @Param("endDate")String endDate);
    
    Integer selectSellerCount();
    
    Double selectManageAchievement(@Param("parentId")String id, @Param("startDate")String startDate, @Param("endDate")String endDate);
    
    Integer selectSubordinateSellerCount(String id);
}