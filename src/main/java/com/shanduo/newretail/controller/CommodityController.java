package com.shanduo.newretail.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shanduo.newretail.consts.ErrorConsts;
import com.shanduo.newretail.consts.WxPayConsts;
import com.shanduo.newretail.entity.common.ErrorBean;
import com.shanduo.newretail.entity.common.ResultBean;
import com.shanduo.newretail.entity.common.SuccessBean;
import com.shanduo.newretail.entity.service.CommodityInfo;
import com.shanduo.newretail.service.AccessTokenService;
import com.shanduo.newretail.service.BaseService;
import com.shanduo.newretail.service.CommodityService;
import com.shanduo.newretail.service.UserService;
import com.shanduo.newretail.util.StringUtils;
import com.shanduo.newretail.util.WxFileUtils;
import net.sf.json.JSONArray;

@Controller
@RequestMapping(value = "jcommodity")
public class CommodityController {
	private static final Logger Log = LoggerFactory.getLogger(CommodityController.class);
	@Autowired
	private CommodityService commodityService;
	@Autowired
	private BaseService baseService;
	@Autowired
	private AccessTokenService accessTokenService;
	@Autowired
	private UserService UserService;
	
	/**
	 * 查询店铺商品类别
	 * @param request
	 * @param id
	 * @param typeId
	 * @param token
	 * @return
	 */
	@RequestMapping(value = "selectcommoditytype",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	//http://localhost:8081/shanduonewretail/jcommodity/selectcommoditytype?id=1&typeId=1
	public ResultBean selectCommodityType(HttpServletRequest request, String id,String typeId) {
		if(StringUtils.isNull(typeId) ) {
			Log.warn("typeId错误");
			return new ErrorBean(ErrorConsts.CODE_10002,"参数为空");
		}
		if("1".equals(typeId)){
			if(StringUtils.isNull(id) ) {
				Log.warn("id错误");
				return new ErrorBean(ErrorConsts.CODE_10002,"参数为空");
			}
		}
		if("2".equals(typeId)){
			if(StringUtils.isNull(id)) {
				Log.warn("token为空");
				return new ErrorBean(ErrorConsts.CODE_10001,"token为空");
			}
			id = baseService.checkUserToken(id);
			if(null==id){
				Log.warn("token失效");
				return new ErrorBean(ErrorConsts.CODE_10001,"token失效");
			}
		}
		List<Map<String,Object>> categoryIdList = new ArrayList<Map<String,Object>>();
		try {
			categoryIdList = commodityService.selectSellerCommodityType(id,typeId);
			if(categoryIdList.isEmpty()){
				return new ErrorBean();
			}
	} catch (Exception e) {
		return new ErrorBean(ErrorConsts.CODE_10004,"查询失败");
	}
		return new SuccessBean(categoryIdList);
		
	}
	/**
	 * 查询店铺所有商品
	 * @param request
	 * @param id
	 * @param categoryId
	 * @param page
	 * @param pageSize
	 * @param typeId
	 * @param token
	 * @return
	 */
	@RequestMapping(value = "selectcommodity",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	//http://localhost:8081/shanduonewretail/jcommodity/selectcommodity?id=1&categoryId=
	public ResultBean selectCommodity(HttpServletRequest request, String id,String categoryId, String page, String pageSize,String typeId,String token) {
		
		if(StringUtils.isNull(typeId) ) {
			Log.warn("typeId错误");
			return new ErrorBean(ErrorConsts.CODE_10002,"参数为空");
		}
		if("1".equals(typeId)){
			if(StringUtils.isNull(id) ) {
				Log.warn("id错误");
				return new ErrorBean(ErrorConsts.CODE_10002,"参数为空");
			}
		}
		if("2".equals(typeId)){
			if(StringUtils.isNull(id)) {
				Log.warn("token为空");
				return new ErrorBean(ErrorConsts.CODE_10001,"token为空");
			}
			id = baseService.checkUserToken(id);
			if(null==id){
				Log.warn("token失效");
				return new ErrorBean(ErrorConsts.CODE_10001,"token失效");
			}
		}
		if(null==categoryId){
			Log.warn("categoryId错误");
			return new ErrorBean(ErrorConsts.CODE_10002,"参数为空");
		}
		if(StringUtils.isNull(page) || !page.matches("^\\d*$")) {
			Log.warn("page is error waith page:{}", page);
			return new ErrorBean(ErrorConsts.CODE_10002, "页码错误");
		}
		if(StringUtils.isNull(pageSize) || !pageSize.matches("^\\d*$")) {
			Log.warn("pageSize is error waith pageSize:{}", pageSize);
			return new ErrorBean(ErrorConsts.CODE_10002, "记录错误");
		}
		try {
			Map<String, Object> resultMap = new HashMap<String, Object>(3);
			resultMap = commodityService.selectCommodity(Integer.valueOf(categoryId), id,Integer.valueOf(page),Integer.valueOf(pageSize),typeId);
			return new SuccessBean(resultMap);
		} catch (Exception e) {
			return new ErrorBean(ErrorConsts.CODE_10004,"查询失败");
		}
	}
	
	/**
	 * 查询仓库所有商品
	 * @param request
	 * @param id
	 * @param categoryId
	 * @param page
	 * @param pageSize
	 * @param typeId
	 * @param token
	 * @return
	 */
	@RequestMapping(value = "selectwarehousecommodity",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	//http://localhost:8081/shanduonewretail/jcommodity/selectwarehousecommodity?categoryId=
	public ResultBean selectWarehouseCommodity(HttpServletRequest request, String categoryId, String page, String pageSize,String token) {
		
		if(StringUtils.isNull(token)) {
			Log.warn("token为空");
			return new ErrorBean(ErrorConsts.CODE_10001,"token为空");
		}
		String userId = baseService.checkUserToken(token);
		if(null==userId){
			Log.warn("token失效");
			return new ErrorBean(ErrorConsts.CODE_10001,"token失效");
		}
		String id = UserService.selectAdministratorsId();
		if(null==id){
			Log.warn("token失效");
			return new ErrorBean(ErrorConsts.CODE_10001,"token失效");
		}
		
		if(null==categoryId){
			Log.warn("categoryId错误");
			return new ErrorBean(ErrorConsts.CODE_10002,"参数为空");
		}
		if(StringUtils.isNull(page) || !page.matches("^\\d*$")) {
			Log.warn("page is error waith page:{}", page);
			return new ErrorBean(ErrorConsts.CODE_10002, "页码错误");
		}
		if(StringUtils.isNull(pageSize) || !pageSize.matches("^\\d*$")) {
			Log.warn("pageSize is error waith pageSize:{}", pageSize);
			return new ErrorBean(ErrorConsts.CODE_10002, "记录错误");
		}
		try {
			Map<String, Object> resultMap = new HashMap<String, Object>(3);
			resultMap = commodityService.selectWarehouseCommodity(Integer.valueOf(categoryId), id,Integer.valueOf(page),Integer.valueOf(pageSize),userId);
			return new SuccessBean(resultMap);
		} catch (Exception e) {
			return new ErrorBean(ErrorConsts.CODE_10004,"查询失败");
		}
	}
	/**
	 * 商品上下架
	 * @param request
	 * @param token
	 * @param visible
	 * @param commodityId
	 * @return
	 */
	@RequestMapping(value = "updatecommodityvisible",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	//http://localhost:8081/shanduonewretail/jcommodity/updatecommodityvisible?token=1&visible=1&commodityId=1
	public ResultBean updateCommodityVisible(HttpServletRequest request, String token,String visible,String commodityId) {
		if(StringUtils.isNull(commodityId) ) {
			Log.warn("commodityId为空");
			return new ErrorBean(ErrorConsts.CODE_10002,"commodityId为空");
		}
		if(StringUtils.isNull(visible) || !visible.matches("^[01]$")) {
			Log.error("visible错误");
			return new ErrorBean(ErrorConsts.CODE_10002,"visible错误");
		}
		if(StringUtils.isNull(token)) {
			Log.warn("token为空");
			return new ErrorBean(ErrorConsts.CODE_10001,"token为空");
		}
		String id = baseService.checkUserToken(token);
		if(null==id){
			Log.warn("token失效");
			return new ErrorBean(ErrorConsts.CODE_10001,"token失效");
		}
		try {
			int count = commodityService.updateCommodityVisible(commodityId, id, Integer.valueOf(visible));
			if(count<1){
				return new ErrorBean(ErrorConsts.CODE_10004,"修改失败");
			}
		} catch (Exception e) {
			return new ErrorBean(ErrorConsts.CODE_10004,"修改失败");
		}
		return new SuccessBean("修改成功");
		
	}
	/**
	 * 商品上传
	 * @param request
	 * @param token
	 * @param name
	 * @param picture
	 * @param price
	 * @param stock
	 * @param categoryId
	 * @return
	 */
	
	@RequestMapping(value = "insertcommodity",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	//http://localhost:8081/shanduonewretail/jcommodity/insertcommodity?token=1&name=adsds&picture=11111&price=2.3&stock=1&categoryId=100
	public ResultBean insertCommodity(HttpServletRequest request, String token,String name,String picture,String price,String stock,String categoryId) {
		if(StringUtils.isNull(token)) {
			Log.warn("token为空");
			return new ErrorBean(ErrorConsts.CODE_10001,"token为空");
		}
		if(StringUtils.isNull(name) ) {
			Log.warn("name为空");
			return new ErrorBean(ErrorConsts.CODE_10002,"name为空");
		}
		if(StringUtils.isNull(picture)) {
			Log.warn("picture为空");
			return new ErrorBean(ErrorConsts.CODE_10002,"picture为空");
		}
		if(StringUtils.isNull(price)) {
			Log.warn("price为空");
			return new ErrorBean(ErrorConsts.CODE_10002,"price为空");
		}
		if(StringUtils.isNull(stock) || !stock.matches("^[1-9]\\d*$")) {
			Log.warn("stock不合法");
			return new ErrorBean(ErrorConsts.CODE_10002,"stock不合法");
		}
		if(StringUtils.isNull(categoryId)) {
			Log.warn("categoryId为空");
			return new ErrorBean(ErrorConsts.CODE_10002,"categoryId为空");
		}
		String userId = baseService.checkUserToken(token);
		if(null==userId){
			Log.warn("token失效");
			return new ErrorBean(ErrorConsts.CODE_10001,"token失效");
		}
		try {
			if (picture.lastIndexOf(".") == -1) {
				picture = WxFileUtils.downloadImage(accessTokenService.selectAccessToken(WxPayConsts.APPID).getAccessToken(),picture);
	        }
			int count = commodityService.insertCommodity(name, picture, price, stock, categoryId,userId);
			if(count<1){
				return new ErrorBean(ErrorConsts.CODE_10004,"上传失败");
			}
		} catch (Exception e) {
			return new ErrorBean(ErrorConsts.CODE_10004,"上传失败");
		}
		return new SuccessBean("上传成功");	
	}
	/**
	 * 在仓库选择商品
	 * @param request
	 * @param token
	 * @return
	 */
	@RequestMapping(value = "insertwarehousecommodity",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	//http://localhost:8081/shanduonewretail/jcommodity/insertwarehousecommodity?token=1&
	public ResultBean insertWarehouseCommodity(HttpServletRequest request, String token) {
		if(StringUtils.isNull(token)) {
			Log.warn("token为空");
			return new ErrorBean(ErrorConsts.CODE_10001,"token为空");
		}
		String userId = baseService.checkUserToken(token);
		if(null==userId){
			Log.warn("token失效");
			return new ErrorBean(ErrorConsts.CODE_10001,"token失效");
		}
		String commodityId = request.getParameter("commodityIdList");
		if(StringUtils.isNull(commodityId)) {
			Log.warn("用户信息为空");
			return new ErrorBean(ErrorConsts.CODE_10002,"用户信息为空");
		}
		JSONArray jsonArray = JSONArray.fromObject(commodityId);
		@SuppressWarnings("unchecked")
		List<String> commodityIdList = (List<String>) JSONArray.toCollection(jsonArray, String.class);
		try {
			int count = commodityService.insertWarehouseCommodity(commodityIdList,userId);
			if(count<1){
				return new ErrorBean(ErrorConsts.CODE_10004,"上传失败");
			}
		} catch (Exception e) {
			return new ErrorBean(ErrorConsts.CODE_10004,"上传失败");
		}
		return new SuccessBean("上传成功");	
	}
	/**
	 * 修改商品
	 * @param request
	 * @param token
	 * @param name
	 * @param picture
	 * @param price
	 * @param stock
	 * @param categoryId
	 * @param commodityId
	 * @return
	 */
	@RequestMapping(value = "updatecommodity",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	//http://localhost:8081/shanduonewretail/jcommodity/updatecommodity?token=1&name=adsds&picture=11111&price=2.3&stock=1&categoryId=100&commodityId
	public ResultBean updateCommodity(HttpServletRequest request, String token,String name,String picture,String price,String stock,String categoryId,
			String commodityId) {
		if(StringUtils.isNull(token)) {
			Log.warn("token为空");
			return new ErrorBean(ErrorConsts.CODE_10001,"token为空");
		}
		if(StringUtils.isNull(name) ) {
			Log.warn("name为空");
			return new ErrorBean(ErrorConsts.CODE_10002,"name为空");
		}
		if(StringUtils.isNull(picture)) {
			Log.warn("picture为空");
			return new ErrorBean(ErrorConsts.CODE_10002,"picture为空");
		}
		if(StringUtils.isNull(price)) {
			Log.warn("price为空");
			return new ErrorBean(ErrorConsts.CODE_10002,"price为空");
		}
		if(StringUtils.isNull(commodityId)) {
			Log.warn("commodityId为空");
			return new ErrorBean(ErrorConsts.CODE_10002,"commodityId为空");
		}
		if(StringUtils.isNull(stock) || !stock.matches("^[1-9]\\d*$")) {
			Log.warn("stock不合法");
			return new ErrorBean(ErrorConsts.CODE_10002,"stock不合法");
		}
		if(StringUtils.isNull(categoryId)) {
			Log.warn("categoryId为空");
			return new ErrorBean(ErrorConsts.CODE_10002,"categoryId为空");
		}
		String userId = baseService.checkUserToken(token);
		if(null==userId){
			Log.warn("token失效");
			return new ErrorBean(ErrorConsts.CODE_10001,"token失效");
		}
		try {
			if(!picture.contains(".jpg")){
				picture = WxFileUtils.downloadImage(accessTokenService.selectAccessToken(WxPayConsts.APPID).getAccessToken(),picture);
			}
			commodityService.updateCommodity(name, picture, price, stock, categoryId,userId,commodityId);
		} catch (Exception e) {
			return new ErrorBean(ErrorConsts.CODE_10004,"修改失败");
		}
		return new SuccessBean("修改成功");	
	}
	/**
	 * 商品删除
	 * @param request
	 * @param token
	 * @param commodityId
	 * @return
	 */
	@RequestMapping(value = "deletecommodity",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	//http://localhost:8081/shanduonewretail/jcommodity/deletecommodity?token=1&commodityId
	public ResultBean deleteCommodity(HttpServletRequest request, String token,String commodityId) {
		if(StringUtils.isNull(token)) {
			Log.warn("token为空");
			return new ErrorBean(ErrorConsts.CODE_10001,"token为空");
		}
		if(StringUtils.isNull(commodityId)) {
			Log.warn("commodityId为空");
			return new ErrorBean(ErrorConsts.CODE_10002,"commodityId为空");
		}
		String userId = baseService.checkUserToken(token);
		if(null==userId){
			Log.warn("token失效");
			return new ErrorBean(ErrorConsts.CODE_10001,"token失效");
		}
		try {
			int count = commodityService.deleteCommodity(userId, commodityId);
			if(count<1){
				return new ErrorBean(ErrorConsts.CODE_10004,"删除失败");
			}
		} catch (Exception e) {
			return new ErrorBean(ErrorConsts.CODE_10004,"删除失败");
		}
		return new SuccessBean("删除成功");	
	}
	
	/**
	 * 查询所有商品类别
	 * @param request
	 * @param token
	 * @return
	 */
	@RequestMapping(value = "selectcommodityalltype",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	//http://localhost:8081/shanduonewretail/jcommodity/selectcommodityalltype?token=1
	public ResultBean selectCommodityAllType(HttpServletRequest request, String token) {
		if(StringUtils.isNull(token)) {
			Log.warn("token为空");
			return new ErrorBean(ErrorConsts.CODE_10001,"token为空");
		}
		String id = baseService.checkUserToken(token);
		if(null==id){
			Log.warn("token失效");
			return new ErrorBean(ErrorConsts.CODE_10001,"token失效");
		}
		List<Map<String,Object>> categoryIdList = new ArrayList<Map<String,Object>>();
		try {
			categoryIdList = commodityService.selectCommodityType(id);
			if(categoryIdList.isEmpty()){
				return new ErrorBean();
			}
	} catch (Exception e) {
		return new ErrorBean(ErrorConsts.CODE_10004,"查询失败");
	}
		return new SuccessBean(categoryIdList);
	}
	/**
	 * 查询单个商品详情
	 * @param request
	 * @param token
	 * @param commodityId
	 * @return
	 */
	@RequestMapping(value = "selectonecommodity",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	//http://localhost:8081/shanduonewretail/jcommodity/selectonecommodity?token=1&commodityId=
	public ResultBean selectOneCommodity(HttpServletRequest request, String token,String commodityId) {
		if(StringUtils.isNull(commodityId)) {
			Log.warn("commodityId为空");
			return new ErrorBean(ErrorConsts.CODE_10002,"commodityId为空");
		}
		if(StringUtils.isNull(token)) {
			Log.warn("token为空");
			return new ErrorBean(ErrorConsts.CODE_10002,"token为空");
		}
		String id = baseService.checkUserToken(token);
		if(null==id){
			Log.warn("token失效");
			return new ErrorBean(ErrorConsts.CODE_10001,"token失效");
		}
		CommodityInfo  commodityInfo = new CommodityInfo ();
		try {
			commodityInfo = commodityService.selectOneCommodity(id, commodityId);
			if(commodityInfo==null){
				return new ErrorBean();
			}
	} catch (Exception e) {
		return new ErrorBean(ErrorConsts.CODE_10004,"查询失败");
	}
		return new SuccessBean(commodityInfo);
	}

}
