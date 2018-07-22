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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Template;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
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
         net.sf.json.JSONObject jsonObject = HttpRequest.httpsRequest(requestUrl, "GET", null);
         if (null != jsonObject) {
             try {
            	 accessToken = new AccessToken();
            	 accessToken.setAccessToken(jsonObject.getString("access_token"));
            	 accessToken.setExpiresIn(jsonObject.getInt("expires_in"));
             } catch (net.sf.json.JSONException e) {
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
        net.sf.json.JSONObject jsonObject = HttpRequest.httpsRequest(requestUrl, "GET", null);
         if (null != jsonObject) {
             try {
            	 return new SuccessBean(jsonObject.getString("openid")) ;
             } catch (net.sf.json.JSONException e) {
            	 return new ErrorBean();
                 // 获取token失败
                // log.error("获取token失败 errcode:{} errmsg:{}", jsonObject.getInt("errcode"), jsonObject.getString("errmsg"));
             }
         }
         return new ErrorBean();
    }
  //创建自定义菜单
  @ResponseBody
  @RequestMapping(value = "createMenu")
    public ResultBean createMenu() {
        try {
            AccessToken token = accessTokenService.selectAccessToken(WxPayConsts.APPID);
            if (token != null) {
                JSONObject jsonObject = JSON.parseObject(getMenu(token.getAccessToken()));
                if (jsonObject.containsKey("errcode")) {
                    jsonObject.clear();
                    JSONObject root=new JSONObject();
                    JSONArray jsonArray=new JSONArray();
                    JSONObject child=new JSONObject();
                    child.put("type","view");
                    child.put("name","闪多新零售");
                    child.put("url","https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxe9811ac767f05237&redirect_uri=http://lixinrong.vicp.io/shanduonewretail/index.html&response_type=code&scope=snsapi_userinfo#wechat_redirect");
                    JSONObject child2=new JSONObject();
                    child2.put("type","view");
                    child2.put("name","后台管理");
                    child2.put("url","http://lixinrong.vicp.io/shanduonewretail/login.html");
                    jsonArray.add(child);
                    jsonArray.add(child2);
                    root.put("button",jsonArray);
                    return new SuccessBean(createMenu(token.getAccessToken(), root.toJSONString())) ;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ErrorBean(10023,"");
    }
    /**
     * 查询自定义菜单
     */
    public static String getMenu(String token) {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet method = new HttpGet("https://api.weixin.qq.com/cgi-bin/menu/get?access_token=" + token);
        try {
            response = httpClient.execute(method);
            if (response.getStatusLine().getStatusCode() == org.apache.http.HttpStatus.SC_OK) {
                return EntityUtils.toString(response.getEntity());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    /**
     * 创建自定义菜单
     */
    public static String createMenu(String token, String menu) {
        if (menu != null) {
            CloseableHttpResponse response = null;
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost method = new HttpPost("https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + token);
            try {
                StringEntity stringEntity = new StringEntity(menu, "UTF-8");
                method.setEntity(stringEntity);
                response = httpClient.execute(method);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    return EntityUtils.toString(response.getEntity());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (response != null) {
                        response.close();
                    }
                    if (httpClient != null) {
                        httpClient.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
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
