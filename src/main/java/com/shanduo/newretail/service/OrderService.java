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

	String saveOrder(Map<String, Object> parameterMap);
	
	ToOrder getUnpaidOrder(String orderId);
}
