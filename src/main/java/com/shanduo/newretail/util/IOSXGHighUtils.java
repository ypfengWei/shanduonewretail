package com.shanduo.newretail.util;

import java.util.List;

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
	 * @throws JSONException 
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
        JSONObject obj = new JSONObject();
        JSONObject aps = new JSONObject();
        try {
			aps.put("alert", "你有新的订单");
			aps.put("badge", 1);
			aps.put("mutable-content", 1);
			obj.put("aps", aps);
		} catch (JSONException e) {
			e.printStackTrace();
		}
        mess.setRaw(obj.toString());
        return mess;
    }
    
    /**
     * ios单个账户推送
     * @Title: pushSingleAccount
     * @Description: TODO
     * @param @param account
     * @param @return
     * @return String
     * @throws
     */
    public String pushSingleAccount(String account) {
    	MessageIOS message = getMessageIos();
        JSONObject resultJson = xinge.pushSingleAccount(0, account, message , XingeApp.IOSENV_PROD);
		return isError(resultJson);
    }
    
    /**
     * IOS多个账号推送
     * @Title: demoPushAccountListIOS
     * @Description: TODO
     * @param @param accountList
     * @param @return
     * @return JSONObject
     * @throws
     */
    public String pushAccountListIOS(List<String> accountList) {
    	MessageIOS message = getMessageIos();
        JSONObject resultJson = xinge.pushAccountList(0, accountList, message, XingeApp.IOSENV_PROD);
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
