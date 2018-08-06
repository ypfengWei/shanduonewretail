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
import com.shanduo.newretail.consts.WxPayConsts;
import com.shanduo.newretail.entity.PresentRecord;
import com.shanduo.newretail.entity.ToUser;
import com.shanduo.newretail.service.BaseService;
import com.shanduo.newretail.service.PresentService;
import com.shanduo.newretail.service.SellerService;
import com.shanduo.newretail.service.UserService;
import com.shanduo.newretail.util.ClientCustomSSL;
import com.shanduo.newretail.util.GetRSA;
import com.shanduo.newretail.util.IpUtils;
import com.shanduo.newretail.util.ResultUtils;
import com.shanduo.newretail.util.StringUtils;
import com.shanduo.newretail.util.UUIDGenerator;
import com.shanduo.newretail.util.WxPayUtils;

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
	@Autowired
	private UserService userService;
	
	/**
	 * 申请提现
	 * @Title: savePresent
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param money 提现金额
	 * @param @param typeId 1:微信提现2：银行卡提现
	 * @param @param name 持卡人姓名
	 * @param @param openingBank 开户行名称
	 * @param @param bankName 所在卡银行名称
	 * @param @param cardNumber 银行卡号
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
		if(StringUtils.isNull(money) || !money.matches("^\\d+(\\.\\d{0,2})?$")) {
			log.warn("提现金额错误");
			return ResultUtils.error(ErrorConsts.CODE_10002, "提现金额错误");
		}
		if(StringUtils.isNull(name)) {
			return ResultUtils.error(ErrorConsts.CODE_10002, "姓名为空");
		}
		BigDecimal moneys = new BigDecimal(money);
		if(moneys.intValue() < 1) {
			return ResultUtils.error(ErrorConsts.CODE_10003, "金额必须大于1");
		}
		if(sellerService.selectMoney(moneys, userId) == 0) {
			return ResultUtils.error(ErrorConsts.CODE_10003, "余额不足");
		}
		ToUser user = userService.selectUser(userId);
		if(!user.getName().equals(name)) {
			log.warn("name is error waith: userName:{} and name:{}",user.getName(),name);
			return ResultUtils.error(ErrorConsts.CODE_10003, "提现人与用户名不一致");
		}
		if("2".equals(typeId)) {
			if(StringUtils.isNull(cardNumber)) {
				return ResultUtils.error(ErrorConsts.CODE_10002, "银行卡号为空");
			}
			if(StringUtils.isNull(bankName)) {
				return ResultUtils.error(ErrorConsts.CODE_10002, "银行id为空");
			}
//			if(StringUtils.isNull(openingBank)) {
//				return ResultUtils.error(ErrorConsts.CODE_10002, "银行id为空");
//			}
		}
		try {
			presentService.savePresent(userId, money, typeId, name, openingBank, bankName, cardNumber);
		} catch (Exception e) {
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
	 * @param @throws Exception
	 * @return JSONObject
	 * @throws
	 */
	@RequestMapping(value = "presentsucceed",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public JSONObject presentSucceed(HttpServletRequest request, String token, String presentId) throws Exception {
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
		if("1".equals(presentRecord.getTypeid())) {
			return transfers(request, presentRecord);
		}
		return payBank(presentRecord);
//		return ResultUtils.error(ErrorConsts.CODE_10003, "银行卡提现维护中");
	}
	
	/**
	 * 拒绝提现
	 * @Title: rejectSucceed
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @param presentId 提现订单ID
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
		//银行卡提现查询是否已经向微信申请,只有没有申请才可以拒绝
		if("2".equals(presentRecord.getTypeid())) {
			Map<String, Object> resultMap = queryBank(presentRecord);
			String returnCode = resultMap.get("return_code").toString();
			if("FAIL".equals(returnCode)) {
				log.error(resultMap.toString());
				return ResultUtils.error(ErrorConsts.CODE_10004, resultMap.get("return_msg").toString());
			}
			String resultCode = resultMap.get("result_code").toString();
			if("FAIL".equals(resultCode)) {
				String errCode = resultMap.get("err_code").toString();
				if(!"ORDERNOTEXIST".equals(errCode)) {
					log.error(resultMap.toString());
					return ResultUtils.error(ErrorConsts.CODE_10004, resultMap.get("err_code_des").toString());
				}
			}else {
				String status = resultMap.get("status").toString();
				if("PROCESSING".equals(status)) {
					return ResultUtils.error(ErrorConsts.CODE_10003, "微信处理中,拒绝操作");
				}
				if("SUCCESS".equals(status)) {
					int i = presentService.updateSucceed(presentId);
					if(i < 1) {
						return ResultUtils.error(ErrorConsts.CODE_10004, "拒绝失败");
					}
					return ResultUtils.success("微信已经处理完毕改为提现成功");
				}
			}
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
	 * @param @param state 订单状态:1:申请提现2:同意提现;3.拒绝提现;
	 * @param @param page 页码
	 * @param @param pageSize 记录数
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
		Map<String, Object> resultMap = new HashMap<>(4);
		try {
			resultMap = presentService.listPresent(state, pages, pageSizes);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtils.error(ErrorConsts.CODE_10002, "查询错误");
		}
		return ResultUtils.success(resultMap);
	}
	
	/**
	 * 卖家查询提现记录
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
		Map<String, Object> resultMap = new HashMap<>(4);
		try {
			resultMap = presentService.listPresents(userId, pages, pageSizes);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtils.error(ErrorConsts.CODE_10002, "查询错误");
		}
		return ResultUtils.success(resultMap);
	}
	
	/**
	 * 查询最近一次银行卡提现的信息
	 * @Title: getPresent
	 * @Description: TODO
	 * @param @param request
	 * @param @param token
	 * @param @return
	 * @return JSONObject
	 * @throws
	 */
	@RequestMapping(value = "getpresent",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public JSONObject getPresent(HttpServletRequest request, String token) {
		String userId = baseService.checkUserToken(token);
		if(userId == null) {
			return ResultUtils.error(ErrorConsts.CODE_10001, "请重新登录");
		}
		if(baseService.checkUserRole(userId, DefaultConsts.ROLE_MERCHANT)) {
			return ResultUtils.error(ErrorConsts.CODE_10003, ErrorConsts.USER_LIMITED_AUTHORITY);
		}
		Map<String, String> map = new HashMap<>(4);
		try {
			map = presentService.getPresent(userId);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtils.error(ErrorConsts.CODE_10002, "查询错误");
		}
		return ResultUtils.success(map);
	}
	
	/**
	 * 企业付款到零钱
	 * @Title: transfers
	 * @Description: TODO
	 * @param @param request
	 * @param @param presentRecord
	 * @param @return
	 * @param @throws Exception
	 * @return JSONObject
	 * @throws
	 */
	private JSONObject transfers(HttpServletRequest request, PresentRecord presentRecord) throws Exception {
		String presentId = presentRecord.getId();
		//价格，单位为分
		BigDecimal amount = presentRecord.getAmountCash();
		amount = amount.multiply(new BigDecimal("100"));
		//订单总金额
		Integer moneys = amount.intValue();
		ToUser user = userService.selectUser(presentRecord.getUserId());
		Map<String, String> paramsMap = new HashMap<>(11);
		paramsMap.put("mch_appid", WxPayConsts.APPID);
		paramsMap.put("mchid", WxPayConsts.MCH_ID);
		paramsMap.put("nonce_str", UUIDGenerator.getUUID());
		paramsMap.put("partner_trade_no", presentId);
		paramsMap.put("openid", user.getOpenId());
		paramsMap.put("check_name", "NO_CHECK");
//		paramsMap.put("re_user_name", presentRecord.getUserName());
		paramsMap.put("amount", moneys.toString());
		paramsMap.put("desc", "用户提现");
		paramsMap.put("spbill_create_ip", IpUtils.getIpAddress(request));
		//把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
		String paramsString = WxPayUtils.createLinkString(paramsMap);
		//MD5运算生成签名
		String sign = WxPayUtils.sign(paramsString, WxPayConsts.KEY, "utf-8").toUpperCase();
		//签名
		paramsMap.put("sign", sign);
		String paramsXml = WxPayUtils.map2Xmlstring(paramsMap);
		String result = ClientCustomSSL.doRefund(WxPayConsts.TRANSFERS_URL, paramsXml);
		Map<String, Object> resultMap = WxPayUtils.Str2Map(result);
		String returnCode = resultMap.get("return_code").toString();
		if(!returnCode.equals("SUCCESS")) {
			log.error(resultMap.get("return_msg").toString());
			return ResultUtils.error(ErrorConsts.CODE_10004,"提现出错");
		}
		String resultCode = resultMap.get("result_code").toString();
		if(resultCode.equals("SUCCESS")) {
			int i = presentService.updateSucceed(presentId);
			if(i < 1) {
				return ResultUtils.error(ErrorConsts.CODE_10004, "提现失败");
			}
			return ResultUtils.success("提现成功");
		}
		String errCode = resultMap.get("err_code").toString();
		if(errCode.equals("SYSTEMERROR")) {
			paramsMap = new HashMap<>(5);
			paramsMap.put("mch_id", WxPayConsts.MCH_ID);
			paramsMap.put("appid", WxPayConsts.APPID);
			paramsMap.put("nonce_str", UUIDGenerator.getUUID());
			paramsMap.put("partner_trade_no", presentId);
			//把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
			paramsString = WxPayUtils.createLinkString(paramsMap);
			//MD5运算生成签名
			sign = WxPayUtils.sign(paramsString, WxPayConsts.KEY, "utf-8").toUpperCase();
			//签名
			paramsMap.put("sign", sign);
			paramsXml = WxPayUtils.map2Xmlstring(paramsMap);
			result = ClientCustomSSL.doRefund(WxPayConsts.GETTRANSFERINFO_URL, paramsXml);
			resultMap = WxPayUtils.Str2Map(result);
			returnCode = resultMap.get("return_code").toString();
			if(!returnCode.equals("SUCCESS")) {
				log.error(resultMap.get("return_msg").toString());
				return ResultUtils.error(ErrorConsts.CODE_10004,"提现出错");
			}
			resultCode = resultMap.get("result_code").toString();
			if(!resultCode.equals("SUCCESS")) {
				log.error(resultMap.get("err_code_des").toString());
				return ResultUtils.error(ErrorConsts.CODE_10004,"提现出错");
			}
			String status = resultMap.get("status").toString();
			if(status.equals("SUCCESS")) {
				int i = presentService.updateSucceed(presentId);
				if(i < 1) {
					return ResultUtils.error(ErrorConsts.CODE_10004, "提现失败");
				}
				return ResultUtils.success("提现成功");
			}else if(status.equals("FAILED")) {
				try {
					presentService.updateReject(presentId);
				} catch (Exception e) {
					return ResultUtils.error(ErrorConsts.CODE_10004, "拒绝失败");
				}
				return ResultUtils.error(ErrorConsts.CODE_10003,"提现失败");
			}
			return ResultUtils.error(ErrorConsts.CODE_10003,"提现处理中");
		}else {
			String errCodeDes = resultMap.get("err_code_des").toString();
			log.error(resultMap.toString());
			return ResultUtils.error(ErrorConsts.CODE_10003, errCodeDes);
		}
	}
	
	/**
	 * 企业付款到银行卡查询
	 * @Title: queryBank
	 * @Description: TODO
	 * @param @param presentRecord
	 * @param @return
	 * @return Map<String,Object>
	 * @throws
	 */
	private Map<String, Object> queryBank(PresentRecord presentRecord){
		Map<String, String> paramsMap = new HashMap<>(4);
	    paramsMap.put("mch_id", WxPayConsts.MCH_ID);
	    paramsMap.put("partner_trade_no", presentRecord.getId());
	    paramsMap.put("nonce_str", UUIDGenerator.getUUID());
		//把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
		String paramsString = WxPayUtils.createLinkString(paramsMap);
		//MD5运算生成签名
		String sign = WxPayUtils.sign(paramsString, WxPayConsts.KEY, "utf-8").toUpperCase();
		//签名
		paramsMap.put("sign", sign);
		String paramsXml = WxPayUtils.map2Xmlstring(paramsMap);
		String result = null ;
		try {
			result = ClientCustomSSL.doRefund(WxPayConsts.QUERY_BANK_URL, paramsXml);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return WxPayUtils.Str2Map(result);
	}
	
	/**
	 * 企业支付到银行卡
	 * @Title: payBank
	 * @Description: TODO
	 * @param @param presentRecord
	 * @param @return
	 * @return JSONObject
	 * @throws
	 */
	private JSONObject payBank(PresentRecord presentRecord) {
		Map<String, Object> resultMap = queryBank(presentRecord);
		String returnCode = resultMap.get("return_code").toString();
		if("FAIL".equals(returnCode)) {
			log.error(resultMap.toString());
			return ResultUtils.error(ErrorConsts.CODE_10004, resultMap.get("return_msg").toString());
		}
		String resultCode = resultMap.get("result_code").toString();
		if("SUCCESS".equals(resultCode)) {
			String status = resultMap.get("status").toString();
			if("PROCESSING".equals(status)) {
				return ResultUtils.error(ErrorConsts.CODE_10003, "微信处理中,T+1天后再来处理");
			}
			if("SUCCESS".equals(status)) {
				int i = presentService.updateSucceed(presentRecord.getId());
				if(i < 1) {
					return ResultUtils.error(ErrorConsts.CODE_10004, "同意失败");
				}
			}
		}else {
			String errCode = resultMap.get("err_code").toString();
			if(!"ORDERNOTEXIST".equals(errCode)) {
				log.error(resultMap.toString());
				return ResultUtils.error(ErrorConsts.CODE_10004, resultMap.get("err_code_des").toString());
			}
			//申请微信支付到银行卡
			BigDecimal amount = presentRecord.getAmountCash();
			amount = amount.multiply(new BigDecimal("100"));
			//订单总金额
			Integer money = amount.intValue();
		    Map<String, String> paramsMap = new HashMap<>(9);
		    paramsMap.put("mch_id", WxPayConsts.MCH_ID);
		    paramsMap.put("partner_trade_no", presentRecord.getId());
		    paramsMap.put("nonce_str", UUIDGenerator.getUUID());
		    try {
				paramsMap.put("enc_bank_no", GetRSA.getRSA(presentRecord.getCardNumber(), WxPayConsts.publicKeyPKCS8));
				paramsMap.put("enc_true_name", GetRSA.getRSA(presentRecord.getUserName(), WxPayConsts.publicKeyPKCS8));
			} catch (Exception e) {
				e.printStackTrace();
			}
		    paramsMap.put("bank_code", presentRecord.getBankName());
		    paramsMap.put("amount", money.toString());
		    paramsMap.put("desc", "用户提现");
			//把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
			String paramsString = WxPayUtils.createLinkString(paramsMap);
			//MD5运算生成签名
			String sign = WxPayUtils.sign(paramsString, WxPayConsts.KEY, "utf-8").toUpperCase();
			//签名
			paramsMap.put("sign", sign);
			String paramsXml = WxPayUtils.map2Xmlstring(paramsMap);
			String result = null;
			try {
				result = ClientCustomSSL.doRefund(WxPayConsts.PAY_BANK_URL, paramsXml);
			} catch (Exception e) {
				e.printStackTrace();
			}
			resultMap = WxPayUtils.Str2Map(result);
			returnCode = resultMap.get("return_code").toString();
			if("FAIL".equals(returnCode)) {
				log.error(resultMap.toString());
				return ResultUtils.error(ErrorConsts.CODE_10004, resultMap.get("return_msg").toString());
			}
			resultCode = resultMap.get("result_code").toString();
			if("FAIL".equals(resultCode)) {
				log.error(resultMap.toString());
				return ResultUtils.error(ErrorConsts.CODE_10004, resultMap.get("err_code_des").toString());
			}
			return ResultUtils.success("微信受理成功,T+1天后再来处理");
		}
		return ResultUtils.success("同意成功");
	}
}