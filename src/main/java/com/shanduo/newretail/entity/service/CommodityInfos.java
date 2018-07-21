package com.shanduo.newretail.entity.service;

/**
 * 商品销售实体
 * @ClassName: CommodityInfos
 * @Description: TODO
 * @author fanshixin
 * @date 2018年7月20日 下午3:37:31
 *
 */
public class CommodityInfos {
	private String commodityId;
	private String picture;
	private String commodityName;
	private Integer number;
	public String getCommodityId() {
		return commodityId;
	}
	public void setCommodityId(String commodityId) {
		this.commodityId = commodityId;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	public String getCommodityName() {
		return commodityName;
	}
	public void setCommodityName(String commodityName) {
		this.commodityName = commodityName;
	}
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	
}
