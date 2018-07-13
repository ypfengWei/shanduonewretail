package com.shanduo.newretail.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shanduo.newretail.consts.DefaultConsts;
import com.shanduo.newretail.consts.ErrorConsts;
import com.shanduo.newretail.entity.common.ErrorBean;
import com.shanduo.newretail.entity.common.ResultBean;
import com.shanduo.newretail.entity.common.SuccessBean;
import com.shanduo.newretail.entity.serice.TokenInfo;
import com.shanduo.newretail.service.BaseService;
import com.shanduo.newretail.service.CodeService;
import com.shanduo.newretail.service.UserService;
import com.shanduo.newretail.util.PatternUtils;
import com.shanduo.newretail.util.StringUtils;

/**
 * 用户操作接口层
 * @ClassName: UserController
 * @Description: TODO
 * @author fanshixin
 * @date 2018年7月12日 下午2:22:49
 *
 */
@Controller
@RequestMapping(value = "juser")
public class UserController {

	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private BaseService baseService;
	@Autowired
	private UserService userService;
	@Autowired
	private CodeService codeService;
	
	/**
	 * 用户注册
	 * @Title: registerUser
	 * @Description: TODO
	 * @param @param request
	 * @param @param phone 手机号
	 * @param @param code 验证码
	 * @param @param password 密码
	 * @param @param parentId 推荐人ID
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "registeruser",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean registerUser(HttpServletRequest request, String phone, String code, String password,String parentId, String openId) {
		if(StringUtils.isNull(phone) || PatternUtils.patternPhone(phone)) {
			log.warn("phone is error waith phone:{}", phone);
			return new ErrorBean(ErrorConsts.CODE_10002, "手机号输入错误");
		}
		if(StringUtils.isNull(code) || PatternUtils.patternCode(code)) {
			log.warn("code is error waith code:{}", code);
			return new ErrorBean(ErrorConsts.CODE_10002, "验证码输入错误");
		}
		if(StringUtils.isNull(password) || PatternUtils.patternPassword(password)) {
			log.warn("password is error waith password:{}", password);
			return new ErrorBean(ErrorConsts.CODE_10002, "密码输入错误");
		}
		if(codeService.checkCode(phone, code, DefaultConsts.CODE_REGISTER)) {
			return new ErrorBean(ErrorConsts.CODE_10003, "验证码错误或超时");
		}
//		if(StringUtils.isNull(openId)) {
//			log.warn("openId is null waith openId:{}", openId);
//			return new ErrorBean(ErrorConsts.CODE_10002, "openId为空");
//		}
		try {
			userService.saveUser(openId, phone, password, parentId);
		} catch (Exception e) {
			return new ErrorBean(ErrorConsts.CODE_10003, "注册失败");
		}
		TokenInfo token = userService.loginUser(phone, password);
		if(token == null) {
			return new SuccessBean("注册成功");
		}
		return new SuccessBean(token);
	}
	
	/**
	 * 用户登录
	 * @Title: loginUser
	 * @Description: TODO
	 * @param @param request
	 * @param @param phone 手机号
	 * @param @param password 密码
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "loginuser",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean loginUser(HttpServletRequest request, String phone, String password) {
		if(StringUtils.isNull(phone) || PatternUtils.patternPhone(phone)) {
			log.warn("phone is error waith phone:{}", phone);
			return new ErrorBean(ErrorConsts.CODE_10002, "手机号输入错误");
		}
		if(StringUtils.isNull(password) || PatternUtils.patternPassword(password)) {
			log.warn("password is error waith password:{}", password);
			return new ErrorBean(ErrorConsts.CODE_10002, "密码输入错误");
		}
		TokenInfo token = userService.loginUser(phone, password);
		if(token == null) {
			return new ErrorBean(ErrorConsts.CODE_10003, "账号或密码错误");
		}
		return new SuccessBean(token);
	}
	
	/**
	 * 修改手机号
	 * @Title: updatePhone
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param phone
	 * @param @param code
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "updatephone",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean updatePhone(HttpServletRequest request, String token, String phone, String code) {
		String userId = baseService.checkUserToken(token);
		if(userId == null) {
			return new ErrorBean(ErrorConsts.CODE_10001, "请重新登录");
		}
		if(StringUtils.isNull(phone) || PatternUtils.patternPhone(phone)) {
			log.warn("phone is error waith phone:{}", phone);
			return new ErrorBean(ErrorConsts.CODE_10002, "手机号输入错误");
		}
		if(StringUtils.isNull(code) || PatternUtils.patternCode(code)) {
			log.warn("code is error waith code:{}", code);
			return new ErrorBean(ErrorConsts.CODE_10002, "验证码输入错误");
		}
		if(codeService.checkCode(phone, code, DefaultConsts.CODE_PHONE)) {
			return new ErrorBean(ErrorConsts.CODE_10003, "验证码错误或超时");
		}
		int i = userService.updateUser(userId, phone, DefaultConsts.NUMBER_1);
		if(i < 1) {
			log.warn("updatephone is error waith userId:{} and phone:{}", userId, phone);
			return new ErrorBean(ErrorConsts.CODE_10004, "修改失败");
		}
		return new SuccessBean("修改成功");
	}
	
	/**
	 * 修改登录密码
	 * @Title: updatePassword
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param typeId 类型:1.验证码修改;2.老密码修改;
	 * @param @param phone
	 * @param @param code
	 * @param @param password
	 * @param @param newPassword
	 * @param @return
	 * @return ResultBean
	 * @throws
	 */
	@RequestMapping(value = "updatepassword",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean updatePassword(HttpServletRequest request,String token,String typeId, String phone,
			String code, String password, String newPassword) {
		if(StringUtils.isNull(typeId) || !typeId.matches("^[12]$")) {
			log.warn("typeId is error waith typeId:{}", typeId);
			return new ErrorBean(ErrorConsts.CODE_10002, "类型错误");
		}
		if(StringUtils.isNull(newPassword) || PatternUtils.patternPassword(newPassword)) {
			log.warn("newpassword is error waith newpassword:{}", newPassword);
			return new ErrorBean(ErrorConsts.CODE_10002, "新密码输入错误");
		}
		if(typeId.equals(DefaultConsts.NUMBER_1)) {
			if(StringUtils.isNull(phone) || PatternUtils.patternPhone(phone)) {
				log.warn("phone is error waith phone:{}", phone);
				return new ErrorBean(ErrorConsts.CODE_10002, "手机号输入错误");
			}
			if(StringUtils.isNull(code) || PatternUtils.patternCode(code)) {
				log.warn("code is error waith code:{}", code);
				return new ErrorBean(ErrorConsts.CODE_10002, "验证码输入错误");
			}
			if(codeService.checkCode(phone, code, DefaultConsts.CODE_PASSWORD)) {
				return new ErrorBean(ErrorConsts.CODE_10003, "验证码错误或超时");
			}
			int i = userService.updatePassswordByPhone(phone, newPassword);
			if(i < 1) {
				log.warn("updatephone is error waith phone:{} and newpassword:{}", phone, newPassword);
				return new ErrorBean(ErrorConsts.CODE_10004, "修改失败");
			}
		}else {
			String userId = baseService.checkUserToken(token);
			if(userId == null) {
				return new ErrorBean(ErrorConsts.CODE_10001, "请重新登录");
			}
			if(StringUtils.isNull(password) || PatternUtils.patternPassword(password)) {
				log.warn("password is error waith password:{}", password);
				return new ErrorBean(ErrorConsts.CODE_10002, "密码输入错误");
			}
			if(userService.checkUserPassword(userId, password)) {
				log.warn("password is error waith password:{}", password);
				return new ErrorBean(ErrorConsts.CODE_10003, "旧密码输入错误");
			}
			int i = userService.updateUser(userId, newPassword, DefaultConsts.NUMBER_2);
			if(i < 1) {
				log.warn("updatephone is error waith userId:{} and phone:{}", userId, phone);
				return new ErrorBean(ErrorConsts.CODE_10004, "修改失败");
			}
		}
		return new SuccessBean("修改成功");
	}
}
