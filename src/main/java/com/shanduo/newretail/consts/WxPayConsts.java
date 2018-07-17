package com.shanduo.newretail.consts;

import com.shanduo.newretail.consts.ConfigConsts;

public class WxPayConsts {

	/**
	 * APPID
	 */
  // public static final String APPID = "wxe0870cb2d63b008d";
   public static final String APPID = "wxe9811ac767f05237";
    /**
     * APPSECRET
     */
  // public static final String APPSECRET = "4cb593859693a7cfd3a04bc6df454d3c";
   public static final String APPSECRET = "d7570adcda710f54cd542c8e47f258c7";
   
   public static final String TOKEN="weixin";
    
    /**
     * 商户id
     */
    public static final String MCH_ID = "1495349042";
    
    /**
     * 商户密钥
     */
    public static final String KEY = "shanduo123456TCL987654888fa00888";
    
    public static final String PKCS12_PATH = "/www/apiclient_cert.p12";
    
    /**
     * 签名方式,固定值
     */
    public static final String SIGNTYPE = "MD5";
    
    /**
     * 交易类型
     */
    public static final String TRADETYPE = "JSAPI";
    
    /**
     * 微信统一下单接口地址
     */
    public static final String PAY_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    
    /**
     * 支付回调url
     */
    public static final String NOTIFY_URL= ConfigConsts.API_URL+"/jpay/pay";
    
    /**
     * 微信申请退款接口
     */
    public static final String REFUND_URL = "https://api.mch.weixin.qq.com/secapi/pay/refund";
    
    /**
     * 退款回调url
     */
    public static final String CANCEL_URL= ConfigConsts.API_URL+"/jpay/cancel";
    
    /**
     * 调用微信接口的返回值的false
     */
    public static final String FAIL = "FAIL";
    
    /**
     * 调用微信接口的返回值的ture
     */
    public static final String SUCCESS = "SUCCESS";
}
