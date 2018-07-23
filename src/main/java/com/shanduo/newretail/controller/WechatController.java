package com.shanduo.newretail.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shanduo.newretail.consts.ErrorConsts;
import com.shanduo.newretail.consts.WxPayConsts;
import com.shanduo.newretail.entity.AccessToken;
import com.shanduo.newretail.entity.JsApiTicket;
import com.shanduo.newretail.entity.common.ErrorBean;
import com.shanduo.newretail.entity.common.ResultBean;
import com.shanduo.newretail.entity.common.SuccessBean;
import com.shanduo.newretail.service.AccessTokenService;
import com.shanduo.newretail.service.BaseService;
import com.shanduo.newretail.service.JsApiTicketService;
import com.shanduo.newretail.util.HttpRequest;
import com.shanduo.newretail.util.SHA1;
import com.shanduo.newretail.util.StringUtils;

@Controller
@RequestMapping(value = "jwechat")
public class WechatController {
	private static final Logger Log = LoggerFactory.getLogger(WechatController.class);
	@Autowired
	private JsApiTicketService jsApiTicketService;
	@Autowired
	private AccessTokenService accessTokenService;
	@Autowired
    private BaseService baseService;
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
            	delMenu(token.getAccessToken());
                JSONObject jsonObject = JSON.parseObject(getMenu(token.getAccessToken()));
                if (jsonObject.containsKey("errcode")) {
                    jsonObject.clear();
                    JSONObject root=new JSONObject();
                    JSONArray jsonArray=new JSONArray();
                    JSONObject child=new JSONObject();
                    child.put("type","view");
                    child.put("name","闪多新零售");
                    child.put("url","https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxe0870cb2d63b008d&redirect_uri=https://yapinkeji.com/shanduonewretail/index.html&response_type=code&scope=snsapi_userinfo#wechat_redirect");
                    JSONObject child2=new JSONObject();
                    child2.put("type","view");
                    child2.put("name","后台管理");
                    child2.put("url","https://yapinkeji.com/shanduonewretail/login.html");
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
     * 删除自定义菜单
     */
    public static String delMenu(String token) {
        HttpGet method = new HttpGet("https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=" + token);
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
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
    /*获取业务员的二维码*/
    @ResponseBody
    @RequestMapping(value = "salesmancode")
    public ResultBean salesmanCode(String token) {
    	if (StringUtils.isNull(token)) {
            Log.warn("token为空");
         //   return new ErrorBean(ErrorConsts.CODE_10002, "token为空");
        }
        String id = baseService.checkUserToken(token);
        if (null == id) {
            Log.warn("token失效");
        //    return new ErrorBean(ErrorConsts.CODE_10001, "token失效");
        }
        if (id != null) {
        	AccessToken accessToken = accessTokenService.selectAccessToken(WxPayConsts.APPID);
        	//获取数据的地址（微信提供）
            String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token="+accessToken.getAccessToken();
   
            //发送给微信服务器的数据
            String jsonStr = "{\"expire_seconds\": 2592000,\"action_name\": \"QR_SCENE\", \"action_info\": {\"scene\": {\"id\": "+id+"}}}";
   
            //将得到的字符串转化成json对象
         //   String response = sendPost(jsonStr, url);
            return new SuccessBean(sendPost(jsonStr, url));
            //JSONObject jsonObject = create_qrcode(accessToken.getAccessToken(), "{\"expire_seconds\": 2592000,\"action_name\": \"QR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": \"yaping_NUMBER\"}}}".replace("NUMBER", id));
         //   if (jsonObject != null) {
           //     HttpEntity entity = download_qrcode(jsonObject.getString("ticket"));
            //    if (entity != null) {
            //        try {
             //       	 String response = RequestMethod.sendPost(jsonStr, url);
              //           return response.toString();
                      //  setFileName("wx_NUMBER.jpg".replace("NUMBER", id));
                       // setInputStream(entity.getContent());
                      //  return null;
               //     } catch (IOException e) {
               //         e.printStackTrace();
              //      }
            //    }
          //  }
        }
        return null;
    }
    public static BufferedReader sendPost(String param, String url) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                      "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            // out = new PrintWriter(conn.getOutputStream());
            out = new PrintWriter(new OutputStreamWriter(
                      conn.getOutputStream(), "utf-8"));
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                      conn.getInputStream(), "UTF-8"));
            return in;
           /* String line;
            while ((line = in.readLine()) != null) {
                 result += line;
            }*/
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                 if (out != null) {
                      out.close();
                 }
                 if (in != null) {
                      in.close();
                 }
            } catch (IOException ex) {
                 ex.printStackTrace();
            }
        }
        return null;
   }
   /* private void setInputStream(InputStream content) {
		// TODO Auto-generated method stub
		
	}

	private void setFileName(String replace) {
		// TODO Auto-generated method stub
		
	}*/

	public static JSONObject create_qrcode(String token, String content) {
        JSONObject jsonObject = null;
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        HttpPost method = new HttpPost("https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + token);
        try {
            StringEntity entity = new StringEntity(content, "UTF-8");
            method.addHeader("Content-type", "application/json; charset=utf-8");
            method.setHeader("Accept", "application/json");
            method.setEntity(entity);
            response = client.execute(method);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                jsonObject = JSON.parseObject(EntityUtils.toString(response.getEntity()));
                if (jsonObject.containsKey("ticket")) {
                    String ticket = jsonObject.getString("ticket");
                    jsonObject.replace("ticket", URLEncoder.encode(ticket, "utf-8"));
                    return jsonObject;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        	try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    public static HttpEntity download_qrcode(String ticket) {
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        HttpGet method = new HttpGet("https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + ticket);
        try {
            response = client.execute(method);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return response.getEntity();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
