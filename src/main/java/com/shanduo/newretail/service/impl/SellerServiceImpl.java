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
import com.shanduo.newretail.entity.service.SellerInfo;
import com.shanduo.newretail.mapper.UserSellerMapper;
import com.shanduo.newretail.service.SellerService;
import com.shanduo.newretail.util.DateUtils;
import com.shanduo.newretail.util.LocationUtils;
import com.shanduo.newretail.util.StringUtils;
@Service
public class SellerServiceImpl implements SellerService {
	private static final Logger Log = LoggerFactory.getLogger(SellerServiceImpl.class);
	@Autowired
	private UserSellerMapper userSellerMapper;

	@Override
	public List<Map<String, List<SellerInfo>>> selectNearbySeller(double lon, double lat,List<String> sellerType) {
		List<Map<String, List<SellerInfo>>> sellerInfoList = new ArrayList<Map<String, List<SellerInfo>>>();
		for(int i=0;i<sellerType.size();i++){
			Map<String, List<SellerInfo>> sellerInfoMap = new HashMap<String, List<SellerInfo>>();
			sellerInfoMap = selectNearbySellerOneType(lon,lat,sellerType.get(i));
			sellerInfoList.add(sellerInfoMap);
		}
		return sellerInfoList;
	}
	/*
	 * 查询单个店铺种类下的附近所有店铺
	 */
	public Map<String, List<SellerInfo>> selectNearbySellerOneType(double lon, double lat,String sellerType) {
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
		Map<String, List<SellerInfo>> sellerInfoMap = new HashMap<String, List<SellerInfo>>();
		sellerInfoMap.put(sellerType, sellerInfoList);
		return sellerInfoMap;
	}


	@Override
	@Transactional(rollbackFor = Exception.class)
	public int insertSeller(String id, String sellerName, String phone,String parentId) {
		
		return userSellerMapper.insertSeller(id, sellerName, phone,parentId);
	}

	@Override
	public List<String> selectNearbySellerType(double lon, double lat) {
		
		return userSellerMapper.selectNearbySellerType(lon, lat);
	}
	@Override
	public UserSeller selectSellerDetails(String id) {
		
		return userSellerMapper.selectByPrimaryKey(id);
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
		userSeller.setSellerType(userSellerMap.get("sellerType").toString());
		userSeller.setLat(new BigDecimal(userSellerMap.get("lat").toString()));
		userSeller.setLon(new BigDecimal(userSellerMap.get("lon").toString()));
		userSeller.setStartDate(new Timestamp(Long.parseLong(userSellerMap.get("startDate").toString())));
		userSeller.setEndDate(new Timestamp(Long.parseLong(userSellerMap.get("endDate").toString())));
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

}
