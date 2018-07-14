package com.shanduo.newretail.service;

import com.shanduo.newretail.entity.AccessToken;

public interface AccessTokenService {
	
	AccessToken selectAccessToken(String appid);

}
