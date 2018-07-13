package com.shanduo.newretail.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.newretail.entity.Commodity;
import com.shanduo.newretail.entity.ToOrder;
import com.shanduo.newretail.entity.ToOrderDetails;
import com.shanduo.newretail.mapper.CommodityMapper;
import com.shanduo.newretail.mapper.ToOrderDetailsMapper;
import com.shanduo.newretail.mapper.ToOrderMapper;
import com.shanduo.newretail.service.CommodityService;
import com.shanduo.newretail.service.OrderService;
import com.shanduo.newretail.service.SellerService;
import com.shanduo.newretail.util.UUIDGenerator;

/**
 * 
 * @ClassName: OrderServiceImpl
 * @Description: TODO
 * @author fanshixin
 * @date 2018年7月13日 上午10:31:10
 *
 */
@Service
public class OrderServiceImpl implements OrderService {

	private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

	@Autowired
	private ToOrderMapper orderMapper;
	@Autowired
	private ToOrderDetailsMapper orderDetailsMapper;
	@Autowired
	private CommodityMapper commodityMapper;
	@Autowired
	private SellerService sellerService;
	@Autowired
	private CommodityService commodityService;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public String saveOrder(Map<String, Object> parameterMap) {
		String orderId = UUIDGenerator.getUUID();
		String sellerId = parameterMap.get("sellerId").toString();
		//检查店铺是否休息或不在营业时间段内
		if(!sellerService.selectBusinessSign(sellerId)) {
			return "0";
		}
		BigDecimal totalPrice = new BigDecimal("0");
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> parameterList = (List<Map<String, Object>>) parameterMap.get("list");
		for (Map<String, Object> map : parameterList) {
			String commodityId = (String) map.get("commodityId");
			int number = (int) map.get("number");
			//检查商品库存是否删除是否下架是否足够再减库存
			if(!commodityService.selectCommodityStock(sellerId, commodityId, number)) {
				log.warn("库存不足");
				throw new RuntimeException();
			}
			commodityService.updateCommodityStock(sellerId, commodityId, number, "0");
			Commodity commodity = commodityMapper.selectByPrimaryKey(commodityId);
			ToOrderDetails orderDetails = new ToOrderDetails();
			orderDetails.setOrderId(orderId);
			orderDetails.setCommodityId(commodityId);
			orderDetails.setCommodityName(commodity.getName());
			orderDetails.setCommodityNumber(number);
			orderDetails.setPrice(commodity.getPrice());
			int i = orderDetailsMapper.insertSelective(orderDetails);
			if(i < 1) {
				log.warn("订单详情录入失败");
				throw new RuntimeException();
			}
			totalPrice = totalPrice.add(orderDetails.getPrice().multiply(new BigDecimal(number+"")));
		}
		ToOrder order = new ToOrder();
		order.setId(orderId);
		order.setOpenid(parameterMap.get("openId").toString());
		order.setSellerId(sellerId);
		order.setTotalPrice(totalPrice);
		order.setUserName(parameterMap.get("name").toString());
		order.setUserPhone(parameterMap.get("phone").toString());
		order.setUserAddress(parameterMap.get("address").toString());
		order.setRemarks(parameterMap.get("remarks")+"");
		int i = orderMapper.insertSelective(order);
		if(i < 1) {
			log.warn("订单录入失败");
			throw new RuntimeException();
		}
		return orderId;
	}

	@Override
	public ToOrder getUnpaidOrder(String orderId) {
		return orderMapper.getUnpaidOrder(orderId);
	}
	
}
