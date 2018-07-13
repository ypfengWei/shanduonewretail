package com.shanduo.newretail.controller;

import java.util.ArrayList;
import java.util.List;

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
import com.shanduo.newretail.service.CommodityService;
import com.shanduo.newretail.util.StringUtils;

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
		List<Integer> commodityTypeList = new ArrayList<Integer>();
		try {
			commodityTypeList = commodityService.selectSellerCommodityType(id);
	} catch (Exception e) {
		return new ErrorBean(ErrorConsts.CODE_10004,"查询失败");
	}
		return new SuccessBean(commodityTypeList);
		
	}

}
