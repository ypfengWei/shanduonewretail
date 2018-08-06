package com.shanduo.newretail.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.shanduo.newretail.consts.WxPayConsts;
import com.shanduo.newretail.entity.ToOrder;
import com.shanduo.newretail.service.OrderService;
import com.shanduo.newretail.util.AESUtil;
import com.shanduo.newretail.util.ClientCustomSSL;
import com.shanduo.newretail.util.ResultUtils;
import com.shanduo.newretail.util.UUIDGenerator;
import com.shanduo.newretail.util.WxPayUtils;


/**
 * 支付回调接口层
 * @ClassName: PayController
 * @Description: TODO
 * @author fanshixin
 * @date 2018年7月13日 下午3:23:19
 *
 */
@Controller
@RequestMapping(value = "jpay")
public class PayController {

	private static final Logger log = LoggerFactory.getLogger(PayController.class);
	
	@Autowired
	private OrderService orderService;
	
	/**
	 * 微信支付回调
	 * @Title: pay
	 * @Description: TODO
	 * @param @param request
	 * @param @return
	 * @param @throws IOException
	 * @return String
	 * @throws
	 */
	@RequestMapping(value = "pay")
	@ResponseBody
	public String pay(HttpServletRequest request) throws IOException {
		BufferedReader reader = request.getReader();
        String line = "";
        StringBuffer inputString = new StringBuffer();
        while ((line = reader.readLine()) != null) {
            inputString.append(line);
        }
        String xmlString  = inputString.toString();
        request.getReader().close();
//        log.info("微信支付回调接口返回XML数据:" + xmlString);
        Map<String, Object> resultMap = WxPayUtils.Str2Map(xmlString);
        //验证签名是否微信调用
        boolean flag = WxPayUtils.isWechatSigns(resultMap, WxPayConsts.KEY, "utf-8");
        if(flag) {
        	String returnCode = resultMap.get("return_code").toString();
    		if(!"SUCCESS".equals(returnCode)) {
    			log.warn(resultMap.get("return_msg").toString());
    			return returnXML(WxPayConsts.FAIL);
    		}
    		String resultCode = resultMap.get("result_code").toString();
    		if(!"SUCCESS".equals(resultCode)) {
    			log.warn(resultMap.get("err_code_des").toString());
    			return returnXML(WxPayConsts.FAIL);
    		}
    		String appid = resultMap.get("appid").toString();
    		if(!appid.equals(WxPayConsts.APPID)) {
    			log.warn("APPID不匹配");
    			return returnXML(WxPayConsts.FAIL);
    		}
    		String mchId = resultMap.get("mch_id").toString();
    		if(!mchId.equals(WxPayConsts.MCH_ID)) {
    			log.warn("商户号不匹配");
    			return returnXML(WxPayConsts.FAIL);
    		}
    		String orderId = resultMap.get("out_trade_no").toString();
    		ToOrder order = orderService.getOrder(orderId,"1");
			if(order == null) {
				log.warn("订单已操作或不存在");
				return returnXML(WxPayConsts.FAIL);
			}
    		String totalFee = resultMap.get("total_fee").toString();
    		//价格，单位为分
    		BigDecimal amount = order.getTotalPrice();
    		amount = amount.multiply(new BigDecimal("100"));
    		if(amount.compareTo(new BigDecimal(totalFee)) != 0) {
    			log.warn("订单金额错误:"+totalFee+","+order.getTotalPrice());
    			return returnXML(WxPayConsts.FAIL);
    		}
    		int i = orderService.updatePayOrder(orderId);
    		if(i < 1) {
				log.error("修改订单错误");
				return returnXML(WxPayConsts.FAIL);
			}
    		return returnXML(WxPayConsts.SUCCESS);
        }
    	return returnXML(WxPayConsts.FAIL);
	}
	
	/**
	 * 微信退款回调
	 * @Title: cancel
	 * @Description: TODO
	 * @param @param request
	 * @param @return
	 * @param @throws Exception
	 * @return String
	 * @throws
	 */
	@RequestMapping(value = "cancel")
	@ResponseBody
	public String cancel(HttpServletRequest request) throws Exception {
		BufferedReader reader = request.getReader();
        String line = "";
        StringBuffer inputString = new StringBuffer();
        while ((line = reader.readLine()) != null) {
            inputString.append(line);
        }
        String xmlString  = inputString.toString();
        request.getReader().close();
//        log.info("微信退款回调接口返回XML数据:" + xmlString);
        Map<String, Object> resultMap = WxPayUtils.Str2Map(xmlString);
    	String returnCode = resultMap.get("return_code").toString();
		if(!"SUCCESS".equals(returnCode)) {
			log.warn(resultMap.get("return_msg").toString());
			return returnXML(WxPayConsts.FAIL);
		}
		String appid = resultMap.get("appid").toString();
		if(!appid.equals(WxPayConsts.APPID)) {
			log.warn("APPID不匹配");
			return returnXML(WxPayConsts.FAIL);
		}
		String mchId = resultMap.get("mch_id").toString();
		if(!mchId.equals(WxPayConsts.MCH_ID)) {
			log.warn("商户号不匹配");
			return returnXML(WxPayConsts.FAIL);
		}
		//加密信息
		String reqInfo = resultMap.get("req_info").toString();
		String reqInfos = AESUtil.decryptData(reqInfo);
		resultMap = WxPayUtils.Str2Map(reqInfos);
		String refundStatus = resultMap.get("refund_status").toString();
		if(!refundStatus.equals("SUCCESS")) {
			log.warn("退款失败");
			return returnXML(WxPayConsts.FAIL);
		}
		String orderId = resultMap.get("out_trade_no").toString();
		ToOrder order = orderService.getOrder(orderId,"5");
		if(order == null) {
			log.warn("订单已操作或不存在");
			return returnXML(WxPayConsts.FAIL);
		}
		String totalFee = resultMap.get("total_fee").toString();
		//价格，单位为分
		BigDecimal amount = order.getTotalPrice();
		amount = amount.multiply(new BigDecimal("100"));
		if(amount.compareTo(new BigDecimal(totalFee)) != 0) {
			log.warn("订单金额错误:"+totalFee+","+order.getTotalPrice());
			return returnXML(WxPayConsts.FAIL);
		}
		try {
			orderService.updateCancelOrder(orderId);
		} catch (Exception e) {
			log.error("订单退款失败 waith orderId:{}", orderId);
			return returnXML(WxPayConsts.FAIL);
		}
		return returnXML(WxPayConsts.SUCCESS);
	}
	
	/**
	 * 微信回调返回XML
	 * @param returnCode
	 * @return
	 */
	private String returnXML(String returnCode) {
        return "<xml><return_code><![CDATA["
                + returnCode
                + "]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
	}
	
	/**
	 * 获取RSA公钥API获取RSA公钥(error)
	 * @Title: getPublicKey
	 * @Description: TODO
	 * @param @param request
	 * @param @return
	 * @param @throws Exception
	 * @return JSONObject
	 * @throws
	 */
	@RequestMapping(value = "getpublickey")
	@ResponseBody
	public JSONObject getPublicKey(HttpServletRequest request) throws Exception {
		Map<String, String> paramsMap = new HashMap<>(4);
		paramsMap.put("mch_id", WxPayConsts.MCH_ID);
		paramsMap.put("nonce_str", UUIDGenerator.getUUID());
		paramsMap.put("sign_type", WxPayConsts.SIGNTYPE);
		//把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
		String paramsString = WxPayUtils.createLinkString(paramsMap);
		//MD5运算生成签名
		String sign = WxPayUtils.sign(paramsString, WxPayConsts.KEY, "utf-8").toUpperCase();
		//签名
		paramsMap.put("sign", sign);
		String paramsXml = WxPayUtils.map2Xmlstring(paramsMap);
		String result = ClientCustomSSL.doRefund("https://fraud.mch.weixin.qq.com/risk/getpublickey", paramsXml);
		log.warn(result);
		Map<String, Object> resultMap = WxPayUtils.Str2Map(result);
		return ResultUtils.success(resultMap);
	}
}
