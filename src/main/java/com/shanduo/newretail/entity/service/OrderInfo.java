package com.shanduo.newretail.entity.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.shanduo.newretail.entity.ToOrderDetails;

public class OrderInfo{
	private String id;
    private BigDecimal totalPrice;
    private String userName;
    private String userPhone;
    private String userAddress;
    private String remarks;
    private Date gmtCreate;
    private List<ToOrderDetails> details;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserPhone() {
		return userPhone;
	}
	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}
	public String getUserAddress() {
		return userAddress;
	}
	public void setUserAddress(String userAddress) {
		this.userAddress = userAddress;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public Date getGmtCreate() {
		return gmtCreate;
	}
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	public List<ToOrderDetails> getDetails() {
		return details;
	}
	public void setDetails(List<ToOrderDetails> details) {
		this.details = details;
	}
	
	@Override
	public String toString() {
		return "OrderInfo [id=" + id + ", totalPrice=" + totalPrice + ", userName=" + userName + ", userPhone="
				+ userPhone + ", userAddress=" + userAddress + ", remarks=" + remarks + ", gmtCreate=" + gmtCreate
				+ ", details=" + details + "]";
	}
    
}
