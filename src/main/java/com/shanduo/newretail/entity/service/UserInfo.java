package com.shanduo.newretail.entity.service;

public class UserInfo {
	private String id;
    private String name;
    private String phone;
    private Integer sellerNum;
    private Double achievement;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public Integer getSellerNum() {
		return sellerNum;
	}
	public void setSellerNum(Integer sellerNum) {
		this.sellerNum = sellerNum;
	}
	public Double getAchievement() {
		return achievement;
	}
	public void setAchievement(Double achievement) {
		this.achievement = achievement;
	}
	
}
