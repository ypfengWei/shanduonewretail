package com.shanduo.newretail.mapper;

import com.shanduo.newretail.entity.JsApiTicket;

public interface JsApiTicketMapper {
    int deleteByPrimaryKey(Integer jsApiTokenid);

    int insert(JsApiTicket record);

    int insertSelective(JsApiTicket record);

    JsApiTicket selectByPrimaryKey(Integer jsApiTokenid);

    int updateByPrimaryKeySelective(JsApiTicket record);

    int updateByPrimaryKey(JsApiTicket record);
    
    JsApiTicket selectJsApiTicket(String appid);
    
    int updateJsApiTicket(JsApiTicket jsApiTicket);
}