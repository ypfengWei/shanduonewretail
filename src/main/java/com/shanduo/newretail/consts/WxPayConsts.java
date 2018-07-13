package com.shanduo.newretail.consts;

import com.shanduo.newretail.consts.ConfigConsts;

public class WxPayConsts {

	/**
	 * APPID
	 */
    public static final String APPID = "wxe0870cb2d63b008d";
    
    /**
     * 商户id
     */
    public static final String MCH_ID = "1495349042";
    
    /**
     * 商户密钥
     */
    public static final String KEY = "shanduo123456TCL987654888fa00888";
    
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
     * 回调url
     */
    public static final String NOTIFY_URL= ConfigConsts.API_URL+"/jpay/pay";
    
    /**
     * 调用微信接口的返回值的false
     */
    public static final String FAIL = "FAIL";
    
    /**
     * 调用微信接口的返回值的ture
     */
    public static final String SUCCESS = "SUCCESS";
}