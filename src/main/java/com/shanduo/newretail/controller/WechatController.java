package com.shanduo.newretail.controller;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.shanduo.newretail.consts.WxPayConsts;
import com.shanduo.newretail.entity.AccessToken;
import com.shanduo.newretail.entity.JsApiTicket;
import com.shanduo.newretail.entity.common.ErrorBean;
import com.shanduo.newretail.entity.common.ResultBean;
import com.shanduo.newretail.entity.common.SuccessBean;
import com.shanduo.newretail.service.AccessTokenService;
import com.shanduo.newretail.service.JsApiTicketService;
import com.shanduo.newretail.util.HttpRequest;
import com.shanduo.newretail.util.SHA1;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import net.sf.json.JSON;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
@Controller
@RequestMapping(value = "jwechat")
public class WechatController {
	@Autowired
	private JsApiTicketService jsApiTicketService;
	@Autowired
	private AccessTokenService accessTokenService;
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
       
       return new ErrorBean();
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
    @ResponseBody
	@RequestMapping(value = "access")
    public String wxRedirectMessage(HttpServletRequest request, String signature,String timestamp,String nonce,String echostr) {
        
        if (echostr == null) {
           
        } else {
            //接入端口验证
            List<String> list = new ArrayList<String>();
            list.add(WxPayConsts.TOKEN);
            list.add(timestamp);
            list.add(nonce);
            // 1. 将token、timestamp、nonce三个参数进行字典序排序  
            Collections.sort(list, new Comparator<String>() {  
                @Override  
                public int compare(String o1, String o2) {  
                    return o1.compareTo(o2);  
                }  
            });  
            String signStr = "";
            for (String str : list) {
                signStr += str;
            }
            if (signature.equals(SHA1.SHA2(signStr))) {
            	 return echostr;
            } 
        }
        return null; 
    }
    /**
     * 远程请求微信服务器获取用户openid
     * @throws IOException 
     * @throws ClientProtocolException 
     */
    @ResponseBody
	@RequestMapping(value = "getopenid")
    //http://localhost:8081/shanduonewretail/jwechat/getopenid?code=
    public  ResultBean getWXOpenId(String code)  {
         String requestUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code".replace("APPID", WxPayConsts.APPID).replace("SECRET", WxPayConsts.APPSECRET).replace("CODE", code);
         // 发起GET请求获取凭证
         JSONObject jsonObject = HttpRequest.httpsRequest(requestUrl, "GET", null);
         if (null != jsonObject) {
             try {
            	 return new SuccessBean(jsonObject.getString("openid")) ;
             } catch (JSONException e) {
            	 return new ErrorBean();
                 // 获取token失败
                // log.error("获取token失败 errcode:{} errmsg:{}", jsonObject.getInt("errcode"), jsonObject.getString("errmsg"));
             }
         }
         return new ErrorBean();
    }
  //创建自定义菜单
   /* public ResultBean createMenu() {
        try {
            AccessToken token = accessTokenService.selectAccessToken(WxPayConsts.APPID);
            if (token != null) {
                JSONObject jsonObject = JSON.parseObject(getMenu(token.getAccessToken()));
                if (jsonObject.containsKey("errcode")) {
                    jsonObject.clear();
                    Map<String, Object> map = new HashMap<>();
                    map.put("appid", Config.APPID);
                    map.put("host", Config.HOST);
                    Template tpl = freeMarkerConfigurer.getConfiguration().getTemplate("menu.json");
                    respJSON = JSON.parseObject(WebUtils.createMenu(token.getAccess_token(), FreeMarkerTemplateUtils.processTemplateIntoString(tpl, map)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new SuccessBean() ;
    }*/
    /*@ResponseBody
	@RequestMapping(value = "getopenids")
    public  String getMenu(String token) throws IOException {
    	 String requestUrl ="https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=" + token;
    	 JSONObject jsonObject = HttpRequest.httpsRequest(requestUrl, "GET", null);
    	 requestUrl ="https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + token;
    	 jsonObject = HttpRequest.httpsRequest(requestUrl, "GET", null);
    	 Map<String, Object> map = new HashMap<>();
    	 map.put("appid", WxPayConsts.APPID);
         map.put("host",WxPayConsts.APPSECRET);
         Template freeMarkerConfigurer = null;
		Template tpl = freeMarkerConfigurer.getConfiguration().getTemplate("menu.json");
        //JSON.parseObject(WebUtils.createMenu(token.getAccess_token(), FreeMarkerTemplateUtils.processTemplateIntoString(tpl, map)));
        try {
          //  if (response.getStatusLine().getStatusCode() == org.apache.http.HttpStatus.SC_OK) {
                return null;
           // }
        } finally {
            //closeConn(httpClient, response);
        }
      //  return null;
    }
*/
}
