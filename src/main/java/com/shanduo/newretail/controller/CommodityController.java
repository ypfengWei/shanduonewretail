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
import com.shanduo.newretail.entity.common.ErrorBean;
import com.shanduo.newretail.entity.common.ResultBean;
import com.shanduo.newretail.entity.common.SuccessBean;
import com.shanduo.newretail.entity.service.CommodityInfo;
import com.shanduo.newretail.service.BaseService;
import com.shanduo.newretail.service.CommodityService;
import com.shanduo.newretail.util.StringUtils;
import net.sf.json.JSONArray;

@Controller
@RequestMapping(value = "jcommodity")
public class CommodityController {
	private static final Logger Log = LoggerFactory.getLogger(CommodityController.class);
	@Autowired
	private CommodityService commodityService;
	@Autowired
	private BaseService baseService;
	/*
	 * 查询店铺商品类别
	 */
	@RequestMapping(value = "selectcommoditytype",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	//http://localhost:8081/shanduonewretail/jcommodity/selectcommoditytype?id=1
	public ResultBean selectCommodityType(HttpServletRequest request, String id) {
		if(StringUtils.isNull(id) ) {
			Log.warn("id错误");
			return new ErrorBean(ErrorConsts.CODE_10002,"参数为空");
		}
<<<<<<< HEAD
		List<Integer> categoryIdList = new ArrayList<Integer>();
=======
		List<Map<String,Object>> categoryIdList = new ArrayList<Map<String,Object>>();
>>>>>>> 292a584f24f9808b854bb1946a748c8c915d6d14
		try {
			categoryIdList = commodityService.selectSellerCommodityType(id);
	} catch (Exception e) {
		return new ErrorBean(ErrorConsts.CODE_10004,"查询失败");
	}
		return new SuccessBean(categoryIdList);
		
	}
	/*
	 * 查询店铺所有商品
	 */
	@RequestMapping(value = "selectcommodity",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
<<<<<<< HEAD
	//http://localhost:8081/shanduonewretail/jcommodity/selectcommodity?id=1&categoryIdList=
	public ResultBean selectCommodity(HttpServletRequest request, String id) {
=======
	//http://localhost:8081/shanduonewretail/jcommodity/selectcommodity?id=1&categoryId=
	public ResultBean selectCommodity(HttpServletRequest request, String id,String categoryId) {
>>>>>>> 292a584f24f9808b854bb1946a748c8c915d6d14
		if(StringUtils.isNull(id) ) {
			Log.warn("id错误");
			return new ErrorBean(ErrorConsts.CODE_10002,"参数为空");
		}
<<<<<<< HEAD
		try {
			String categoryId = request.getParameter("categoryIdList");
			JSONArray jsonArray = JSONArray.fromObject(categoryId);
			@SuppressWarnings("unchecked")
			List<Integer>categoryIdList = (List<Integer>) JSONArray.toCollection(jsonArray, Map.class);
			if(null==categoryIdList || categoryIdList.isEmpty()){
				Log.warn("categoryIdList错误");
				return new ErrorBean(ErrorConsts.CODE_10002,"参数为空");
			}
			Map<Integer, List<CommodityInfo>> commodityMap = new HashMap<Integer, List<CommodityInfo>>();
			commodityMap = commodityService.selectCommodity(categoryIdList, id);
			return new SuccessBean(commodityMap);
=======
		if(null==categoryId){
			Log.warn("categoryId错误");
			return new ErrorBean(ErrorConsts.CODE_10002,"参数为空");
		}
		try {
			
			List<CommodityInfo> commodityInfo = new ArrayList<CommodityInfo>(); 
			commodityInfo = commodityService.selectCommodity(Integer.valueOf(categoryId), id);
			return new SuccessBean(commodityInfo);
>>>>>>> 292a584f24f9808b854bb1946a748c8c915d6d14
		} catch (Exception e) {
			return new ErrorBean(ErrorConsts.CODE_10004,"查询失败");
		}
	}
	/*
	 * 商品上下架
	 */
	@RequestMapping(value = "updatecommodityvisible",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	//http://localhost:8081/shanduonewretail/jcommodity/updatecommodityvisible?token=1&visible=1&commodityId=1
	public ResultBean updateCommodityVisible(HttpServletRequest request, String token,String visible,String commodityId) {
		if(StringUtils.isNull(commodityId) ) {
			Log.warn("commodityId为空");
			return new ErrorBean(ErrorConsts.CODE_10002,"commodityId为空");
		}
		if(StringUtils.isNull(visible) || !visible.matches("^[01]$")) {
			Log.error("visible错误");
			return new ErrorBean(ErrorConsts.CODE_10002,"visible错误");
		}
		if(StringUtils.isNull(token)) {
			Log.warn("token为空");
			return new ErrorBean(ErrorConsts.CODE_10002,"token为空");
		}
		String id = baseService.checkUserToken(token);
		
		try {
			int count = commodityService.updateCommodityVisible(commodityId, id, Integer.valueOf(visible));
			if(count<1){
				return new ErrorBean(ErrorConsts.CODE_10004,"修改失败");
			}
		} catch (Exception e) {
			return new ErrorBean(ErrorConsts.CODE_10004,"修改失败");
		}
		return new SuccessBean("修改成功");
		
	}

}
