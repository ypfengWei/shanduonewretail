package com.shanduo.newretail.service;

import java.util.List;
import java.util.Map;

import com.shanduo.newretail.entity.ToOrder;
import com.shanduo.newretail.entity.ToOrderDetails;

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
	 * 查询订单
	 * @Title: getOrder
	 * @Description: TODO
	 * @param @param orderId
	 * @param @param typeId
	 * @param @return
	 * @return ToOrder
	 * @throws
	 */
	ToOrder getOrder(String orderId,String typeId);
	
	/**
	 * 订单详情
	 * @Title: listOrderId
	 * @Description: TODO
	 * @param @param orderId
	 * @param @return
	 * @return List<ToOrderDetails>
	 * @throws
	 */
	List<ToOrderDetails> listOrderId(String orderId);
	
	/**
	 * 支付订单
	 * @Title: updatePayOrder
	 * @Description: TODO
	 * @param @param orderId
	 * @param @return
	 * @return int
	 * @throws
	 */
	int updatePayOrder(String orderId);
	
	/**
	 * 接单
	 * @Title: updateReceivingOrder
	 * @Description: TODO
	 * @param @param orderId
	 * @param @param sellerId
	 * @param @return
	 * @return int
	 * @throws
	 */
	int updateReceivingOrder(String orderId,String sellerId);
	
	/**
	 * 申请退款中
	 * @Title: updateCancelOrder
	 * @Description: TODO
	 * @param @param orderId
	 * @param @param sellerId
	 * @param @return
	 * @return int
	 * @throws
	 */
	int updateCancelOrder(String orderId,String sellerId);
	
	/**
	 * 退款回调处理
	 * @Title: updateCancelOrder
	 * @Description: TODO
	 * @param @param orderId
	 * @param @return
	 * @return int
	 * @throws
	 */
	int updateCancelOrder(String orderId);
	
	/**
	 * 完成订单
	 * @Title: updateFinishOrder
	 * @Description: TODO
	 * @param @param orderId
	 * @param @param sellerId
	 * @param @return
	 * @return int
	 * @throws
	 */
	int updateFinishOrder(String orderId,String sellerId);
	
	/**
	 * 条件分页查询卖家订单
	 * @Title: listSellerOrder
	 * @Description: TODO
	 * @param @param sellerId
	 * @param @param state
	 * @param @param startDate
	 * @param @param endDate
	 * @param @param pageNum
	 * @param @param pageSize
	 * @param @return
	 * @return Map<String,Object>
	 * @throws
	 */
	Map<String, Object> listSellerOrder(String sellerId, String state, String startDate, String endDate, Integer pageNum, Integer pageSize);
	
	/**
	 * 卖家条件分页查询商品销售数量
	 * @Title: listSellerCommodity
	 * @Description: TODO
	 * @param @param sellerId
	 * @param @param categoryId
	 * @param @param startDate
	 * @param @param endDate
	 * @param @param pageNum
	 * @param @param pageSize
	 * @param @return
	 * @return Map<String,Object>
	 * @throws
	 */
	Map<String, Object> listSellerCommodity(String sellerId, String categoryId, String startDate, String endDate, Integer pageNum, Integer pageSize);
	
	/**
	 * 卖家条件查询商品销售金额
	 * @Title: sumSellerMoney
	 * @Description: TODO
	 * @param @param sellerId
	 * @param @param categoryId
	 * @param @param startDate
	 * @param @param endDate
	 * @param @return
	 * @return Double
	 * @throws
	 */
	Double sumSellerMoney(String sellerId, String categoryId, String startDate, String endDate);
	
	/**
	 * 查询所有未处理的订单
	 * @Title: listPending
	 * @Description: TODO
	 * @param @return
	 * @return List<String>
	 * @throws
	 */
	List<String> listPending();
}
