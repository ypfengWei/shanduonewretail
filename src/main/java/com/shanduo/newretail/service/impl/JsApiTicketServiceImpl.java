package com.shanduo.newretail.service.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shanduo.newretail.consts.WxPayConsts;
import com.shanduo.newretail.entity.AccessToken;
import com.shanduo.newretail.entity.JsApiTicket;
import com.shanduo.newretail.mapper.JsApiTicketMapper;
import com.shanduo.newretail.service.AccessTokenService;
import com.shanduo.newretail.service.JsApiTicketService;
import com.shanduo.newretail.util.HttpRequest;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
@Service
public class JsApiTicketServiceImpl implements JsApiTicketService {
	@Autowired
	private JsApiTicketMapper jsApiTicketMapper;
	@Autowired
	private AccessTokenService accessTokenService;

	@Override
	public JsApiTicket selectJsApiTicket(String appid) {
		JsApiTicket jsApiTicket = jsApiTicketMapper.selectJsApiTicket(appid);
		 AccessToken token = new AccessToken();
	        if (jsApiTicket == null) {
	            token = accessTokenService.selectAccessToken(appid);
	            if (token != null) {
	                jsApiTicket = getWXJsApiTicket(token.getAccessToken());
	                if (jsApiTicket != null) {
	                    jsApiTicket.setAppid(WxPayConsts.APPID);
	                    jsApiTicket.setCreateDate(System.currentTimeMillis());
	                    jsApiTicketMapper.insertSelective(jsApiTicket);
	                }
	            }
	        } else {
	            long newTime = System.currentTimeMillis() / 1000;
	            long oldTime = jsApiTicket.getCreateDate() / 1000;
	            if ((int) (newTime - oldTime) >= jsApiTicket.getExpiresIn()) {
	            	token = accessTokenService.selectAccessToken(appid);
	                if (token != null) {
	                    JsApiTicket newJsApiTicket = getWXJsApiTicket(token.getAccessToken());
	                    if (newJsApiTicket != null) {
	                        jsApiTicket.setCreateDate(System.currentTimeMillis());
	                        jsApiTicket.setExpiresIn(newJsApiTicket.getExpiresIn());
	                        jsApiTicket.setTicket(newJsApiTicket.getTicket());
	                        jsApiTicket.setAppid(appid);
	                        jsApiTicketMapper.updateJsApiTicket(jsApiTicket);
	                    }
	                }
	            }
	        }
	        return jsApiTicket;
	}
	
	public  JsApiTicket getWXJsApiTicket(String access_token) {
		JsApiTicket jsApiTicket = new JsApiTicket();
         String requestUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=TOKEN&type=jsapi".replace("TOKEN", access_token);
         // 发起GET请求获取凭证
         JSONObject jsonObject = HttpRequest.httpsRequest(requestUrl, "GET", null);
         if (null != jsonObject) {
             try {
            	 jsApiTicket.setTicket(jsonObject.getString("ticket"));
            	 jsApiTicket.setExpiresIn(jsonObject.getInt("expires_in"));
             } catch (JSONException e) {
            	 jsApiTicket = null;
                 // 获取token失败
                // log.error("获取token失败 errcode:{} errmsg:{}", jsonObject.getInt("errcode"), jsonObject.getString("errmsg"));
             }
         }
	     return jsApiTicket;
    }

}
