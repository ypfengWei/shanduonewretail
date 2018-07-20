package com.shanduo.newretail.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.shanduo.newretail.consts.DefaultConsts;
import com.shanduo.newretail.consts.ErrorConsts;
import com.shanduo.newretail.entity.PresentRecord;
import com.shanduo.newretail.service.BaseService;
import com.shanduo.newretail.service.PresentService;
import com.shanduo.newretail.service.SellerService;
import com.shanduo.newretail.util.ResultUtils;
import com.shanduo.newretail.util.StringUtils;

/**
 * 提现接口层
 * @ClassName: PresentController
 * @Description: TODO
 * @author fanshixin
 * @date 2018年7月20日 上午9:22:34
 *
 */
@Controller
@RequestMapping(value = "jpresent")
public class PresentController {

	private static final Logger log = LoggerFactory.getLogger(PresentController.class);
	
	@Autowired
	private BaseService baseService;
	@Autowired
	private SellerService sellerService;
	@Autowired
	private PresentService presentService;
	
	/**
	 * 申请提现
	 * @Title: savePresent
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param money
	 * @param @param typeId
	 * @param @param name
	 * @param @param openingBank
	 * @param @param bankName
	 * @param @param cardNumber
	 * @param @return
	 * @return JSONObject
	 * @throws
	 */
	@RequestMapping(value = "savepresent",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public JSONObject savePresent(HttpServletRequest request, String token, String money, String typeId, 
			String name, String openingBank, String bankName, String cardNumber) {
		String userId = baseService.checkUserToken(token);
		if(userId == null) {
			return ResultUtils.error(ErrorConsts.CODE_10001, "请重新登录");
		}
		if(baseService.checkUserRole(userId, DefaultConsts.ROLE_MERCHANT)) {
			return ResultUtils.error(ErrorConsts.CODE_10003, ErrorConsts.USER_LIMITED_AUTHORITY);
		}
		if(StringUtils.isNull(typeId) || !typeId.matches("^[12]$")) {
			return ResultUtils.error(ErrorConsts.CODE_10002, "提现类型错误");
		}
		if(StringUtils.isNull(money) || !money.matches("^\\d+(\\.\\d{0,2})$")) {
			log.warn("充值金额错误");
			return ResultUtils.error(ErrorConsts.CODE_10002, "充值金额错误");
		}
		if(typeId.equals("1")) {
			return ResultUtils.error(ErrorConsts.CODE_10003, "微信提现维护中");
		}else {
			if(StringUtils.isNull(name)) {
				return ResultUtils.error(ErrorConsts.CODE_10002, "姓名为空");
			}
			if(StringUtils.isNull(openingBank)) {
				return ResultUtils.error(ErrorConsts.CODE_10002, "参数错误");
			}
			if(StringUtils.isNull(bankName)) {
				return ResultUtils.error(ErrorConsts.CODE_10002, "参数错误");
			}
			if(StringUtils.isNull(cardNumber)) {
				return ResultUtils.error(ErrorConsts.CODE_10002, "参数错误");
			}
		}
		if(sellerService.selectMoney(new BigDecimal(money), userId) == 0) {
			return ResultUtils.error(ErrorConsts.CODE_10003, "余额不足");
		}
		try {
			presentService.savePresent(userId, money, typeId, name, openingBank, bankName, cardNumber);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtils.error(ErrorConsts.CODE_10004, "申请失败");
		}
		return ResultUtils.success("申请成功");
	}
	
	/**
	 * 同意提现
	 * @Title: presentSucceed
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param presentId
	 * @param @return
	 * @return JSONObject
	 * @throws
	 */
	@RequestMapping(value = "presentsucceed",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public JSONObject presentSucceed(HttpServletRequest request, String token, String presentId) {
		String userId = baseService.checkUserToken(token);
		if(userId == null) {
			return ResultUtils.error(ErrorConsts.CODE_10001, "请重新登录");
		}
		if(baseService.checkUserRole(userId, DefaultConsts.ROLE_ADMIN)) {
			return ResultUtils.error(ErrorConsts.CODE_10003, ErrorConsts.USER_LIMITED_AUTHORITY);
		}
		if(StringUtils.isNull(presentId)) {
			return ResultUtils.error(ErrorConsts.CODE_10002, "参数错误");
		}
		PresentRecord presentRecord = presentService.getPresentRecord(presentId, "1");
		if(presentRecord == null) {
			return ResultUtils.error(ErrorConsts.CODE_10003, "已处理");
		}
		int i = presentService.updateSucceed(presentId);
		if(i < 1) {
			return ResultUtils.error(ErrorConsts.CODE_10004, "提现失败");
		}
		return ResultUtils.success("提现成功");
	}
	
	/**
	 * 拒绝提现
	 * @Title: rejectSucceed
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param presentId
	 * @param @return
	 * @return JSONObject
	 * @throws
	 */
	@RequestMapping(value = "rejectsucceed",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public JSONObject rejectSucceed(HttpServletRequest request, String token, String presentId) {
		String userId = baseService.checkUserToken(token);
		if(userId == null) {
			return ResultUtils.error(ErrorConsts.CODE_10001, "请重新登录");
		}
		if(baseService.checkUserRole(userId, DefaultConsts.ROLE_ADMIN)) {
			return ResultUtils.error(ErrorConsts.CODE_10003, ErrorConsts.USER_LIMITED_AUTHORITY);
		}
		if(StringUtils.isNull(presentId)) {
			return ResultUtils.error(ErrorConsts.CODE_10002, "参数错误");
		}
		PresentRecord presentRecord = presentService.getPresentRecord(presentId, "1");
		if(presentRecord == null) {
			return ResultUtils.error(ErrorConsts.CODE_10003, "已处理");
		}
		try {
			presentService.updateReject(presentId);
		} catch (Exception e) {
			return ResultUtils.error(ErrorConsts.CODE_10004, "拒绝失败");
		}
		return ResultUtils.success("拒绝成功");
	}
	
	/**
	 * 管理员查询提现记录
	 * @Title: presentList
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param state
	 * @param @param page
	 * @param @param pageSize
	 * @param @return
	 * @return JSONObject
	 * @throws
	 */
	@RequestMapping(value = "presentList",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public JSONObject presentList(HttpServletRequest request, String token, String state, 
			String page, String pageSize) {
		String userId = baseService.checkUserToken(token);
		if(userId == null) {
			return ResultUtils.error(ErrorConsts.CODE_10001, "请重新登录");
		}
		if(baseService.checkUserRole(userId, DefaultConsts.ROLE_ADMIN)) {
			return ResultUtils.error(ErrorConsts.CODE_10003, ErrorConsts.USER_LIMITED_AUTHORITY);
		}
		if(StringUtils.isNull(state) || !state.matches("^[1-6]$")) {
			log.warn("state is error waith state:{}", state);
			return ResultUtils.error(ErrorConsts.CODE_10002, "状态错误");
		}
		if(StringUtils.isNull(page) || !page.matches("^\\d*$")) {
			log.warn("page is error waith page:{}", page);
			return ResultUtils.error(ErrorConsts.CODE_10002, "页码错误");
		}
		if(StringUtils.isNull(pageSize) || !pageSize.matches("^\\d*$")) {
			log.warn("pageSize is error waith pageSize:{}", pageSize);
			return ResultUtils.error(ErrorConsts.CODE_10002, "记录错误");
		}
		Integer pages = Integer.valueOf(page);
		Integer pageSizes = Integer.valueOf(pageSize);
		Map<String, Object> resultMap = new HashMap<>(3);
		try {
			resultMap = presentService.listPresent(state, pages, pageSizes);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtils.error(ErrorConsts.CODE_10002, "查询错误");
		}
		return ResultUtils.success(resultMap);
	}
	
	/**
	 * 提现记录
	 * @Title: presentRecord
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param page
	 * @param @param pageSize
	 * @param @return
	 * @return JSONObject
	 * @throws
	 */
	@RequestMapping(value = "presentrecord",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public JSONObject presentRecord(HttpServletRequest request, String token, String page, String pageSize) {
		String userId = baseService.checkUserToken(token);
		if(userId == null) {
			return ResultUtils.error(ErrorConsts.CODE_10001, "请重新登录");
		}
		if(baseService.checkUserRole(userId, DefaultConsts.ROLE_MERCHANT)) {
			return ResultUtils.error(ErrorConsts.CODE_10003, ErrorConsts.USER_LIMITED_AUTHORITY);
		}
		if(StringUtils.isNull(page) || !page.matches("^\\d*$")) {
			log.warn("page is error waith page:{}", page);
			return ResultUtils.error(ErrorConsts.CODE_10002, "页码错误");
		}
		if(StringUtils.isNull(pageSize) || !pageSize.matches("^\\d*$")) {
			log.warn("pageSize is error waith pageSize:{}", pageSize);
			return ResultUtils.error(ErrorConsts.CODE_10002, "记录错误");
		}
		Integer pages = Integer.valueOf(page);
		Integer pageSizes = Integer.valueOf(pageSize);
		Map<String, Object> resultMap = new HashMap<>(3);
		try {
			resultMap = presentService.listPresents(userId, pages, pageSizes);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtils.error(ErrorConsts.CODE_10002, "查询错误");
		}
		return ResultUtils.success(resultMap);
	}
	
}