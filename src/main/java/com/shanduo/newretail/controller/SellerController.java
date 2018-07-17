package com.shanduo.newretail.controller;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.shanduo.newretail.entity.UserSeller;
import com.shanduo.newretail.entity.common.ErrorBean;
import com.shanduo.newretail.entity.common.ResultBean;
import com.shanduo.newretail.entity.common.SuccessBean;
import com.shanduo.newretail.entity.service.SellerInfo;
import com.shanduo.newretail.service.BaseService;
import com.shanduo.newretail.service.SellerService;
import com.shanduo.newretail.util.JsonStringUtils;
import com.shanduo.newretail.util.PatternUtils;
import com.shanduo.newretail.util.StringUtils;

import net.sf.json.JSONArray;


@Controller
@RequestMapping(value = "jseller")
public class SellerController {
	private static final Logger Log = LoggerFactory.getLogger(SellerController.class);
	@Autowired
	private SellerService sellerService;
	@Autowired
	private BaseService baseService;
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
<<<<<<< HEAD
		List<String>	sellerTypeList = new ArrayList<String>();
=======
		List<Map<String,Object>>	sellerTypeList = new ArrayList<Map<String,Object>>();
>>>>>>> 292a584f24f9808b854bb1946a748c8c915d6d14
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
<<<<<<< HEAD
	public ResultBean selectseller(HttpServletRequest request, String lat,String lon) {
=======
	public ResultBean selectseller(HttpServletRequest request, String lat,String lon,String sellerType) {
>>>>>>> 292a584f24f9808b854bb1946a748c8c915d6d14
		if(StringUtils.isNull(lon) || PatternUtils.patternLatitude(lon)) {
			Log.warn("经度格式错误");
			return new ErrorBean(ErrorConsts.CODE_10002,"经度格式错误");
		}
		if(StringUtils.isNull(lat) || PatternUtils.patternLatitude(lat)) {
			Log.warn("纬度格式错误");
			return new ErrorBean(ErrorConsts.CODE_10002,"纬度格式错误");
		}
<<<<<<< HEAD
		
		List<Map<String, List<SellerInfo>>> sellerInfoList = new ArrayList<Map<String, List<SellerInfo>>>();
		try {
			String sellerType = request.getParameter("sellerTypeList");
			JSONArray jsonArray = JSONArray.fromObject(sellerType);
			@SuppressWarnings("unchecked")
			List<String>sellerTypeList = (List<String>) JSONArray.toCollection(jsonArray, Map.class);
			if(sellerTypeList.isEmpty()){
				Log.warn("无店铺类别");
				return new ErrorBean(ErrorConsts.CODE_10002,"无店铺类别");
			}
			sellerInfoList = sellerService.selectNearbySeller(new Double(lon), new Double(lat),sellerTypeList);
=======
		if(StringUtils.isNull(sellerType) ) {
			Log.warn("无店铺类别");
			return new ErrorBean(ErrorConsts.CODE_10002,"无店铺类别");
		}
		
		Map<String, List<SellerInfo>> sellerInfoMap = new HashMap<String, List<SellerInfo>>();
		try {
			
			sellerInfoMap = sellerService.selectNearbySellerOneType(new Double(lon), new Double(lat),sellerType);
>>>>>>> 292a584f24f9808b854bb1946a748c8c915d6d14
		} catch (Exception e) {
			return new ErrorBean(ErrorConsts.CODE_10004,"查询失败");
		}
		
<<<<<<< HEAD
		return new SuccessBean(sellerInfoList);
=======
		return new SuccessBean(sellerInfoMap);
>>>>>>> 292a584f24f9808b854bb1946a748c8c915d6d14
	}
	/*
	 * 查询店铺详情
	 */
	@RequestMapping(value = "selectsellerdetails",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	//http://localhost:8081/shanduonewretail/jseller/selectsellerdetails?token=1
	public ResultBean selectSellerDetails(HttpServletRequest request, String token) {
		if(StringUtils.isNull(token)) {
			Log.warn("token为空");
			return new ErrorBean(ErrorConsts.CODE_10002,"token为空");
		}
		String id = baseService.checkUserToken(token);
		if(null==id){
			Log.warn("token失效");
			return new ErrorBean(ErrorConsts.CODE_10001,"token失效");
		}
		UserSeller userSeller = new UserSeller();
		try {
			userSeller  = sellerService.selectSellerDetails(id);
		} catch (Exception e) {
			return new ErrorBean(ErrorConsts.CODE_10004,"查询失败");
		}
		return new SuccessBean(userSeller);
	}
	
	/*
	 * 修改店铺详情
	 */
	@RequestMapping(value = "updatesellerdetails",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	//http://localhost:8081/shanduonewretail/jseller/selectsellerdetails?id=1
	public ResultBean updateSellerDetails(HttpServletRequest request,String token) {
		if(StringUtils.isNull(token)) {
			Log.warn("token为空");
			return new ErrorBean(ErrorConsts.CODE_10002,"token为空");
		}
		String id = baseService.checkUserToken(token);
		if(null==id){
			Log.warn("token失效");
			return new ErrorBean(ErrorConsts.CODE_10001,"token失效");
		}
		String userSeller = request.getParameter("userSeller");
		if(StringUtils.isNull(userSeller)) {
			Log.warn("用户信息为空");
			return new ErrorBean(ErrorConsts.CODE_10002,"用户信息为空");
		}
		Map<String, Object> userSellerMap = new HashMap<String, Object>();
		userSellerMap = JsonStringUtils.getMap(userSeller);
		userSellerMap.put("id", id);
		if(StringUtils.isNull(userSellerMap.get("phone")+"") || PatternUtils.patternPhone(userSellerMap.get("phone").toString())){
			Log.warn("电话号码错误");
			return new ErrorBean(ErrorConsts.CODE_10002,"电话号码错误");
		}
		try {
			int count  = sellerService.updateSellerDetails(userSellerMap);
			if(count<1){
				return new ErrorBean(ErrorConsts.CODE_10004,"修改失败");
			}
		} catch (Exception e) {
			return new ErrorBean(ErrorConsts.CODE_10004,"修改失败");
		}
		return new SuccessBean("修改成功");
	}
	
	/*
	 * 开店关店
	 */
	@RequestMapping(value = "updatebusinesssign",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	//http://localhost:8081/shanduonewretail/jseller/updatebusinesssign?token=1&businessSign=1
	public ResultBean updateBusinessSign(HttpServletRequest request,String token,String businessSign) {
		if(StringUtils.isNull(token)) {
			Log.warn("token为空");
			return new ErrorBean(ErrorConsts.CODE_10002,"token为空");
		}
		String id = baseService.checkUserToken(token);
		if(null==id){
			Log.warn("token失效");
			return new ErrorBean(ErrorConsts.CODE_10001,"token失效");
		}
		try {
			int count  = sellerService.updateBusinessSign(businessSign, id);
			if(count<1){
				return new ErrorBean(ErrorConsts.CODE_10004,"修改失败");
			}
		} catch (Exception e) {
			return new ErrorBean(ErrorConsts.CODE_10004,"修改失败");
		}
		return new SuccessBean("修改成功");
	}
}
