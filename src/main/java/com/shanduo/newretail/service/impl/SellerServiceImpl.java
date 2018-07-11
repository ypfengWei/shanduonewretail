package com.shanduo.newretail.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.shanduo.newretail.entity.SellerInfo;
import com.shanduo.newretail.entity.UserSeller;
import com.shanduo.newretail.mapper.UserSellerMapper;
import com.shanduo.newretail.service.SellerService;
import com.shanduo.newretail.util.DateUtils;
import com.shanduo.newretail.util.LocationUtils;
@Service
public class SellerServiceImpl implements SellerService {
	@Autowired
	private UserSellerMapper userSellerMapper;

	@Override
	public List<List<SellerInfo>> selectNearbySeller(double lon, double lat) {
		List<Object> sellerList = new ArrayList<Object>();
		List<List<SellerInfo>>  sellerInfoList= new ArrayList<List<SellerInfo>>();
		List<String> sellerType = new ArrayList<String>();
		sellerType.add("0");
		sellerType.add("1");
		sellerType.add("2");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("lon", lon);
		params.put("lat", lat);
		params.put("list", sellerType);
		sellerList = userSellerMapper.selectNearbySeller(params);
		for(int i=0;i<sellerList.size();i++){
			List<UserSeller> sellerLists =(List<UserSeller>) sellerList.get(i);
			List<SellerInfo> sellerInfoLists = new ArrayList<SellerInfo>();
			for(int j=0;j<sellerLists.size();j++){
				SellerInfo seller = new SellerInfo();
				UserSeller userSeller = new UserSeller();
				userSeller = sellerLists.get(j);
				seller.setId(userSeller.getId());
				seller.setSellerName(userSeller.getSellerName());
				seller.setSellerPicture(userSeller.getSellerPicture());
				seller.setSellerType(userSeller.getSellerType());
				//计算店铺与顾客的距离
				seller.setDistance(LocationUtils.getDistance(lon, lat, userSeller.getLon().doubleValue(), userSeller.getLat().doubleValue())+"");
				//判断当前时间店铺是否营业
				if("0".equals(userSeller.getBusinessSign())){
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
				sellerInfoLists.add(seller);
			}
			sellerInfoList.add(sellerInfoLists);
		}
		
		return sellerInfoList;
	}

}
