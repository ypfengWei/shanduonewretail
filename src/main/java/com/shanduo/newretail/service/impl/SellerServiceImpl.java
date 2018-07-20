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
import com.shanduo.newretail.util.WxFileUtils;
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
		return userSeller;
	}
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateSellerDetails(Map<String, Object> userSellerMap) {
		UserSeller userSeller = new UserSeller();
		userSeller.setId(userSellerMap.get("id").toString());
		userSeller.setSellerName(userSellerMap.get("sellerName").toString());
		String sellerPicture = WxFileUtils.downloadImage(userSellerMap.get("accessToken").toString(), userSellerMap.get("sellerPicture").toString());
		userSeller.setSellerPicture(sellerPicture);
		userSeller.setNotice(userSellerMap.get("notice").toString());
		userSeller.setPhone(userSellerMap.get("phone").toString());
		userSeller.setSellerType(userSellerMap.get("sellerType").toString());
		userSeller.setLat(new BigDecimal(userSellerMap.get("lat").toString()));
		userSeller.setLon(new BigDecimal(userSellerMap.get("lon").toString()));
		userSeller.setStartDate(Timestamp.valueOf("1970-01-01 "+userSellerMap.get("startDate").toString()+":00"));
		userSeller.setEndDate(Timestamp.valueOf("1970-01-01 "+userSellerMap.get("endDate").toString()+":00"));
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

}
