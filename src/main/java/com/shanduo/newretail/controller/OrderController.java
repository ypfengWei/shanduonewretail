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
import com.shanduo.newretail.service.OrderService;
import com.shanduo.newretail.util.JsonStringUtils;
import com.shanduo.newretail.util.StringUtils;

/**
 * 订单接口层
 * @ClassName: OrderController
 * @Description: TODO
 * @author fanshixin
 * @date 2018年7月13日 下午2:07:47
 *
 */
@Controller
@RequestMapping(value = "jorder")
public class OrderController {

	private static final Logger log = LoggerFactory.getLogger(OrderController.class);
	
	@Autowired
	private OrderService orderService;
	
	@RequestMapping(value = "payorder",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean payOrder(HttpServletRequest request, String data) {
		if(StringUtils.isNull(data)) {
			return new ErrorBean(ErrorConsts.CODE_10002, "参数为空");
		}
		Map<String, Object> parameter = new HashMap<>();
		try {
			parameter = JsonStringUtils.getMap(data);
		} catch (Exception e) {
			return new ErrorBean(ErrorConsts.CODE_10002, "参数错误");
		}
		if(parameter.isEmpty()) {
			return new ErrorBean(ErrorConsts.CODE_10002, "参数错误");
		}
		String orderId = "";
		try {
			orderId = orderService.saveOrder(parameter);
		} catch (Exception e) {
			return new ErrorBean(ErrorConsts.CODE_10004, "订单错误");
		}
		
		return null;
	}
	
}
