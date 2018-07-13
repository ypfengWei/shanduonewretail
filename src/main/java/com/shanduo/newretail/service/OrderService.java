package com.shanduo.newretail.service;

import java.util.Map;

import com.shanduo.newretail.entity.ToOrder;

/**
 * 订单业务层
 * @ClassName: OrderService
 * @Description: TODO
 * @author fanshixin
 * @date 2018年7月13日 上午10:29:37
 *
 */
public interface OrderService {

	/**
	 * 生成订单
	 * @Title: saveOrder
	 * @Description: TODO
	 * @param @param parameterMap
	 * @param @return
	 * @return String
	 * @throws
	 */
	String saveOrder(Map<String, Object> parameterMap);
	
	/**
	 * 查询待支付的订单
	 * @Title: getUnpaidOrder
	 * @Description: TODO
	 * @param @param orderId
	 * @param @return
	 * @return ToOrder
	 * @throws
	 */
	ToOrder getUnpaidOrder(String orderId);
}
