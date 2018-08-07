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
import com.shanduo.newretail.consts.WxPayConsts;
import com.shanduo.newretail.entity.service.TokenInfo;
import com.shanduo.newretail.service.BaseService;
import com.shanduo.newretail.service.CodeService;
import com.shanduo.newretail.service.UserService;
import com.shanduo.newretail.util.HttpRequest;
import com.shanduo.newretail.util.PatternUtils;
import com.shanduo.newretail.util.ResultUtils;
import com.shanduo.newretail.util.StringUtils;

import com.alibaba.fastjson.JSONObject;

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
	 * @param @param typeId 注册类型:2.区域管理;3.店铺;4.业务员;
	 * @param @param phone 手机号
	 * @param @param code 验证码
	 * @param @param password 密码
	 * @param @param parentId 推荐人ID
	 * @param @param openId 
	 * @param @param name 昵称
	 * @param @return
	 * @return JSONObject
	 * @throws
	 */
	@RequestMapping(value = "registeruser",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public JSONObject registerUser(HttpServletRequest request, String typeId, String phone, String code, String password,
			String parentId, String openId, String name) {
		if(StringUtils.isNull(typeId) || !typeId.matches("^[234]$")) {
			log.warn("typeId is error waith typeId:{}", typeId);
			return ResultUtils.error(ErrorConsts.CODE_10002, "注册类型错误");
		}
		if(StringUtils.isNull(phone) || PatternUtils.patternPhone(phone)) {
			log.warn("phone is error waith phone:{}", phone);
			return ResultUtils.error(ErrorConsts.CODE_10002, "手机号输入错误");
		}
		if(StringUtils.isNull(code) || PatternUtils.patternCode(code)) {
			log.warn("code is error waith code:{}", code);
			return ResultUtils.error(ErrorConsts.CODE_10002, "验证码输入错误");
		}
		if(StringUtils.isNull(password) || PatternUtils.patternPassword(password)) {
			log.warn("password is error waith password:{}", password);
			return ResultUtils.error(ErrorConsts.CODE_10002, "密码输入错误");
		}
		if(codeService.checkCode(phone, code, DefaultConsts.CODE_REGISTER)) {
			return ResultUtils.error(ErrorConsts.CODE_10003, "验证码错误或超时");
		}
		if(StringUtils.isNull(name)) {
			return ResultUtils.error(ErrorConsts.CODE_10002, "昵称不能为空");
		}
//		if(StringUtils.isNull(openId)) {
//			log.warn("openId is null waith openId:{}", openId);
//			return ResultUtils.error(ErrorConsts.CODE_10002, "openId为空");
//		}
		parentId = baseService.checkUserToken(parentId);
		if (null == parentId){
			log.warn("token");
			return ResultUtils.error(ErrorConsts.CODE_10002, "token错误");
		}
		try {
			userService.saveUser(openId, phone, password, parentId, typeId, name);
		} catch (Exception e) {
			return ResultUtils.error(ErrorConsts.CODE_10003, "注册失败");
		}
		TokenInfo token = userService.loginUser(phone, password);
		if(token == null) {
			return ResultUtils.success("注册成功");
		}
		return ResultUtils.success(token);
	}
	
	/**
	 * 用户登录
	 * @Title: loginUser
	 * @Description: TODO
	 * @param @param request
	 * @param @param phone 手机号
	 * @param @param password 密码
	 * @param @return
	 * @return JSONObject
	 * @throws
	 */
	@RequestMapping(value = "loginuser",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public JSONObject loginUser(HttpServletRequest request, String phone, String password,String code) {
		if(StringUtils.isNull(phone) || PatternUtils.patternPhone(phone)) {
			log.warn("phone is error waith phone:{}", phone);
			return ResultUtils.error(ErrorConsts.CODE_10002, "手机号输入错误");
		}
		if(StringUtils.isNull(password) || PatternUtils.patternPassword(password)) {
			log.warn("password is error waith password:{}", password);
			return ResultUtils.error(ErrorConsts.CODE_10002, "密码输入错误");
		}
		TokenInfo token = userService.loginUser(phone, password);
		if(token == null) {
			return ResultUtils.error(ErrorConsts.CODE_10003, "账号或密码错误");
		}
		if("3".equals(token.getJurisdiction())){
			if("".equals(token.getOpenId()) || null==token.getOpenId()) {
				if (StringUtils.isNull(code)) {
		            log.warn("code为空");
		            return ResultUtils.error(ErrorConsts.CODE_10002, "code为空");
		        }
				 String requestUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code".replace("APPID", WxPayConsts.APPID).replace("SECRET", WxPayConsts.APPSECRET).replace("CODE", code);
			        // 发起GET请求获取凭证
			       net.sf.json.JSONObject jsonObject = HttpRequest.httpsRequest(requestUrl, "GET", null);
			       String openId = jsonObject.getString("openid");
			       try{
			        	int count = userService.updateopenId(openId, phone);
			        	if(count<1){
			        		return ResultUtils.error(ErrorConsts.CODE_10004, "修改openId失败");
			        	}
			        }catch (Exception e) {
			        	return ResultUtils.error(ErrorConsts.CODE_10004, "修改openId失败");
				}
			}
			
		}
		return ResultUtils.success(token);
	}
	
	/**
	 * 用户登录
	 * @Title: loginUsers
	 * @Description: TODO
	 * @param @param request
	 * @param @param phone 手机号
	 * @param @param password 密码
	 * @param @return
	 * @return JSONObject
	 * @throws
	 */
	@RequestMapping(value = "loginusers",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public JSONObject loginUsers(HttpServletRequest request, String phone, String password) {
		if(StringUtils.isNull(phone) || PatternUtils.patternPhone(phone)) {
			log.warn("phone is error waith phone:{}", phone);
			return ResultUtils.error(ErrorConsts.CODE_10002, "手机号输入错误");
		}
		if(StringUtils.isNull(password) || PatternUtils.patternPassword(password)) {
			log.warn("password is error waith password:{}", password);
			return ResultUtils.error(ErrorConsts.CODE_10002, "密码输入错误");
		}
		TokenInfo token = userService.loginUser(phone, password);
		if(token == null) {
			return ResultUtils.error(ErrorConsts.CODE_10003, "账号或密码错误");
		}
		return ResultUtils.success(token);
	}
    @RequestMapping(value = "selectuser",method={RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public JSONObject selectUser(HttpServletRequest request, String token) {
        if (StringUtils.isNull(token)) {
            log.warn("token为空");
            return ResultUtils.error(ErrorConsts.CODE_10002, "token为空");
        }
        String id = baseService.checkUserToken(token);
        if (null == id) {
            log.warn("token失效");
            return ResultUtils.error(ErrorConsts.CODE_10001, "token失效");
        }
        return ResultUtils.success(userService.selectUser(id));
    }
	/**
	 * 查询版本升级
	 * @param request
	 * @param appType
	 * @return
	 */
    @RequestMapping(value = "selectapp",method={RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public JSONObject selectAPP(HttpServletRequest request, Integer appType) {
       
        return ResultUtils.success(userService.selectApp(appType));
    }
	/**
	 * 修改手机号
	 * @Title: updatePhone
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param phone 手机号
	 * @param @param code 验证码
	 * @param @return
	 * @return JSONObject
	 * @throws
	 */
	@RequestMapping(value = "updatephone",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public JSONObject updatePhone(HttpServletRequest request, String token, String phone, String code) {
		String userId = baseService.checkUserToken(token);
		if(userId == null) {
			return ResultUtils.error(ErrorConsts.CODE_10001, "请重新登录");
		}
		if(StringUtils.isNull(phone) || PatternUtils.patternPhone(phone)) {
			log.warn("phone is error waith phone:{}", phone);
			return ResultUtils.error(ErrorConsts.CODE_10002, "手机号输入错误");
		}
		if(StringUtils.isNull(code) || PatternUtils.patternCode(code)) {
			log.warn("code is error waith code:{}", code);
			return ResultUtils.error(ErrorConsts.CODE_10002, "验证码输入错误");
		}
		if(codeService.checkCode(phone, code, DefaultConsts.CODE_PHONE)) {
			return ResultUtils.error(ErrorConsts.CODE_10003, "验证码错误或超时");
		}
		int i = userService.updateUser(userId, phone, DefaultConsts.NUMBER_1);
		if(i < 1) {
			log.warn("updatephone is error waith userId:{} and phone:{}", userId, phone);
			return ResultUtils.error(ErrorConsts.CODE_10004, "修改失败");
		}
		return ResultUtils.success("修改成功");
	}
	
	/**
	 * 修改登录密码
	 * @Title: updatePassword
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param typeId 类型:1.验证码修改;2.老密码修改;
	 * @param @param phone 手机号
	 * @param @param code 验证码
	 * @param @param password 旧密码
	 * @param @param newPassword 新密码
	 * @param @return
	 * @return JSONObject
	 * @throws
	 */
	@RequestMapping(value = "updatepassword",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public JSONObject updatePassword(HttpServletRequest request,String token,String typeId, String phone,
			String code, String password, String newPassword) {
		if(StringUtils.isNull(typeId) || !typeId.matches("^[12]$")) {
			log.warn("typeId is error waith typeId:{}", typeId);
			return ResultUtils.error(ErrorConsts.CODE_10002, "类型错误");
		}
		if(StringUtils.isNull(newPassword) || PatternUtils.patternPassword(newPassword)) {
			log.warn("newpassword is error waith newpassword:{}", newPassword);
			return ResultUtils.error(ErrorConsts.CODE_10002, "新密码输入错误");
		}
		if(typeId.equals(DefaultConsts.NUMBER_1)) {
			if(StringUtils.isNull(phone) || PatternUtils.patternPhone(phone)) {
				log.warn("phone is error waith phone:{}", phone);
				return ResultUtils.error(ErrorConsts.CODE_10002, "手机号输入错误");
			}
			if(StringUtils.isNull(code) || PatternUtils.patternCode(code)) {
				log.warn("code is error waith code:{}", code);
				return ResultUtils.error(ErrorConsts.CODE_10002, "验证码输入错误");
			}
			if(codeService.checkCode(phone, code, DefaultConsts.CODE_PASSWORD)) {
				return ResultUtils.error(ErrorConsts.CODE_10003, "验证码错误或超时");
			}
			int i = userService.updatePassswordByPhone(phone, newPassword);
			if(i < 1) {
				log.warn("updatephone is error waith phone:{} and newpassword:{}", phone, newPassword);
				return ResultUtils.error(ErrorConsts.CODE_10004, "修改失败");
			}
		}else {
			String userId = baseService.checkUserToken(token);
			if(userId == null) {
				return ResultUtils.error(ErrorConsts.CODE_10001, "请重新登录");
			}
			if(StringUtils.isNull(password) || PatternUtils.patternPassword(password)) {
				log.warn("password is error waith password:{}", password);
				return ResultUtils.error(ErrorConsts.CODE_10002, "密码输入错误");
			}
			if(userService.checkUserPassword(userId, password)) {
				log.warn("password is error waith password:{}", password);
				return ResultUtils.error(ErrorConsts.CODE_10003, "旧密码输入错误");
			}
			int i = userService.updateUser(userId, newPassword, DefaultConsts.NUMBER_2);
			if(i < 1) {
				log.warn("updatephone is error waith userId:{} and phone:{}", userId, phone);
				return ResultUtils.error(ErrorConsts.CODE_10004, "修改失败");
			}
		}
		return ResultUtils.success("修改成功");
	}
	
	
	@RequestMapping(value = "updateopenid",method={RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public JSONObject updateOpenId(HttpServletRequest request, String code,String id) {
        if (StringUtils.isNull(code)) {
            log.warn("openId为空");
            return ResultUtils.error(ErrorConsts.CODE_10002, "openId为空");
        }
        String requestUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code".replace("APPID", WxPayConsts.APPID).replace("SECRET", WxPayConsts.APPSECRET).replace("CODE", code);
        // 发起GET请求获取凭证
       net.sf.json.JSONObject jsonObject = HttpRequest.httpsRequest(requestUrl, "GET", null);
       String openId = jsonObject.getString("openid");
        if (StringUtils.isNull(id)) {
            log.warn("id为空");
            return ResultUtils.error(ErrorConsts.CODE_10002, "id为空");
        }
        try{
        	int count = userService.updateopenId(openId, id);
        	if(count<1){
        		return ResultUtils.error(ErrorConsts.CODE_10004, "修改openId失败");
        	}
        }catch (Exception e) {
        	return ResultUtils.error(ErrorConsts.CODE_10004, "修改openId失败");
		}
        
        return ResultUtils.success("修改成功");
    }
	
}
