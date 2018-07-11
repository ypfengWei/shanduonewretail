package com.shanduo.newretail.controller;

import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.shanduo.newretail.consts.ErrorConsts;
import com.shanduo.newretail.entity.common.ErrorBean;
import com.shanduo.newretail.entity.common.ResultBean;
import com.shanduo.newretail.entity.common.SuccessBean;
import com.shanduo.newretail.service.CodeService;
import com.shanduo.newretail.util.PatternUtils;
import com.shanduo.newretail.util.StringUtils;
import com.shanduo.newretail.util.SmsUtils;

@Controller
@RequestMapping(value = "code")
public class CodeController {

	private static final Logger log = LoggerFactory.getLogger(CodeController.class);
	
	@Autowired
	private CodeService codeService;
	
	@RequestMapping(value = "envoyer",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean envoyer(HttpServletRequest request,String phone,String typeId) {
		if(StringUtils.isNull(phone) || PatternUtils.patternPhone(phone)) {
			log.warn("手机号格式错误");
			return new ErrorBean(ErrorConsts.CODE_10002, "手机号错误");
		}
		if(StringUtils.isNull(typeId) || !typeId.matches("^[1]$")) {
			log.warn("类型错误");
			return new ErrorBean(ErrorConsts.CODE_10002, "类型错误");
		}
		if(codeService.checkSend(phone, typeId)) {
			return new ErrorBean(ErrorConsts.CODE_10003, "发送频率受限");
		}
		int code = new Random().nextInt(900000)+100000;
		SendSmsResponse sendSmsResponse = null;
		try {
			sendSmsResponse = SmsUtils.sendSms(phone,code+"",typeId);
		} catch (ClientException e) {
			log.error("发送短信验证码错误");
			return new ErrorBean(ErrorConsts.CODE_10003,"发送失败");
		}
		if(sendSmsResponse.getCode() == null || !"OK".equals(sendSmsResponse.getCode())) {
			log.warn(sendSmsResponse.getMessage());
			return new ErrorBean(ErrorConsts.CODE_10003,"发送失败");
		}
		try {
			codeService.saveCode(phone, code+"", typeId);
		} catch (Exception e) {
			return new ErrorBean(ErrorConsts.CODE_10004,"发送失败");
		}
		return new SuccessBean("发送成功");
	}
}
