package com.shanduo.newretail.entity;

import java.math.BigDecimal;
import java.util.Date;

public class UserSeller {
    private String id;

    private String sellerName;

    private String sellerPicture;

    private String notice;

    private String phone;

    private Integer sellerType;
    
    private Integer distribution;

    private Date startDate;

    private Date endDate;

    private String businessSign;

    private BigDecimal money;

    private String parentId;

    private BigDecimal lat;

    private BigDecimal lon;
    
    private String address;

    private Date gmtCreate;

    private Date gmtModified;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName == null ? null : sellerName.trim();
    }

    public String getSellerPicture() {
        return sellerPicture;
    }

    public void setSellerPicture(String sellerPicture) {
        this.sellerPicture = sellerPicture == null ? null : sellerPicture.trim();
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice == null ? null : notice.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    

    public Integer getSellerType() {
		return sellerType;
	}

	public void setSellerType(Integer sellerType) {
		this.sellerType = sellerType;
	}

	public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getBusinessSign() {
        return businessSign;
    }

    public void setBusinessSign(String businessSign) {
        this.businessSign = businessSign == null ? null : businessSign.trim();
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId == null ? null : parentId.trim();
    }

    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public BigDecimal getLon() {
        return lon;
    }

    public void setLon(BigDecimal lon) {
        this.lon = lon;
    }

    public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getDistribution() {
		return distribution;
	}

	public void setDistribution(Integer distribution) {
		this.distribution = distribution;
	}

	public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }
}