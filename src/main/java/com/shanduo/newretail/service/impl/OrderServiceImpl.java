package com.shanduo.newretail.service.impl;

import static com.shanduo.newretail.wx.WX_TemplateMsgUtil.packJsonmsg;
import static com.shanduo.newretail.wx.WX_TemplateMsgUtil.getWXTemplateMsgId;

import java.math.BigDecimal;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.newretail.consts.WxPayConsts;
import com.shanduo.newretail.entity.AccessToken;
import com.shanduo.newretail.entity.Commodity;
import com.shanduo.newretail.entity.ToOrder;
import com.shanduo.newretail.entity.ToOrderDetails;
import com.shanduo.newretail.entity.ToUser;
import com.shanduo.newretail.entity.service.CommodityInfos;
import com.shanduo.newretail.entity.service.OrderInfo;
import com.shanduo.newretail.mapper.CommodityMapper;
import com.shanduo.newretail.mapper.ToOrderDetailsMapper;
import com.shanduo.newretail.mapper.ToOrderMapper;
import com.shanduo.newretail.service.AccessTokenService;
import com.shanduo.newretail.service.CommodityService;
import com.shanduo.newretail.service.OrderService;
import com.shanduo.newretail.service.SellerService;
import com.shanduo.newretail.service.UserService;
import com.shanduo.newretail.util.IOSXGHighUtils;
import com.shanduo.newretail.util.OrderIdUtils;
import com.shanduo.newretail.util.Page;
import com.shanduo.newretail.util.UUIDGenerator;
import com.shanduo.newretail.util.XGHighUtils;
import com.shanduo.newretail.wx.TemplateData;
import com.shanduo.newretail.wx.WX_TemplateMsgUtil;
import com.shanduo.newretail.wx.WX_UserUtil;
import com.shanduo.newretail.wx.WeiXinEnum;

/**
 * 
 * @ClassName: OrderServiceImpl
 * @Description: TODO
 * @author fanshixin
 * @date 2018年7月13日 上午10:31:10
 *
 */
@Service
public class OrderServiceImpl implements OrderService {

	private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

	@Autowired
	private ToOrderMapper orderMapper;
	@Autowired
	private ToOrderDetailsMapper orderDetailsMapper;
	@Autowired
	private CommodityMapper commodityMapper;
	@Autowired
	private SellerService sellerService;
	@Autowired
	private CommodityService commodityService;
	@Autowired
	private AccessTokenService accessTokenService;
	@Autowired
	private UserService userService;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public String saveOrder(Map<String, Object> parameterMap) {
		String orderId = OrderIdUtils.getId();
		String sellerId = parameterMap.get("sellerId").toString();
		BigDecimal totalPrice = new BigDecimal("0");
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> parameterList = (List<Map<String, Object>>) parameterMap.get("list");
		for (Map<String, Object> map : parameterList) {
			String commodityId = (String) map.get("commodityId");
			int number = (int) map.get("number");
			//检查商品库存是否删除是否下架是否足够再减库存
			if(!commodityService.selectCommodityStock(sellerId, commodityId, number)) {
				log.warn("库存不足");
				throw new RuntimeException();
			}
			commodityService.updateCommodityStock(sellerId, commodityId, number, "0");
			Commodity commodity = commodityMapper.selectByPrimaryKey(commodityId);
			ToOrderDetails orderDetails = new ToOrderDetails();
			orderDetails.setId(UUIDGenerator.getUUID());
			orderDetails.setOrderId(orderId);
			orderDetails.setCommodityId(commodityId);
			orderDetails.setCommodityName(commodity.getName());
			orderDetails.setCommodityNumber(number);
			orderDetails.setPrice(commodity.getPrice());
			int i = orderDetailsMapper.insertSelective(orderDetails);
			if(i < 1) {
				log.warn("订单详情录入失败");
				throw new RuntimeException();
			}
			totalPrice = totalPrice.add(orderDetails.getPrice().multiply(new BigDecimal(number+"")));
		}
		ToOrder order = new ToOrder();
		order.setId(orderId);
		order.setOpenid(parameterMap.get("openId").toString());
		order.setSellerId(sellerId);
		order.setTotalPrice(totalPrice);
		order.setUserName(parameterMap.get("name").toString());
		order.setUserPhone(parameterMap.get("phone").toString());
		order.setUserAddress(parameterMap.get("address").toString());
		try {
			order.setRemarks(parameterMap.get("remarks").toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		int i = orderMapper.insertSelective(order);
		if(i < 1) {
			log.warn("订单录入失败");
			throw new RuntimeException();
		}
		return orderId;
	}

	@Override
	public ToOrder getOrder(String orderId,String typeId) {
		return orderMapper.getOrder(orderId, typeId);
	}

	@Override
	public List<ToOrderDetails> listOrderId(String orderId) {
		return orderDetailsMapper.listOrderId(orderId);
	}
	
	@Override
	public int updatePayOrder(String orderId) {
		ToOrder order = new ToOrder();
		order.setId(orderId);
		order.setState("2");
		order.setPaymentTime(new Date());
		int i = orderMapper.updateByPrimaryKeySelective(order);
		if(i > 0) {
			AccessToken token = accessTokenService.selectAccessToken(WxPayConsts.APPID);
			//用户是否订阅该公众号标识 (0代表此用户没有关注该公众号 1表示关注了该公众号)
			order = getOrder(orderId, "2");
			String accessToken = token.getAccessToken();
			String openId = order.getOpenid();
		    Integer state = WX_UserUtil.subscribeState(accessToken, openId);
		    //获取模板Id
		    String regTempId = getWXTemplateMsgId(accessToken, WeiXinEnum.WX_TEMPLATE_MSG_NUMBER.ORDER_PAYED.getMsgNumber());
		    //向买家推送支付成功通知
		    if(state == 1) {
		    	Map<String,TemplateData> param = new HashMap<>(4);
	            param.put("first",new TemplateData("尊敬的用户:你已支付成功","#4395ff"));
	            param.put("keyword1",new TemplateData(order.getTotalPrice().toString(),"#4395ff"));
	            param.put("keyword2",new TemplateData(order.getId(),"#4395ff"));
	            param.put("remark",new TemplateData("小闪温馨提醒您:如下单超过15分钟没有及时处理,请拨打店铺客服电话沟通","#4395ff"));
	            WX_TemplateMsgUtil.sendWechatMsgToUser(accessToken, openId,regTempId, "", "#000000", packJsonmsg(param));
		    }
		    ToUser user = userService.selectUser(order.getSellerId());
		    openId = user.getOpenId();
		    if(openId != null) {
		    	state = WX_UserUtil.subscribeState(accessToken, openId);
		    	//向卖家推送下单通知
		    	if(state == 1) {
		    		Map<String,TemplateData> params = new HashMap<>(4);
		    		params.put("first",new TemplateData("你有新的待处理订单","#4395ff"));
		    		params.put("keyword1",new TemplateData(order.getTotalPrice().toString(),"#4395ff"));
		    		params.put("keyword2",new TemplateData(order.getId(),"#4395ff"));
		    		params.put("remark",new TemplateData("请尽快处理","#4395ff"));
		    		WX_TemplateMsgUtil.sendWechatMsgToUser(accessToken, openId,regTempId, "https://yapinkeji.com/shanduonewretail/orderList.html", "#000000", packJsonmsg(params));
		    	}
		    }
		    //ios推送
		    IOSXGHighUtils.getInstance().pushSingleAccount(order.getSellerId());
		    //安卓推送
		    XGHighUtils.getInstance().pushSingleAccount(order.getSellerId());
		}
		return i;
	}

	@Override
	public int updateReceivingOrder(String orderId,String sellerId) {
		int i = orderMapper.updateOrder(orderId, sellerId, "3");
		//推送接单
		if(i > 0) {
			AccessToken token = accessTokenService.selectAccessToken(WxPayConsts.APPID);
			ToOrder order = getOrder(orderId, "3");
			String accessToken = token.getAccessToken();
			String openId = order.getOpenid();
		    Integer state = WX_UserUtil.subscribeState(accessToken, openId);
		    if(state == 1) {
		    	//获取模板Id
			    String regTempId = getWXTemplateMsgId(accessToken, WeiXinEnum.WX_TEMPLATE_MSG_NUMBER.ORDER_SUCCESS.getMsgNumber());
			    Map<String,TemplateData> param = new HashMap<>(4);
	            param.put("first",new TemplateData("尊敬的【"+order.getUserName()+"】","#4395ff"));
	            param.put("keyword1",new TemplateData(order.getId(),"#4395ff"));
	            Format format = new SimpleDateFormat("MM月dd日 HH:mm:ss");
	    		String date = format.format(new Date());
	            param.put("keyword2",new TemplateData(date,"#4395ff"));
	            param.put("remark",new TemplateData("商家已接单","#4395ff"));
	            WX_TemplateMsgUtil.sendWechatMsgToUser(accessToken, openId,regTempId, "", "#000000", packJsonmsg(param));
		    }
		}
		return i;
	}

	@Override
	public int updateCancelOrder(String orderId,String sellerId) {
		return orderMapper.updateOrder(orderId, sellerId, "5");
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateCancelOrder(String orderId) {
		ToOrder order = getOrder(orderId, "5");
		int i = orderMapper.updateOrder(orderId, order.getSellerId(), "6");
		if(i < 1) {
			log.warn("订单修改退款状态失败");
			throw new RuntimeException();
		}
		List<ToOrderDetails> list = listOrderId(orderId);
		for (ToOrderDetails orderDetails : list) {
			commodityService.updateCommodityStock(order.getSellerId(), orderDetails.getCommodityId(), orderDetails.getCommodityNumber(), "1");
		}
		//向买家推送退款通知
		AccessToken token = accessTokenService.selectAccessToken(WxPayConsts.APPID);
		String accessToken = token.getAccessToken();
		String openId = order.getOpenid();
	    Integer state = WX_UserUtil.subscribeState(accessToken, openId);
	    if(state == 1) {
	    	//获取模板Id
		    String regTempId = getWXTemplateMsgId(accessToken, WeiXinEnum.WX_TEMPLATE_MSG_NUMBER.ORDER_ERROR_SUCCESS.getMsgNumber());
		    Map<String,TemplateData> param = new HashMap<>(7);
            param.put("first",new TemplateData("你的订单已退款","#4395ff"));
            param.put("keyword1",new TemplateData(order.getTotalPrice().toString(),"#4395ff"));
            param.put("keyword2",new TemplateData("店家退款","#4395ff"));
            Format format = new SimpleDateFormat("MM月dd日 HH:mm:ss");
    		String date = format.format(new Date());
            param.put("keyword3",new TemplateData(date,"#4395ff"));
            param.put("keyword4",new TemplateData("微信钱包","#4395ff"));
            param.put("keyword5",new TemplateData("已退款","#4395ff"));
            param.put("remark",new TemplateData("对你造成的不便敬请谅解","#4395ff"));
            WX_TemplateMsgUtil.sendWechatMsgToUser(accessToken, openId,regTempId, "", "#000000", packJsonmsg(param));
	    }
		return 1;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateFinishOrder(String orderId, String sellerId) {
		ToOrder order = getOrder(orderId, "3");
		int i = orderMapper.updateOrder(orderId, sellerId,"4");
		if(i < 1) {
			log.warn("完成订单失败");
			throw new RuntimeException();
		}
		BigDecimal totalPrice = order.getTotalPrice().multiply(new BigDecimal("0.94"));
		//店铺加钱
		sellerService.updateMoney(totalPrice, sellerId, "0");
		//向买家推送订单完成通知?
		return 1;
	}

	@Override
	public Map<String, Object> listSellerOrder(String sellerId, String state, String startDate, String endDate, Integer pageNum,
			Integer pageSize) {
		int totalRecord = orderMapper.countSellerOrder(sellerId, state);
		Page page = new Page(totalRecord, pageSize, pageNum);
		pageNum = (page.getPageNum() - 1) * page.getPageSize();
		List<OrderInfo> listOrderInfo = orderMapper.listSellerOrder(sellerId, state, startDate, endDate, pageNum, page.getPageSize());
		for (OrderInfo order : listOrderInfo) {
			List<ToOrderDetails> details = listOrderId(order.getId());
			order.setDetails(details);
		}
		Map<String, Object> resultMap = new HashMap<String, Object>(3);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalPage", page.getTotalPage());
		resultMap.put("data", listOrderInfo);
		return resultMap;
	}

	@Override
	public Map<String, Object> listSellerCommodity(String sellerId, String categoryId, String startDate, String endDate,
			Integer pageNum, Integer pageSize) {
		int totalRecord = orderDetailsMapper.countSellerCommodity(sellerId, categoryId, startDate, endDate);
		Page page = new Page(totalRecord, pageSize, pageNum);
		pageNum = (page.getPageNum() - 1) * page.getPageSize();
		List<CommodityInfos> listCommodityInfos = orderDetailsMapper.listSellerCommodity(sellerId, categoryId ,startDate, 
				endDate, pageNum, page.getPageSize());
		Map<String, Object> resultMap = new HashMap<String, Object>(3);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalPage", page.getTotalPage());
		resultMap.put("data", listCommodityInfos);
		return resultMap;
	}

	@Override
	public Double sumSellerMoney(String sellerId, String categoryId, String startDate, String endDate) {
		return orderDetailsMapper.sumSellerMoney(sellerId, categoryId, startDate, endDate);
	}

	@Override
	public List<String> listPending() {
		return orderMapper.listPending();
	}

}
