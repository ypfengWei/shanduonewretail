package com.shanduo.newretail.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.newretail.entity.UserSeller;
import com.shanduo.newretail.entity.service.SellerDetails;
import com.shanduo.newretail.entity.service.SellerInfo;
import com.shanduo.newretail.mapper.CategoryMapper;
import com.shanduo.newretail.mapper.UserSellerMapper;
import com.shanduo.newretail.service.SellerService;
import com.shanduo.newretail.util.DateUtils;
import com.shanduo.newretail.util.LocationUtils;

@Service
public class SellerServiceImpl implements SellerService {
	private static final Logger Log = LoggerFactory.getLogger(SellerServiceImpl.class);
	@Autowired
	private UserSellerMapper userSellerMapper;
	@Autowired
	private CategoryMapper categoryMapper;
	
	/*
	 * 查询单个店铺种类下的附近所有店铺
	 */
	@Override
	public List<SellerInfo> selectNearbySellerOneType(double lon, double lat,String sellerType) {
		List<UserSeller> sellerList = new ArrayList<UserSeller>();
		List<SellerInfo>  sellerInfoList= new ArrayList<SellerInfo>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("lon", lon);
		params.put("lat", lat);
		params.put("sellerType", sellerType);
		sellerList = userSellerMapper.selectNearbySeller(params);
		for(int i=0;i<sellerList.size();i++){
				SellerInfo seller = new SellerInfo();
				UserSeller userSeller = new UserSeller();
				userSeller = sellerList.get(i);
				seller.setId(userSeller.getId());
				seller.setSellerName(userSeller.getSellerName());
				seller.setSellerPicture(userSeller.getSellerPicture());
				seller.setSellerType(userSeller.getSellerType());
				Integer distribution = userSeller.getDistribution();
				List<Map<String, Object>> distributionTypeList =categoryMapper.selectDistributionType();
				Map<String, Object> distributionType = new HashMap<>();
				for(int j=0;j<distributionTypeList.size();j++){
					distributionType = distributionTypeList.get(j);
					if(distribution.equals(Integer.valueOf(distributionType.get("id").toString()))){
						distribution=Integer.valueOf(distributionType.get("category_name").toString());
						seller.setDistribution(distribution);
						break;
					}
				}
				//计算店铺与顾客的距离
				seller.setDistance(LocationUtils.getDistance(lon, lat, userSeller.getLon().doubleValue(), userSeller.getLat().doubleValue())+"");
				//判断当前时间店铺是否营业
				if("0".equals(userSeller.getBusinessSign())&&null!=userSeller.getStartDate()&&null!=userSeller.getEndDate()){
					SimpleDateFormat df = new SimpleDateFormat("HH:mm");//设置日期格式
				    Date now =null;
				    Date beginTime = userSeller.getStartDate();
				    Date endTime = userSeller.getEndDate();
				    try {
				        now = df.parse(df.format(new Date()));
				    } catch (Exception e) {
				        e.printStackTrace();
				    }
				    Boolean businessSign = DateUtils.belongCalendar(now, beginTime, endTime);
				    seller.setBusinessSign(businessSign);
				}else {
					seller.setBusinessSign(false);
				}
				sellerInfoList.add(seller);
		}
		return sellerInfoList;
	}


	@Override
	@Transactional(rollbackFor = Exception.class)
	public int insertSeller(String id, String sellerName, String phone,String parentId) {
		
		return userSellerMapper.insertSeller(id, sellerName, phone,parentId);
	}

	@Override
	public List<Map<String,Object>> selectNearbySellerType(double lon, double lat) {
		
		return userSellerMapper.selectNearbySellerType(lon, lat);
	}
	@Override
	public SellerDetails selectSellerDetails(String id) {
		SellerDetails userSeller = userSellerMapper.selectSellerDetails(id);
		Integer distribution = userSeller.getDistribution();
		List<Map<String, Object>> distributionTypeList =categoryMapper.selectDistributionType();
		Map<String, Object> distributionType = new HashMap<>();
		for(int i=0;i<distributionTypeList.size();i++){
			distributionType = distributionTypeList.get(i);
			if(distribution.equals(Integer.valueOf(distributionType.get("id").toString()))){
				distribution=Integer.valueOf(distributionType.get("category_name").toString());
				userSeller.setDistribution(distribution);
				return userSeller;
			}
		}
		return userSeller;
	}
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateSellerDetails(Map<String, Object> userSellerMap) {
		UserSeller userSeller = new UserSeller();
		userSeller.setId(userSellerMap.get("id").toString());
		userSeller.setSellerName(userSellerMap.get("sellerName").toString());
		userSeller.setSellerPicture(userSellerMap.get("sellerPicture").toString());
		userSeller.setNotice(userSellerMap.get("notice").toString());
		userSeller.setPhone(userSellerMap.get("phone").toString());
		userSeller.setSellerType(Integer.valueOf(userSellerMap.get("sellerType").toString()));
		userSeller.setDistribution(Integer.valueOf(userSellerMap.get("distribution").toString()));
		userSeller.setLat(new BigDecimal(userSellerMap.get("lat").toString()));
		userSeller.setLon(new BigDecimal(userSellerMap.get("lon").toString()));
		userSeller.setStartDate(Timestamp.valueOf("1970-01-01 "+userSellerMap.get("startDate").toString()+":00"));
		userSeller.setEndDate(Timestamp.valueOf("1970-01-01 "+userSellerMap.get("endDate").toString()+":00"));
		userSeller.setAddress(userSellerMap.get("address").toString());
		return userSellerMapper.updateByPrimaryKeySelective(userSeller);
	}
	@Override
	public int updateBusinessSign(String businessSign, String id) {
		
		return userSellerMapper.updateBusinessSign(businessSign, id);
	}
	@Override
	public boolean selectBusinessSign(String id) {
		UserSeller userSeller = userSellerMapper.selectBusinessSign(id);
		if(null==userSeller){
			return false;
		}
		String businessSign = userSeller.getBusinessSign();
		if("0".equals(businessSign)&&null!=userSeller.getStartDate()&&null!=userSeller.getEndDate()){
			SimpleDateFormat df = new SimpleDateFormat("HH:mm");//设置日期格式
		    Date now =null;
		    Date beginTime = userSeller.getStartDate();
		    Date endTime = userSeller.getEndDate();
		    try {
		        now = df.parse(df.format(new Date()));
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		    Boolean businessSigns = DateUtils.belongCalendar(now, beginTime, endTime);
		    return businessSigns;
		}
		
		return false;
	}
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateMoney(BigDecimal money, String id,String type) {
		UserSeller userSeller = userSellerMapper.selectBusinessSign(id);
		if("0".equals(type)){//加钱
			money = money.add(userSeller.getMoney());
		}else{
			money = (userSeller.getMoney()).subtract(money);
			if(BigDecimal.valueOf(0).compareTo(money)==1){
				return 0;
			}
		}
		int count = userSellerMapper.updateMoney(money, id);
		if(count<1){
			Log.warn("修改金额失败");
			throw new RuntimeException();
		}
		return count;
	}
	@Override
	public int selectMoney(BigDecimal money, String id) {
		UserSeller userSeller = userSellerMapper.selectBusinessSign(id);
		money = (userSeller.getMoney()).subtract(money);
		if(BigDecimal.valueOf(0).compareTo(money)==1){//0>money
			return 0;
		}
		return 1;
	}


	@Override
	public List<Map<String,Object>> selectSellerType() {
		
		return categoryMapper.selectSellerType();
	}


	@Override
	public List<Map<String, Object>> selectSalesmanSubordinate(String id,Integer pageNum, Integer pageSize) {
		pageNum = (pageNum-1)*pageSize;
		List<Map<String, Object>> sellerList = userSellerMapper.selectSalesmanSubordinate(id,pageNum,pageSize);
		return sellerList;
	}

	@Override
	public int checkLocation(String sellerId, String lat, String lon) {
		UserSeller seller = userSellerMapper.selectByPrimaryKey(sellerId);
		if(null == seller) {
			return 1;
		}
		double lats = Double.parseDouble(lat);
		double lons = Double.parseDouble(lon);
		double location = LocationUtils.getDistance(lons, lats, seller.getLon().doubleValue(), seller.getLat().doubleValue());
		if(location <= 0.7) {
			return 0;
		}
		return 1;
	}


	@Override
	public Integer selectSubordinateCount(String id) {
		
		return userSellerMapper.selectSubordinateCount(id);
	}


	@Override
	public Double selectSalesmanAchievement(String id, String startDate, String endDate) {
	
		return userSellerMapper.selectSalesmanAchievement(id, startDate, endDate);
	}


	@Override
	public Double selectRegionAchievement(String id, String startDate, String endDate) {
		
		return userSellerMapper.selectRegionAchievement(id, startDate, endDate);
	}


	@Override
	public Integer selectSellerCount() {
		
		return userSellerMapper.selectSellerCount();
	}


	@Override
	public Double selectManageAchievement(String id, String startDate, String endDate) {
		
		return userSellerMapper.selectManageAchievement(id, startDate, endDate);
	}


	@Override
	public List<Map<String, Object>> selectDistributionType() {
		
		return categoryMapper.selectDistributionType();
	}

}
