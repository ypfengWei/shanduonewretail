package com.shanduo.newretail.wx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

public class WX_UserUtil {

	private static Logger log = LoggerFactory.getLogger(WX_UserUtil.class);
	
    /**
     * 根据微信openId 获取用户是否订阅
     * @param openId 微信openId
     * @return 是否订阅该公众号标识
     */
    public static Integer subscribeState(String token, String openId){
        String tmpurl = "https://api.weixin.qq.com/cgi-bin/user/info?access_token="+token+"&openid="+openId;
        JSONObject result = WX_HttpsUtil.httpsRequest(tmpurl, "GET",null);
        JSONObject resultJson = new JSONObject(result);
        String errmsg = (String) resultJson.get("errmsg");
        if(errmsg==null){
            //用户是否订阅该公众号标识（0代表此用户没有关注该公众号 1表示关注了该公众号）。
            Integer subscribe = (Integer) resultJson.get("subscribe");
            return subscribe;
        }
        log.error("获取用户是否订阅 errcode:{} errmsg:{}", resultJson.getInteger("errcode"), resultJson.getString("errmsg"));
        return -1;
    }
}
