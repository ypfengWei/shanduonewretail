package com.shanduo.newretail.service.impl;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shanduo.newretail.consts.WxPayConsts;
import com.shanduo.newretail.entity.AccessToken;
import com.shanduo.newretail.mapper.AccessTokenMapper;
import com.shanduo.newretail.service.AccessTokenService;
import com.shanduo.newretail.util.HttpRequest;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
@Service
public class AccessTokenServiceImpl implements AccessTokenService {
	@Autowired
	private AccessTokenMapper accessTokenMapper;

	@Override
	public AccessToken selectAccessToken(String appid) {
		AccessToken accessToken = accessTokenMapper.selectAccessToken(appid);
		  if (accessToken == null) {
	            try {
					accessToken = getWXAccess_token(WxPayConsts.APPID, WxPayConsts.APPSECRET);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
	            if (accessToken != null) {
	                accessToken.setAppid(WxPayConsts.APPID);
	                accessToken.setCreateDate(System.currentTimeMillis());
	                accessTokenMapper.insertSelective(accessToken);
	            }
	        } else {
	            long newTime = System.currentTimeMillis() / 1000;
	            long oldTime = accessToken.getCreateDate() / 1000;
	            if ((int) (newTime - oldTime) >= accessToken.getExpiresIn()) {
	                AccessToken newAccessToken = new AccessToken();
					try {
						newAccessToken = getWXAccess_token(WxPayConsts.APPID, WxPayConsts.APPSECRET);
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
	                if (newAccessToken != null) {
	                    accessToken.setCreateDate(System.currentTimeMillis());
	                    accessToken.setAccessToken(newAccessToken.getAccessToken());
	                    accessToken.setExpiresIn(newAccessToken.getExpiresIn());
	                    accessToken.setAppid(WxPayConsts.APPID);
	                    accessTokenMapper.updateAccessToken(accessToken);
	                }
	            }
	        }
	        return accessToken;
		
	}
	public  AccessToken getWXAccess_token(String appid, String secret) throws ClientProtocolException, IOException {
    	AccessToken accessToken = null;
         String requestUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=SECRET".replace("APPID", appid).replace("SECRET", secret);
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
