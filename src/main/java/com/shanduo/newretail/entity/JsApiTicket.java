package com.shanduo.newretail.entity;

public class JsApiTicket {
    private Integer jsApiTokenid;

    private String appid;

    private String ticket;

    private Integer expiresIn;

    private Long createDate;

    public Integer getJsApiTokenid() {
        return jsApiTokenid;
    }

    public void setJsApiTokenid(Integer jsApiTokenid) {
        this.jsApiTokenid = jsApiTokenid;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid == null ? null : appid.trim();
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket == null ? null : ticket.trim();
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }
}