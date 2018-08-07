package com.shanduo.newretail.util;


import org.json.JSONException;
import org.json.JSONObject;

import com.tencent.xinge.MessageIOS;
import com.tencent.xinge.TimeInterval;
import com.tencent.xinge.XingeApp;

/**
 * 腾讯信鸽高级推送工具类
 * @ClassName: XGHighUtils
 * @Description: TODO
 * @author fanshixin
 * @date 2018年5月7日 下午3:20:43
 *
 */
public class IOSXGHighUtils {

	private Long ACCESS_ID = 2200301985L;
	private String SECRET_KEY = "e69882e9d8c4343b4403a6d78f348fee";
	private XingeApp xinge;
	
	private static IOSXGHighUtils xGHighUtils;
	
	public IOSXGHighUtils() {
		xinge = new XingeApp(ACCESS_ID, SECRET_KEY);
	}
	
	public static IOSXGHighUtils getInstance(){
		xGHighUtils = new IOSXGHighUtils();
		return xGHighUtils;
	}
	
	 /**
     * 根据类型生成Message
     * @Title: getMessage
     * @Description: TODO
     * @param @param title 标题
     * @param @param content 通知内容
     * @param @return
     * @return Message
     * @throws
     */
    public MessageIOS getMessageIos() {
    	MessageIOS mess = new MessageIOS();
    	mess.setExpireTime(86400);
    	//表示一个允许推送的时间闭区间(起始小时，起始分钟，截止小时，截止分钟)
    	mess.addAcceptTime(new TimeInterval(0, 0, 23, 59));
        mess.setAlert("你有新的订单");
        mess.setBadge(1);
        mess.setSound("s_new_order.wav");
        return mess;
    }
    
    public String pushSingleAccount(String account) {
    	MessageIOS message = getMessageIos();
        JSONObject resultJson = xinge.pushSingleAccount(0, account, message , XingeApp.IOSENV_PROD);
		return isError(resultJson);
    }
    
    /**
	 * 判断返回
	 * @Title: getError
	 * @Description: TODO
	 * @param @param resultJson
	 * @param @return
	 * @param @throws JSONException
	 * @return String
	 * @throws
	 */
	public String isError(JSONObject resultJson){
		try {
			if(resultJson.getInt("ret_code") == 0) {
				return "ok";
			}
			return resultJson.getString("err_msg").toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
