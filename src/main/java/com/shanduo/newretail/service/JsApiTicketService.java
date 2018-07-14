package com.shanduo.newretail.service;

import org.springframework.stereotype.Service;

import com.shanduo.newretail.entity.JsApiTicket;
@Service
public interface JsApiTicketService {
	
	JsApiTicket selectJsApiTicket(String appid);

}
