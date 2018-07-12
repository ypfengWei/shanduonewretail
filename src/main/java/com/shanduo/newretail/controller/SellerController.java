package com.shanduo.newretail.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shanduo.newretail.consts.ErrorConsts;
import com.shanduo.newretail.entity.common.ErrorBean;
import com.shanduo.newretail.entity.common.ResultBean;
import com.shanduo.newretail.entity.common.SuccessBean;
import com.shanduo.newretail.entity.serice.SellerInfo;
import com.shanduo.newretail.service.SellerService;
import com.shanduo.newretail.util.PatternUtils;
import com.shanduo.newretail.util.StringUtils;


@Controller
@RequestMapping(value = "jseller")
public class SellerController {
	private static final Logger Log = LoggerFactory.getLogger(SellerController.class);
	@Autowired
	private SellerService sellerService;
	@RequestMapping(value = "selectsellertype",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	//http://localhost:8081/shanduonewretail/jseller/selectsellertype?lon=113.074815&lat=28.227615
	public ResultBean selectSellerType(HttpServletRequest request, String lat,String lon) {
		if(StringUtils.isNull(lon) || PatternUtils.patternLatitude(lon)) {
			Log.warn("经度格式错误");
			return new ErrorBean(ErrorConsts.CODE_10002,"经度格式错误");
		}
		if(StringUtils.isNull(lat) || PatternUtils.patternLatitude(lat)) {
			Log.warn("纬度格式错误");
			return new ErrorBean(ErrorConsts.CODE_10002,"纬度格式错误");
		}
		List<String>	sellerTypeList = new ArrayList<String>();
		try {
				sellerTypeList = sellerService.selectNearbySellerType(new Double(lon), new Double(lat));
		} catch (Exception e) {
			return new ErrorBean(ErrorConsts.CODE_10004,"查询失败");
		}
		
		return new SuccessBean(sellerTypeList);
	}
	@RequestMapping(value = "selectseller",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	//http://localhost:8081/shanduonewretail/jseller/selectseller?lon=113.074815&lat=28.227615&sellerType=[0,1,2]
	public ResultBean selectseller(HttpServletRequest request, String lat,String lon,List<String> sellerType) {
		if(StringUtils.isNull(lon) || PatternUtils.patternLatitude(lon)) {
			Log.warn("经度格式错误");
			return new ErrorBean(ErrorConsts.CODE_10002,"经度格式错误");
		}
		if(StringUtils.isNull(lat) || PatternUtils.patternLatitude(lat)) {
			Log.warn("纬度格式错误");
			return new ErrorBean(ErrorConsts.CODE_10002,"纬度格式错误");
		}
		if(sellerType.isEmpty()){
			Log.warn("无店铺类别");
			return new ErrorBean(ErrorConsts.CODE_10002,"无店铺类别");
		}
		List<Map<String, List<SellerInfo>>> sellerInfoList = new ArrayList<Map<String, List<SellerInfo>>>();
		try {
			sellerInfoList = sellerService.selectNearbySeller(new Double(lon), new Double(lat),sellerType);
		} catch (Exception e) {
			return new ErrorBean(ErrorConsts.CODE_10004,"查询失败");
		}
		
		return new SuccessBean(sellerInfoList);
	}
	
	
}
