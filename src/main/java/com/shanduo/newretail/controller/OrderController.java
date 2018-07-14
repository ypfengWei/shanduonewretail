package com.shanduo.newretail.controller;

import java.math.BigDecimal;
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
import com.shanduo.newretail.consts.WxPayConsts;
import com.shanduo.newretail.entity.ToOrder;
import com.shanduo.newretail.entity.ToOrderDetails;
import com.shanduo.newretail.entity.common.ErrorBean;
import com.shanduo.newretail.entity.common.ResultBean;
import com.shanduo.newretail.entity.common.SuccessBean;
import com.shanduo.newretail.service.BaseService;
import com.shanduo.newretail.service.OrderService;
import com.shanduo.newretail.util.ClientCustomSSL;
import com.shanduo.newretail.util.IpUtils;
import com.shanduo.newretail.util.JsonStringUtils;
import com.shanduo.newretail.util.StringUtils;
import com.shanduo.newretail.util.UUIDGenerator;
import com.shanduo.newretail.util.WxPayUtils;

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
	private BaseService baseService;
	@Autowired
	private OrderService orderService;
	
	/**
	 * 支付订单
	 * @Title: payOrder
	 * @Description: TODO
	 * @param @param request
	 * @param @param data
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
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
		return payOrder(orderId, request);
	}
	
	/**
	 * 商家接单
	 * @Title: receivingOrder
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param orderId
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "receivingorder",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean receivingOrder(HttpServletRequest request, String token, String orderId) {
		String sellerId = baseService.checkUserToken(token);
		if(sellerId == null) {
			return new ErrorBean(ErrorConsts.CODE_10001, "请重新登录");
		}
		if(StringUtils.isNull(orderId)) {
			log.warn("orderId is null");
			return new ErrorBean(ErrorConsts.CODE_10002, "参数错误");
		}
		ToOrder order = orderService.getOrder(orderId, "2");
		if(order == null) {
			log.warn("orderId is error waith orderId:{}", orderId);
			return new ErrorBean(ErrorConsts.CODE_10003, "订单错误");
		}
		int i = orderService.updateReceivingOrder(orderId, sellerId);
		if(i < 1) {
			log.warn("orderId is error waith orderId:{}", orderId);
			return new ErrorBean(ErrorConsts.CODE_10004, "接单失败");
		}
		return new SuccessBean("接单成功");
	}
	
	/**
	 * 商家完成订单
	 * @Title: finishOrder
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param orderId
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "finishorder",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean finishOrder(HttpServletRequest request, String token, String orderId) {
		String sellerId = baseService.checkUserToken(token);
		if(sellerId == null) {
			return new ErrorBean(ErrorConsts.CODE_10001, "请重新登录");
		}
		if(StringUtils.isNull(orderId)) {
			log.warn("orderId is null");
			return new ErrorBean(ErrorConsts.CODE_10002, "参数错误");
		}
		ToOrder order = orderService.getOrder(orderId, "3");
		if(order == null) {
			log.warn("orderId is error waith orderId:{}", orderId);
			return new ErrorBean(ErrorConsts.CODE_10003, "订单错误");
		}
		try {
			orderService.updateFinishOrder(orderId, sellerId);
		} catch (Exception e) {
			log.warn("orderId is error waith orderId:{}", orderId);
			return new ErrorBean(ErrorConsts.CODE_10004, "完成失败");
		}
		return new SuccessBean("订单完成");
	}
	
	/**
	 * 申请退款
	 * @Title: cancelOrder
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param orderId
	 * @param @return
	 * @param @throws Exception
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "cancelorder",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean cancelOrder(HttpServletRequest request, String token, String orderId) throws Exception {
		String sellerId = baseService.checkUserToken(token);
		if(sellerId == null) {
			return new ErrorBean(ErrorConsts.CODE_10001, "请重新登录");
		}
		if(StringUtils.isNull(orderId)) {
			log.warn("orderId is null");
			return new ErrorBean(ErrorConsts.CODE_10002, "参数错误");
		}
		ToOrder order = orderService.getOrder(orderId, "2");
		if(order == null) {
			log.warn("orderId is error waith orderId:{}", orderId);
			return new ErrorBean(ErrorConsts.CODE_10003, "订单错误");
		}
		//价格，单位为分
		BigDecimal amount = order.getTotalPrice();
		amount = amount.multiply(new BigDecimal("100"));
		//订单总金额
		Integer moneys = amount.intValue();
		Map<String, String> paramsMap = new HashMap<>(10);
		paramsMap.put("appid", WxPayConsts.APPID);
		paramsMap.put("mch_id", WxPayConsts.MCH_ID);
		paramsMap.put("nonce_str", UUIDGenerator.getUUID());
		paramsMap.put("out_trade_no", order.getId());
		paramsMap.put("out_refund_no", order.getId());
		paramsMap.put("total_fee", moneys.toString());
		paramsMap.put("refund_fee", moneys.toString());
		paramsMap.put("notify_url", WxPayConsts.CANCEL_URL);
		//把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
		String paramsString = WxPayUtils.createLinkString(paramsMap);
		//MD5运算生成签名
		String sign = WxPayUtils.sign(paramsString, WxPayConsts.KEY, "utf-8").toUpperCase();
		//签名
		paramsMap.put("sign", sign);
		String paramsXml = WxPayUtils.map2Xmlstring(paramsMap);
		String result = ClientCustomSSL.doRefund(WxPayConsts.REFUND_URL, paramsXml);
		Map<String, Object> resultMap = WxPayUtils.Str2Map(result);
		String returnCode = resultMap.get("return_code").toString();
		if(!returnCode.equals("SUCCESS")) {
			log.error(resultMap.get("return_msg").toString());
			return new ErrorBean(ErrorConsts.CODE_10004,"申请失败");
		}
		String resultCode = resultMap.get("result_code").toString();
		if(!resultCode.equals("SUCCESS")) {
			log.error(resultMap.get("err_code_des").toString());
			return new ErrorBean(ErrorConsts.CODE_10004,"申请失败");
		}
		int i = orderService.updateCancelOrder(orderId, sellerId);
		if(i < 1) {
			log.warn("orderId is error waith orderId:{}", orderId);
			return new ErrorBean(ErrorConsts.CODE_10004, "申请失败");
		}
		return new SuccessBean("申请成功");
	}
	
	
	/**
	 * 微信统一下单
	 * @Title: payOrder
	 * @Description: TODO
	 * @param @param orderId
	 * @param @param request
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	private ResultBean payOrder(String orderId, HttpServletRequest request) {
		ToOrder order = orderService.getOrder(orderId, "1");
		List<ToOrderDetails> list = orderService.listOrderId(orderId);
		StringBuilder body = new StringBuilder();
		for (ToOrderDetails orderDetails : list) {
			body.append(orderDetails.getCommodityName());
		}
		String bodys = body.toString();
		if(bodys.length() > 32) {
			bodys = bodys.substring(0, 31);
		}
		//价格，单位为分
		BigDecimal amount = order.getTotalPrice();
		amount = amount.multiply(new BigDecimal("100"));
		//订单总金额
		Integer moneys = amount.intValue();
		Map<String, String> paramsMap = new HashMap<>(10);
		paramsMap.put("appid", WxPayConsts.APPID);
		paramsMap.put("mch_id", WxPayConsts.MCH_ID);
		paramsMap.put("nonce_str", UUIDGenerator.getUUID());
		paramsMap.put("body", bodys);
		paramsMap.put("out_trade_no", order.getId());
		paramsMap.put("total_fee", moneys.toString());
		paramsMap.put("spbill_create_ip", IpUtils.getIpAddress(request));
		paramsMap.put("notify_url", WxPayConsts.NOTIFY_URL);
		paramsMap.put("trade_type", WxPayConsts.TRADETYPE);
		paramsMap.put("openid", order.getOpenid());
		//把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
		String paramsString = WxPayUtils.createLinkString(paramsMap);
		//MD5运算生成签名
		String sign = WxPayUtils.sign(paramsString, WxPayConsts.KEY, "utf-8").toUpperCase();
		//签名
		paramsMap.put("sign", sign);
		String paramsXml = WxPayUtils.map2Xmlstring(paramsMap);
		String result = WxPayUtils.httpRequest(WxPayConsts.PAY_URL, "POST", paramsXml);
		Map<String, Object> resultMap = WxPayUtils.Str2Map(result);
		String returnCode = resultMap.get("return_code").toString();
		if(!returnCode.equals("SUCCESS")) {
			log.error(resultMap.get("return_msg").toString());
			return new ErrorBean(ErrorConsts.CODE_10004,"支付失败");
		}
		String resultCode = resultMap.get("result_code").toString();
		if(!resultCode.equals("SUCCESS")) {
			log.error(resultMap.get("err_code_des").toString());
			return new ErrorBean(ErrorConsts.CODE_10004,"支付失败");
		}
		String prepayId = resultMap.get("prepay_id").toString();
		Map<String, String> responseMap = new HashMap<String, String>(7);
		responseMap.put("appid", WxPayConsts.APPID);
		responseMap.put("prepayid", "prepay_id=" + prepayId);
		responseMap.put("noncestr", UUIDGenerator.getUUID());
		Long timeStamp = System.currentTimeMillis() / 1000;
		responseMap.put("timeStamp", timeStamp + "");
		responseMap.put("signType", "MD5");
		//把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
		String responseString = WxPayUtils.createLinkString(responseMap);
		//MD5运算生成签名
		String responseSign = WxPayUtils.sign(responseString, WxPayConsts.KEY, "utf-8").toUpperCase();
		responseMap.put("sign", responseSign);
		return new SuccessBean(responseMap);
	}
}
