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
import com.shanduo.newretail.service.BaseService;
import com.shanduo.newretail.service.CommodityService;
import com.shanduo.newretail.util.StringUtils;

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
	//http://localhost:8081/shanduonewretail/jcommodity/selectcommoditytype?id=1&typeId=1
	public ResultBean selectCommodityType(HttpServletRequest request, String id,String typeId,String token) {
		if(StringUtils.isNull(typeId) ) {
			Log.warn("typeId错误");
			return new ErrorBean(ErrorConsts.CODE_10002,"参数为空");
		}
		if("1".equals(typeId)){
			if(StringUtils.isNull(id) ) {
				Log.warn("id错误");
				return new ErrorBean(ErrorConsts.CODE_10002,"参数为空");
			}
		}
		if("2".equals(typeId)){
			if(StringUtils.isNull(token)) {
				Log.warn("token为空");
				return new ErrorBean(ErrorConsts.CODE_10002,"token为空");
			}
			id = baseService.checkUserToken(token);
			if(null==id){
				Log.warn("token失效");
				return new ErrorBean(ErrorConsts.CODE_10001,"token失效");
			}
		}
		List<Map<String,Object>> categoryIdList = new ArrayList<Map<String,Object>>();
		try {
			categoryIdList = commodityService.selectSellerCommodityType(id,typeId);
			if(categoryIdList.isEmpty()){
				return new ErrorBean();
			}
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
	//http://localhost:8081/shanduonewretail/jcommodity/selectcommodity?id=1&categoryId=
	public ResultBean selectCommodity(HttpServletRequest request, String id,String categoryId, String page, String pageSize) {
		if(StringUtils.isNull(id) ) {
			Log.warn("id错误");
			return new ErrorBean(ErrorConsts.CODE_10002,"参数为空");
		}
		if(null==categoryId){
			Log.warn("categoryId错误");
			return new ErrorBean(ErrorConsts.CODE_10002,"参数为空");
		}
		if(StringUtils.isNull(page) || !page.matches("^\\d*$")) {
			Log.warn("page is error waith page:{}", page);
			return new ErrorBean(ErrorConsts.CODE_10002, "页码错误");
		}
		if(StringUtils.isNull(pageSize) || !pageSize.matches("^\\d*$")) {
			Log.warn("pageSize is error waith pageSize:{}", pageSize);
			return new ErrorBean(ErrorConsts.CODE_10002, "记录错误");
		}
		try {
			
			Map<String, Object> resultMap = new HashMap<String, Object>(3);
			resultMap = commodityService.selectCommodity(Integer.valueOf(categoryId), id,Integer.valueOf(page),Integer.valueOf(pageSize));
			if(resultMap.isEmpty()){
				return new ErrorBean();
			}
			return new SuccessBean(resultMap);
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
