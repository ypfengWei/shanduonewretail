package com.shanduo.newretail.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
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

import com.alibaba.fastjson.JSONObject;
import com.shanduo.newretail.consts.ErrorConsts;
import com.shanduo.newretail.consts.WxPayConsts;
import com.shanduo.newretail.entity.ToOrder;
import com.shanduo.newretail.entity.ToOrderDetails;
import com.shanduo.newretail.service.BaseService;
import com.shanduo.newretail.service.OrderService;
import com.shanduo.newretail.service.SellerService;
import com.shanduo.newretail.util.ClientCustomSSL;
import com.shanduo.newretail.util.IpUtils;
import com.shanduo.newretail.util.JsonStringUtils;
import com.shanduo.newretail.util.PatternUtils;
import com.shanduo.newretail.util.ResultUtils;
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
	@Autowired
	private SellerService sellerService;
	
	/**
	 * 支付订单
	 * @Title: payOrder
	 * @Description: TODO
	 * @param @param request
	 * @param @param data
	 * @param @return
	 * @return JSONObject
	 * @throws
	 */
	@RequestMapping(value = "payorder",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public JSONObject payOrder(HttpServletRequest request, String data, String lat, String lon) {
		if(StringUtils.isNull(data)) {
			return ResultUtils.error(ErrorConsts.CODE_10002, "参数为空");
		}
		if(StringUtils.isNull(lon) || PatternUtils.patternLatitude(lon)) {
			log.warn("经度格式错误");
            return ResultUtils.error(ErrorConsts.CODE_10002, "经度格式错误");
        }
        if(StringUtils.isNull(lat) || PatternUtils.patternLatitude(lat)) {
        	log.warn("纬度格式错误");
            return ResultUtils.error(ErrorConsts.CODE_10002, "纬度格式错误");
        }
		Map<String, Object> parameter = new HashMap<>();
		try {
			parameter = JsonStringUtils.getMap(data);
		} catch (Exception e) {
			return ResultUtils.error(ErrorConsts.CODE_10002, "参数错误");
		}
		if(parameter.isEmpty()) {
			return ResultUtils.error(ErrorConsts.CODE_10002, "参数错误");
		}
		if(sellerService.checkLocation(parameter.get("sellerId").toString(), lat, lon) == 1) {
			return ResultUtils.error(ErrorConsts.CODE_10003, "超过配送范围");
		}
		//检查店铺是否休息或不在营业时间段内
		if(!sellerService.selectBusinessSign(parameter.get("sellerId").toString())) {
			return ResultUtils.error(ErrorConsts.CODE_10002, "店铺休息");
		}
		String orderId = "";
		try {
			orderId = orderService.saveOrder(parameter);
		} catch (Exception e) {
			return ResultUtils.error(ErrorConsts.CODE_10004, "订单错误");
		}
		return payOrder(orderId, request);
	}
	
	/**
	 * 商家接单
	 * @Title: receivingOrder
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param orderId 订单Id
	 * @param @return
	 * @return JSONObject
	 * @throws
	 */
	@RequestMapping(value = "receivingorder",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public JSONObject receivingOrder(HttpServletRequest request, String token, String orderId) {
		String sellerId = baseService.checkUserToken(token);
		if(sellerId == null) {
			return ResultUtils.error(ErrorConsts.CODE_10001, "请重新登录");
		}
		if(StringUtils.isNull(orderId)) {
			log.warn("orderId is null");
			return ResultUtils.error(ErrorConsts.CODE_10002, "参数错误");
		}
		ToOrder order = orderService.getOrder(orderId, "2");
		if(order == null) {
			log.warn("orderId is error waith orderId:{}", orderId);
			return ResultUtils.error(ErrorConsts.CODE_10003, "订单错误");
		}
		int i = orderService.updateReceivingOrder(orderId, sellerId);
		if(i < 1) {
			log.warn("orderId is error waith orderId:{}", orderId);
			return ResultUtils.error(ErrorConsts.CODE_10004, "接单失败");
		}
		return ResultUtils.success("接单成功");
	}
	
	/**
	 * 商家完成订单
	 * @Title: finishOrder
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param orderId
	 * @param @return
	 * @return JSONObject
	 * @throws
	 */
	@RequestMapping(value = "finishorder",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public JSONObject finishOrder(HttpServletRequest request, String token, String orderId) {
		String sellerId = baseService.checkUserToken(token);
		if(sellerId == null) {
			return ResultUtils.error(ErrorConsts.CODE_10001, "请重新登录");
		}
		if(StringUtils.isNull(orderId)) {
			log.warn("orderId is null");
			return ResultUtils.error(ErrorConsts.CODE_10002, "参数错误");
		}
		ToOrder order = orderService.getOrder(orderId, "3");
		if(order == null) {
			log.warn("orderId is error waith orderId:{}", orderId);
			return ResultUtils.error(ErrorConsts.CODE_10003, "订单错误");
		}
		try {
			orderService.updateFinishOrder(orderId, sellerId);
		} catch (Exception e) {
			log.warn("orderId is error waith orderId:{}", orderId);
			return ResultUtils.error(ErrorConsts.CODE_10004, "完成失败");
		}
		return ResultUtils.success("订单完成");
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
	 * @return JSONObject
	 * @throws
	 */
	@RequestMapping(value = "cancelorder",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public JSONObject cancelOrder(HttpServletRequest request, String token, String orderId) throws Exception {
		String sellerId = baseService.checkUserToken(token);
		if(sellerId == null) {
			return ResultUtils.error(ErrorConsts.CODE_10001, "请重新登录");
		}
		if(StringUtils.isNull(orderId)) {
			log.warn("orderId is null");
			return ResultUtils.error(ErrorConsts.CODE_10002, "参数错误");
		}
		ToOrder order = orderService.getOrder(orderId, "2");
		if(order == null) {
			log.warn("orderId is error waith orderId:{}", orderId);
			return ResultUtils.error(ErrorConsts.CODE_10003, "订单错误");
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
			return ResultUtils.error(ErrorConsts.CODE_10004,"申请失败");
		}
		String resultCode = resultMap.get("result_code").toString();
		if(!resultCode.equals("SUCCESS")) {
			log.error(resultMap.get("err_code_des").toString());
			return ResultUtils.error(ErrorConsts.CODE_10004,"申请失败");
		}
		int i = orderService.updateCancelOrder(orderId, sellerId);
		if(i < 1) {
			log.warn("orderId is error waith orderId:{}", orderId);
			return ResultUtils.error(ErrorConsts.CODE_10004, "申请失败");
		}
		return ResultUtils.success("申请成功");
	}
	
	/**
	 * 卖家条件分页查询订单
	 * @Title: orderList
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param state 订单状态
	 * @param @param startDate 开始时间
	 * @param @param endDate 结束时间
	 * @param @param page 页数
	 * @param @param pageSize 记录数
	 * @param @return
	 * @return JSONObject
	 * @throws
	 */
	@RequestMapping(value = "orderList",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public JSONObject orderList(HttpServletRequest request, String token, String state, String startDate, 
			String endDate, String page, String pageSize) {
		String sellerId = baseService.checkUserToken(token);
		if(sellerId == null) {
			return ResultUtils.error(ErrorConsts.CODE_10001, "请重新登录");
		}
		JSONObject json = isDate(startDate, endDate);
		if(json != null) {
			return json;
		}
		if(StringUtils.isNull(page) || !page.matches("^\\d*$")) {
			log.warn("page is error waith page:{}", page);
			return ResultUtils.error(ErrorConsts.CODE_10002, "页码错误");
		}
		if(StringUtils.isNull(pageSize) || !pageSize.matches("^\\d*$")) {
			log.warn("pageSize is error waith pageSize:{}", pageSize);
			return ResultUtils.error(ErrorConsts.CODE_10002, "记录错误");
		}
		Integer pages = Integer.valueOf(page);
		Integer pageSizes = Integer.valueOf(pageSize);
		Map<String, Object> resultMap = new HashMap<>(3);
		try {
			resultMap = orderService.listSellerOrder(sellerId, state, startDate, endDate, pages, pageSizes);
		} catch (Exception e) {
			return ResultUtils.error(ErrorConsts.CODE_10004, "查询错误");
		}
		return ResultUtils.success(resultMap);
	}
	
	/**
	 * 卖家条件分页查询商品销售数量
	 * @Title: commodityList
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param categoryId 分类ID
	 * @param @param startDate 开始时间
	 * @param @param endDate 结束时间 
	 * @param @param page 页码
	 * @param @param pageSize 记录数
	 * @param @return
	 * @return JSONObject
	 * @throws
	 */
	@RequestMapping(value = "commodityList",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public JSONObject commodityList(HttpServletRequest request, String token, String categoryId, String startDate, String endDate, 
			String page, String pageSize) {
		String sellerId = baseService.checkUserToken(token);
		if(sellerId == null) {
			return ResultUtils.error(ErrorConsts.CODE_10001, "请重新登录");
		}
		JSONObject json = isDate(startDate, endDate);
		if(json != null) {
			return json;
		}
		if(StringUtils.isNull(page) || !page.matches("^\\d*$")) {
			log.warn("page is error waith page:{}", page);
			return ResultUtils.error(ErrorConsts.CODE_10002, "页码错误");
		}
		if(StringUtils.isNull(pageSize) || !pageSize.matches("^\\d*$")) {
			log.warn("pageSize is error waith pageSize:{}", pageSize);
			return ResultUtils.error(ErrorConsts.CODE_10002, "记录错误");
		}
		Integer pages = Integer.valueOf(page);
		Integer pageSizes = Integer.valueOf(pageSize);
		Map<String, Object> resultMap = new HashMap<>(3);
		try {
			resultMap = orderService.listSellerCommodity(sellerId, categoryId, startDate, endDate, pages, pageSizes);
		} catch (Exception e) {
			return ResultUtils.error(ErrorConsts.CODE_10004, "查询错误");
		}
		return ResultUtils.success(resultMap);
	}
	
	/**
	 * 卖家条件查询商品销售金额
	 * @Title: countMoney
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param categoryId
	 * @param @param startDate
	 * @param @param endDate
	 * @param @return
	 * @return JSONObject
	 * @throws
	 */
	@RequestMapping(value = "countmoney",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public JSONObject countMoney(HttpServletRequest request, String token, String categoryId, String startDate, String endDate) {
		String sellerId = baseService.checkUserToken(token);
		if(sellerId == null) {
			return ResultUtils.error(ErrorConsts.CODE_10001, "请重新登录");
		}
		JSONObject json = isDate(startDate, endDate);
		if(json != null) {
			return json;
		}
		Double money = 0.00;
		try {
			money = orderService.sumSellerMoney(sellerId, categoryId, startDate, endDate);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtils.error(ErrorConsts.CODE_10004, "查询错误");
		}
		return ResultUtils.success("money", money);
	}
	
	@SuppressWarnings("unused")
	public JSONObject isDate(String startDate, String endDate) {
		if(!StringUtils.isNull(startDate)) {
			if(!startDate.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
				log.warn("startDate is error waith startDate:{}", startDate);
				return ResultUtils.error(ErrorConsts.CODE_10002, "开始时间错误");
			}
			long start = convertTimeToLong(startDate);
			long time = System.currentTimeMillis();
			if(start > time) {
				return ResultUtils.error(ErrorConsts.CODE_10003, "开始时间不能大于当前时间");
			}
		}
		if(!StringUtils.isNull(endDate)) {
			if(StringUtils.isNull(startDate)) {
				return ResultUtils.error(ErrorConsts.CODE_10002, "开始时间不能为空");
			}
			if(!endDate.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
				log.warn("endDate is error waith endDate:{}", endDate);
				return ResultUtils.error(ErrorConsts.CODE_10002, "结束时间错误");
			}
		}
		if(!StringUtils.isNull(startDate) && !StringUtils.isNull(endDate)) {
			long start = convertTimeToLong(startDate);
			long end = convertTimeToLong(endDate);
			if(start > end) {
				return ResultUtils.error(ErrorConsts.CODE_10003,"开始时间大于结束时间");
			}
		}
		return null;
	}
	
	private Long convertTimeToLong(String time) {
	    Date date = null;
	    try {  
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        date = sdf.parse(time);
	        return date.getTime();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return 0L;
	    }  
	}  
	
	/**
	 * 微信统一下单
	 * @Title: payOrder
	 * @Description: TODO
	 * @param @param orderId
	 * @param @param request
	 * @param @return
	 * @return JSONObject
	 * @throws
	 */
	private JSONObject payOrder(String orderId, HttpServletRequest request) {
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
			return ResultUtils.error(ErrorConsts.CODE_10004,"支付失败");
		}
		String resultCode = resultMap.get("result_code").toString();
		if(!resultCode.equals("SUCCESS")) {
			log.error(resultMap.get("err_code_des").toString());
			return ResultUtils.error(ErrorConsts.CODE_10004,"支付失败");
		}
		String prepayId = resultMap.get("prepay_id").toString();
		Map<String, String> responseMap = new HashMap<String, String>(7);
		responseMap.put("appId", WxPayConsts.APPID);
		responseMap.put("package", "prepay_id=" + prepayId);
		responseMap.put("nonceStr", UUIDGenerator.getUUID());
		Long timeStamp = System.currentTimeMillis() / 1000;
		responseMap.put("timeStamp", timeStamp + "");
		responseMap.put("signType", "MD5");
		//把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
		String responseString = WxPayUtils.createLinkString(responseMap);
		//MD5运算生成签名
		String responseSign = WxPayUtils.sign(responseString, WxPayConsts.KEY, "utf-8").toUpperCase();
		responseMap.put("paySign", responseSign);
		return ResultUtils.success(responseMap);
	}
}
