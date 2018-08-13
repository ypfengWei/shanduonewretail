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
import com.shanduo.newretail.consts.DefaultConsts;
import com.shanduo.newretail.consts.ErrorConsts;
import com.shanduo.newretail.service.CodeService;
import com.shanduo.newretail.service.UserService;
import com.shanduo.newretail.util.PatternUtils;
import com.shanduo.newretail.util.ResultUtils;
import com.shanduo.newretail.util.SmsUtils;
import com.shanduo.newretail.util.StringUtils;

import com.alibaba.fastjson.JSONObject;

/**
 * 验证码接口层
 * @ClassName: CodeController
 * @Description: TODO
 * @author fanshixin
 * @date 2018年7月12日 下午2:22:53
 *
 */
@Controller
@RequestMapping(value = "code")
public class CodeController {
	

	private static final Logger log = LoggerFactory.getLogger(CodeController.class);
	
	@Autowired
	private CodeService codeService;
	@Autowired
	private UserService userService;
	
	/**
	 * 发送短信验证码
	 * @Title: envoyer
	 * @Description: TODO
	 * @param @param request
	 * @param @param phone 手机号
	 * @param @param typeId 类型:1.注册;2.换手机号;3.修改密码;
	 * @param @return
	 * @return JSONObject
	 * @throws
	 */
	@RequestMapping(value = "envoyer",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public JSONObject envoyer(HttpServletRequest request,String phone,String typeId) {
		if(StringUtils.isNull(phone) || PatternUtils.patternPhone(phone)) {
			log.warn("phone is error waith phone:{}", phone);
			return ResultUtils.error(ErrorConsts.CODE_10002, "手机号错误");
		}
		if(StringUtils.isNull(typeId) || !typeId.matches("^[123]$")) {
			log.warn("typeId is error waith typeId:{}", typeId);
			return ResultUtils.error(ErrorConsts.CODE_10002, "类型错误");
		}
		if(typeId.equals(DefaultConsts.NUMBER_1) && userService.chackPhone(phone)) {
			return ResultUtils.error(ErrorConsts.CODE_10003, "手机号已存在");
		}
		if(typeId.equals(DefaultConsts.NUMBER_2) && userService.chackPhone(phone)) {
			return ResultUtils.error(ErrorConsts.CODE_10003, "手机号已存在");
		}
		if(codeService.checkSend(phone, typeId)) {
			return ResultUtils.error(ErrorConsts.CODE_10003, "发送频率受限");
		}
		int code = new Random().nextInt(900000)+100000;
		SendSmsResponse sendSmsResponse = null;
		try {
			sendSmsResponse = SmsUtils.sendSms(phone, code+"", typeId);
		} catch (ClientException e) {
			log.error("发送短信验证码错误");
			return ResultUtils.error(ErrorConsts.CODE_10003, "发送失败");
		}
		if(sendSmsResponse.getCode() == null || !"OK".equals(sendSmsResponse.getCode())) {
			log.warn(sendSmsResponse.getMessage());
			return ResultUtils.error(ErrorConsts.CODE_10003, "发送失败");
		}
		try {
			codeService.saveCode(phone, code+"", typeId);
		} catch (Exception e) {
			return ResultUtils.error(ErrorConsts.CODE_10004, "发送失败");
		}
		return ResultUtils.success("发送成功");
	}
}
