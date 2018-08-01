package com.shanduo.newretail.consts;

import java.util.Properties;

public class WxPayConsts {

	/**
	 * APPID
	 */
   public static final String APPID;
  
    /**
     * APPSECRET
     */
   public static final String APPSECRET;

   public static final String TOKEN;
    
    /**
     * 商户id
     */
    public static final String MCH_ID;
    
    /**
     * 商户密钥
     */
    public static final String KEY;
    
    /**
     * 证书保存目录
     */
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
     * 企业付款到零钱接口
     */
    public static final String TRANSFERS_URL = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";
    
    /**
     * 企业付款到零钱查询接口
     */
    public static final String GETTRANSFERINFO_URL = "https://api.mch.weixin.qq.com/mmpaymkttransfers/gettransferinfo";
    
    /**
     * 企业付款到银行卡接口
     */
    public static final String PAY_BANK_URL = "https://api.mch.weixin.qq.com/mmpaysptrans/pay_bank";
    
    /**
     * 企业付款到银行卡接口
     */
    public static final String QUERY_BANK_URL = "https://api.mch.weixin.qq.com/mmpaysptrans/query_bank";
    
    /**
     * 调用微信接口的返回值的false
     */
    public static final String FAIL = "FAIL";
    
    /**
     * 调用微信接口的返回值的ture
     */
    public static final String SUCCESS = "SUCCESS";
    
    static Properties properties = new Properties();
    
    static {
    	try {
    		properties.load(WxPayConsts.class.getResourceAsStream("/wxpay.properties"));
		} catch (Exception e) {
			e.printStackTrace();
		}
    	APPID = properties.getProperty("wx.appid");
    	APPSECRET = properties.getProperty("wx.appsecret");
    	TOKEN = properties.getProperty("wx.token");
    	MCH_ID = properties.getProperty("wx.mch_id");
    	KEY = properties.getProperty("wx.key");
    }
}
