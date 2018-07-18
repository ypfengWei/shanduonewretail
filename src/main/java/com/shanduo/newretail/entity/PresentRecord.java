package com.shanduo.newretail.entity;

import java.math.BigDecimal;
import java.util.Date;

public class PresentRecord {
    private String id;

    private String userId;

    private Date presentTime;

    private BigDecimal amountCash;

    private String typeid;

    private String userName;

    private String openingBank;

    private String bankName;

    private String cardNumber;

    private String state;

    private Date gmtCreate;

    private Date gmtModified;

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