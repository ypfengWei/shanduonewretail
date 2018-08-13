package com.shanduo.newretail.util;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.tencent.xinge.Message;
import com.tencent.xinge.Style;
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
public class XGHighUtils {

	private long ACCESS_ID = 2100302369;
	private String SECRET_KEY = "228b4566829bb7cfab3ffd73251d26f4";
	private XingeApp xinge;
	
	private static XGHighUtils xGHighUtils;
	
	public XGHighUtils() {
		xinge = new XingeApp(ACCESS_ID, SECRET_KEY);
	}
	
	public static XGHighUtils getInstance(){
		xGHighUtils = new XGHighUtils();
		return xGHighUtils;
	}
	
	 /**
     * 根据类型生成Message
     * @Title: getMessage
     * @Description: TODO
     * @param @return
     * @return Message
     * @throws
     */
    public Message getMessage() {
    	Message message = new Message();
    	message.setTitle("闪多新零售");
        message.setContent("你有新的订单");
        //表示一个允许推送的时间闭区间(起始小时，起始分钟，截止小时，截止分钟)
        message.addAcceptTime(new TimeInterval(0, 0, 23, 59));
        //消息类型必填
        //TYPE_NOTIFICATION:通知;TYPE_MESSAGE:透传消息。
        //注意：TYPE_MESSAGE类型消息默认在终端是不展示的,不会弹出通知
        message.setType(Message.TYPE_NOTIFICATION);
        //定义通知消息如何展现
        //通知样式,响铃,不震动,通知栏可清除,展示本条通知且不影响其他通知
        Style style = new Style(4,1,0,1,-1);
        message.setStyle(style);
        return message;
    }
    
	/**
	 * Android单个账号下发
	 * @Title: pushSingleAccount
	 * @Description: TODO
	 * @param @param title
	 * @param @param content
	 * @param @param account
	 * @param @param typeId 1:透传消息,2.打开app
	 * @param @return
	 * @param @throws JSONException
	 * @return String
	 * @throws
	 */
    public String pushSingleAccount(String account) {
    	Message message = getMessage();
        JSONObject resultJson = xinge.pushSingleAccount(0, account, message);
		return isError(resultJson);
    }
    
    /**
     * Android多个账号下发
     * @Title: pushAccountList
     * @Description: TODO
     * @param @param title 标题内容
     * @param @param content 通知内容
     * @param @param account 账号
     * @param @param typeId 类型
     * @param @return
     * @param @throws JSONException
     * @return String
     * @throws
     */
    public String pushAccountList(List<String> accountList) {
    	Message message = getMessage();
        JSONObject resultJson = xinge.pushAccountList(0, accountList, message);
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
