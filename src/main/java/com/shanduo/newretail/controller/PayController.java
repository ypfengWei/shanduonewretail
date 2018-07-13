package com.shanduo.newretail.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shanduo.newretail.consts.WxPayConsts;
import com.shanduo.newretail.entity.ToOrder;
import com.shanduo.newretail.service.OrderService;
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
	 * @Title: appWxPay
	 * @Description: TODO
	 * @param @param request
	 * @param @return
	 * @param @throws IOException
	 * @return String
	 * @throws
	 */
	@RequestMapping(value = "pay")
	@ResponseBody
	public String appWxPay(HttpServletRequest request) throws IOException {
		BufferedReader reader = request.getReader();
        String line = "";
        StringBuffer inputString = new StringBuffer();
        while ((line = reader.readLine()) != null) {
            inputString.append(line);
        }
        String xmlString  = inputString.toString();
        request.getReader().close();
        log.info("微信支付回调接口返回XML数据:" + xmlString);
        Map<String, Object> resultMap = WxPayUtils.Str2Map(xmlString);
        //验证签名是否微信调用
        boolean flag = WxPayUtils.isWechatSigns(resultMap, WxPayConsts.KEY, "utf-8");
        if(flag) {
        	String returnCode = resultMap.get("return_code").toString();
    		if(!"SUCCESS".equals(returnCode)) {
    			log.error(resultMap.get("return_msg").toString());
    			return returnXML(WxPayConsts.FAIL);
    		}
    		String resultCode = resultMap.get("result_code").toString();
    		if(!"SUCCESS".equals(resultCode)) {
    			log.error(resultMap.get("err_code_des").toString());
    			return returnXML(WxPayConsts.FAIL);
    		}
    		String appid = resultMap.get("appid").toString();
    		if(!appid.equals(WxPayConsts.APPID)) {
    			log.error("APPID不匹配");
    			return returnXML(WxPayConsts.FAIL);
    		}
    		String mchId = resultMap.get("mch_id").toString();
    		if(!mchId.equals(WxPayConsts.MCH_ID)) {
    			log.error("商户号不匹配");
    			return returnXML(WxPayConsts.FAIL);
    		}
    		String orderId = resultMap.get("out_trade_no").toString();
    		ToOrder order = orderService.getUnpaidOrder(orderId);
			if(order == null) {
				log.error("订单已操作或不存在");
				return returnXML(WxPayConsts.FAIL);
			}
    		String totalFee = resultMap.get("total_fee").toString();
    		//价格，单位为分
    		BigDecimal amount = order.getTotalPrice();
    		amount = amount.multiply(new BigDecimal("100"));
    		if(amount.compareTo(new BigDecimal(totalFee)) != 0) {
    			log.error("订单金额错误:"+totalFee+","+order.getTotalPrice());
    			return returnXML(WxPayConsts.FAIL);
    		}
    		try {
//				orderService.updateOrders(orderId,"3");
			} catch (Exception e) {
				log.error("修改订单错误");
				return returnXML(WxPayConsts.FAIL);
			}
    		return returnXML(WxPayConsts.SUCCESS);
        }
    	return returnXML(WxPayConsts.FAIL);
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
	
}
