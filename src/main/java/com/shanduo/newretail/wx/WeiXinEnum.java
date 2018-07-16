package com.shanduo.newretail.wx;

/**
 * 微信枚举
 * @ClassName: WeiXinEnum
 * @Description: TODO
 * @author fanshixin
 * @date 2018年7月16日 下午2:41:52
 *
 */
public class WeiXinEnum {
	// 缓存类型     
    public enum CACHE_TYPE {
        CACHE_WX_ACCESS_TOKEN,//模板消息access_token
    }    
    // 模板消息编号
    public enum WX_TEMPLATE_MSG_NUMBER{
        USER_REGISTER_SUCCESS(0,"OPENTM407796225", "注册成功"),
        ORDER_PAYED_SUCCESS(1, "4gw_InBDybb4gl5f1B9XTuT3rlnhWBMfskduFcNZBz4","订单支付成功"),
    	ORDER_SUCCESS(2,"YmkEkD4zM3hoz6lIZJNL3cv99UPQZuUQjVEuffQctLs","购买成功通知"),
    	ORDER_ERROR_SUCCESS(2,"YmkEkD4zM3hoz6lIZJNL3cv99UPQZuUQjVEuffQctLs","订单退款通知");
//        ORDER_...;
        private int code;
        private String msgNumber;
        private String label;
        WX_TEMPLATE_MSG_NUMBER(int code, String msgNumber,String label){
            this.code = code;
            this.msgNumber = msgNumber;
            this.label = label;
        }
        public int getCode(){
            return code;
        }
        public String getMsgNumber(){
            return msgNumber;
        }
        public String getLabel(){
            return label;
        }
    }
}
