package com.shanduo.newretail.controller;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shanduo.newretail.consts.WxPayConsts;
import com.shanduo.newretail.entity.AccessToken;
import com.shanduo.newretail.entity.JsApiTicket;
import com.shanduo.newretail.entity.common.ResultBean;
import com.shanduo.newretail.entity.common.SuccessBean;
import com.shanduo.newretail.service.JsApiTicketService;
import com.shanduo.newretail.util.HttpRequest;
import com.shanduo.newretail.util.SHA1;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
@Controller
@RequestMapping(value = "jwechat")
public class WechatController {
	@Autowired
	private JsApiTicketService jsApiTicketService;
	/*配置微信网页jsapi调用环境*/
	@RequestMapping(value = "selectinitjssdk",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
    public ResultBean initJS_SDK(String pageUrl) {
       JsApiTicket jsapi_ticket = jsApiTicketService.selectJsApiTicket(WxPayConsts.APPID);
       if (jsapi_ticket != null) {
            SecureRandom random = new SecureRandom();
            StringBuilder builder = new StringBuilder();
            String noncestr = new BigInteger(32, random).toString(8);
            long timestamp = System.currentTimeMillis();
            builder.append("jsapi_ticket=").append(jsapi_ticket.getTicket())
                    .append("&noncestr=").append(noncestr)
                    .append("&timestamp=").append(timestamp)
                    .append("&url=").append(pageUrl);
            String signature = SHA1.SHA2(builder.toString());
            Map<String,Object> respJSON = new HashMap<String,Object>();
            respJSON.put("noncestr", noncestr);
            respJSON.put("timestamp", timestamp);
            respJSON.put("appid", WxPayConsts.APPID);
            respJSON.put("signature", signature);
            return new SuccessBean(respJSON);
        }
       
       return null;
    }
    
    /**
     * 远程请求微信服务器获取公众号全局Access_token
     * @throws IOException 
     * @throws ClientProtocolException 
     */
    @ResponseBody
	@RequestMapping(value = "accesstoken")
    //{"errcode":0,"errmsg":"ok","ticket":"kgt8ON7yVITDhtdwci0qeTYZ2XsQzZ0VtpwmXy2xKkTJwduPg4cKFEJdEfLelWHu4Y2HZFLvzdVIA-eJ_Ax98g","expires_in":7200}
    //http://localhost:8081/shanduonewretail/jwechat/accesstoken?appid=wxe9811ac767f05237&secret=d7570adcda710f54cd542c8e47f258c7
    public  AccessToken getWXAccess_token(String access_token) throws ClientProtocolException, IOException {
    	AccessToken accessToken = null;
         String requestUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=TOKEN&type=jsapi".replace("TOKEN", access_token);
         // 发起GET请求获取凭证
         JSONObject jsonObject = HttpRequest.httpsRequest(requestUrl, "GET", null);
         if (null != jsonObject) {
             try {
            	 accessToken = new AccessToken();
            	 accessToken.setAccessToken(jsonObject.getString("access_token"));
            	 accessToken.setExpiresIn(jsonObject.getInt("expires_in"));
             } catch (JSONException e) {
            	 accessToken = null;
                 // 获取token失败
                // log.error("获取token失败 errcode:{} errmsg:{}", jsonObject.getInt("errcode"), jsonObject.getString("errmsg"));
             }
         }
	     return accessToken;
    }
}
