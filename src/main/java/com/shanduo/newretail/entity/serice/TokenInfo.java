package com.shanduo.newretail.entity.serice;

import com.shanduo.newretail.entity.ToUser;

/**
 * 登录返回信息
 * @ClassName: TokenInfo
 * @Description: TODO
 * @author fanshixin
 * @date 2018年7月12日 下午3:01:49
 *
 */
public class TokenInfo {
	private String token;
	private String name;
    private String phone;
    private String jurisdiction;
    
    public TokenInfo() {
    	
    }
    
    public TokenInfo(String token,ToUser user) {
    	this.token = token;
    	this.name = user.getName();
    	this.phone = user.getMobilePhone();
    	this.jurisdiction = user.getJurisdiction();
    }
    
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getJurisdiction() {
		return jurisdiction;
	}
	public void setJurisdiction(String jurisdiction) {
		this.jurisdiction = jurisdiction;
	}
    
}
