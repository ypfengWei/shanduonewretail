package com.shanduo.newretail.entity;

import java.math.BigDecimal;
import java.util.Date;

public class PresentRecord {
    private String id;

    private String userId;//用户Id

    private Date presentTime;//提现时间

    private BigDecimal amountCash;//提现金额

    private String typeid;//1:微信提现2：银行卡提现

    private String userName;//提现人姓名

    private String openingBank;//

    private String bankName;//银行ID

    private String cardNumber;//银行卡号

    private String state;//1:申请提现2:同意提现;3.拒绝提现;

    private Date gmtCreate;//创建时间

    private Date gmtModified;//更新时间

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public Date getPresentTime() {
        return presentTime;
    }

    public void setPresentTime(Date presentTime) {
        this.presentTime = presentTime;
    }

    public BigDecimal getAmountCash() {
        return amountCash;
    }

    public void setAmountCash(BigDecimal amountCash) {
        this.amountCash = amountCash;
    }

    public String getTypeid() {
        return typeid;
    }

    public void setTypeid(String typeid) {
        this.typeid = typeid == null ? null : typeid.trim();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getOpeningBank() {
        return openingBank;
    }

    public void setOpeningBank(String openingBank) {
        this.openingBank = openingBank == null ? null : openingBank.trim();
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName == null ? null : bankName.trim();
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber == null ? null : cardNumber.trim();
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state == null ? null : state.trim();
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