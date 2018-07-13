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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shanduo.newretail.consts.ErrorConsts;
import com.shanduo.newretail.entity.common.ErrorBean;
import com.shanduo.newretail.entity.common.ResultBean;
import com.shanduo.newretail.entity.common.SuccessBean;
import com.shanduo.newretail.entity.service.CommodityInfo;
import com.shanduo.newretail.service.CommodityService;
import com.shanduo.newretail.util.StringUtils;

import net.sf.json.JSONArray;

@Controller
@RequestMapping(value = "jcommodity")
public class CommodityController {
	private static final Logger Log = LoggerFactory.getLogger(CommodityController.class);
	@Autowired
	private CommodityService commodityService;
	@RequestMapping(value = "selectcommoditytype",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	//http://localhost:8081/shanduonewretail/jcommodity/selectcommoditytype?id=1
	public ResultBean selectCommodityType(HttpServletRequest request, String id) {
		if(StringUtils.isNull(id) ) {
			Log.warn("id错误");
			return new ErrorBean(ErrorConsts.CODE_10002,"参数为空");
		}
		List<Integer> categoryIdList = new ArrayList<Integer>();
		try {
			categoryIdList = commodityService.selectSellerCommodityType(id);
	} catch (Exception e) {
		return new ErrorBean(ErrorConsts.CODE_10004,"查询失败");
	}
		return new SuccessBean(categoryIdList);
		
	}
	
	@RequestMapping(value = "selectcommodity",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	//http://localhost:8081/shanduonewretail/jcommodity/selectcommodity?id=1&categoryIdList=
	public ResultBean selectCommodity(HttpServletRequest request, String id) {
		if(StringUtils.isNull(id) ) {
			Log.warn("id错误");
			return new ErrorBean(ErrorConsts.CODE_10002,"参数为空");
		}
		
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
	} catch (Exception e) {
		return new ErrorBean(ErrorConsts.CODE_10004,"查询失败");
	}
	
		
	}

}
