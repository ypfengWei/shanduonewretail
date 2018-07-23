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
        ORDER_PAYED(1, "OPENTM400231951","支付成功通知"),
    	ORDER_SUCCESS(2,"OPENTM411855700","派单成功提醒"),
    	ORDER_ERROR_SUCCESS(3,"OPENTM415964302","退款通知");
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
