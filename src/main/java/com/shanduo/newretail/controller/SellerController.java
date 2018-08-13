package com.shanduo.newretail.controller;

import com.alibaba.fastjson.JSONObject;
import com.shanduo.newretail.consts.ErrorConsts;
import com.shanduo.newretail.consts.WxPayConsts;
import com.shanduo.newretail.entity.common.ErrorBean;
import com.shanduo.newretail.entity.common.ResultBean;
import com.shanduo.newretail.entity.common.SuccessBean;
import com.shanduo.newretail.entity.service.SellerDetails;
import com.shanduo.newretail.entity.service.SellerInfo;
import com.shanduo.newretail.service.AccessTokenService;
import com.shanduo.newretail.service.BaseService;
import com.shanduo.newretail.service.OrderService;
import com.shanduo.newretail.service.SellerService;
import com.shanduo.newretail.service.UserService;
import com.shanduo.newretail.util.JsonStringUtils;
import com.shanduo.newretail.util.Page;
import com.shanduo.newretail.util.PatternUtils;
import com.shanduo.newretail.util.StringUtils;
import com.shanduo.newretail.util.WxFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "jseller")
public class SellerController {
    private static final Logger Log = LoggerFactory.getLogger(SellerController.class);
    @Autowired
    private SellerService sellerService;
    @Autowired
    private BaseService baseService;
    @Autowired
    private AccessTokenService accessTokenService;
    @Autowired
    private OrderController orderController;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;

    /**查询附近店铺类型
     * @param request
     * @param lat
     * @param lon
     * @return
     */
    @RequestMapping(value = "selectsellertype", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    //http://localhost:8081/shanduonewretail/jseller/selectsellertype?lon=113.074815&lat=28.227615
    public ResultBean selectSellerType(HttpServletRequest request, String lat, String lon) {
        if (StringUtils.isNull(lon) || PatternUtils.patternLatitude(lon)) {
            Log.warn("经度格式错误");
            return new ErrorBean(ErrorConsts.CODE_10002, "经度格式错误");
        }
        if (StringUtils.isNull(lat) || PatternUtils.patternLatitude(lat)) {
            Log.warn("纬度格式错误");
            return new ErrorBean(ErrorConsts.CODE_10002, "纬度格式错误");
        }
        List<Map<String, Object>> sellerTypeList = new ArrayList<Map<String, Object>>();
        try {
            sellerTypeList = sellerService.selectNearbySellerType(new Double(lon), new Double(lat));
            if (sellerTypeList.isEmpty()) {
                return new ErrorBean();
            }
        } catch (Exception e) {
            return new ErrorBean(ErrorConsts.CODE_10004, "查询失败");
        }

        return new SuccessBean(sellerTypeList);
    }

    /**查询附近对应类型的店铺
     * @param request
     * @param lat
     * @param lon
     * @param sellerType
     * @return
     */
    @RequestMapping(value = "selectseller", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    //http://localhost:8081/shanduonewretail/jseller/selectseller?lon=113.074815&lat=28.227615&sellerType=[0,1,2]
    public ResultBean selectseller(HttpServletRequest request, String lat, String lon, String sellerType) {
        if (StringUtils.isNull(lon) || PatternUtils.patternLatitude(lon)) {
            Log.warn("经度格式错误");
            return new ErrorBean(ErrorConsts.CODE_10002, "经度格式错误");
        }
        if (StringUtils.isNull(lat) || PatternUtils.patternLatitude(lat)) {
            Log.warn("纬度格式错误");
            return new ErrorBean(ErrorConsts.CODE_10002, "纬度格式错误");
        }
        if (StringUtils.isNull(sellerType)) {
            Log.warn("无店铺类别");
            return new ErrorBean(ErrorConsts.CODE_10002, "无店铺类别");
        }

        List<SellerInfo> sellerInfoMap = new ArrayList<SellerInfo>();
        try {
            sellerInfoMap = sellerService.selectNearbySellerOneType(new Double(lon), new Double(lat), sellerType);
            if (sellerInfoMap.isEmpty()) {
                return new ErrorBean();
            }
        } catch (Exception e) {
        	e.printStackTrace();
            return new ErrorBean(ErrorConsts.CODE_10004, "查询失败");
        }
        return new SuccessBean(sellerInfoMap);
    }
    /**查询起配送类型
     * @param request
     * @return
     */
    @RequestMapping(value = "selectdistributiontype", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    //http://localhost:8081/shanduonewretail/jseller/selectdistributiontype
    public ResultBean selectDistributionType(HttpServletRequest request) {
        List<Map<String, Object>> distributionTypeList = new ArrayList<Map<String, Object>>();
        try {
        	distributionTypeList = sellerService.selectDistributionType(); 
        } catch (Exception e) {
            return new ErrorBean(ErrorConsts.CODE_10004, "查询失败");
        }
        return new SuccessBean(distributionTypeList);
    }

    /**
     * 查询店铺详情
     *
     * @param request
     * @param token
     * @param id
     * @param typeId
     * @return
     */
    @RequestMapping(value = "selectsellerdetails", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    //http://localhost:8081/shanduonewretail/jseller/selectsellerdetails?token=1&id=&typeId=(0顾客1店家)
    public ResultBean selectSellerDetails(HttpServletRequest request, String token, String id, String typeId) {
        if (StringUtils.isNull(typeId)) {
            Log.warn("typeId为空");
            return new ErrorBean(ErrorConsts.CODE_10002, "typeId为空");
        }

        if ("1".equals(typeId)) {
            if (StringUtils.isNull(token)) {
                Log.warn("token为空");
                return new ErrorBean(ErrorConsts.CODE_10001, "token为空");
            }
            id = baseService.checkUserToken(token);
            if (null == id) {
                Log.warn("token失效");
                return new ErrorBean(ErrorConsts.CODE_10001, "token失效");
            }
        } else {
            if (StringUtils.isNull(id)) {
                Log.warn("id为空");
                return new ErrorBean(ErrorConsts.CODE_10002, "id为空");
            }
        }
        SellerDetails userSeller = new SellerDetails();
        try {
            userSeller = sellerService.selectSellerDetails(id);
        } catch (Exception e) {
            return new ErrorBean(ErrorConsts.CODE_10004, "查询失败");
        }
        return new SuccessBean(userSeller);
    }

    /**
     * 修改店铺详情
     *
     * @param request
     * @param token
     * @return
     */

    @RequestMapping(value = "updatesellerdetails", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    //http://localhost:8081/shanduonewretail/jseller/updatesellerdetails?token=1
    public ResultBean updateSellerDetails(HttpServletRequest request, String token) {
        if (StringUtils.isNull(token)) {
            Log.warn("token为空");
            return new ErrorBean(ErrorConsts.CODE_10001, "token为空");
        }
        String id = baseService.checkUserToken(token);
        if (null == id) {
            Log.warn("token失效");
            return new ErrorBean(ErrorConsts.CODE_10001, "token失效");
        }
        String userSeller = request.getParameter("userSeller");

        if (StringUtils.isNull(userSeller)) {
            Log.warn("用户信息为空");
            return new ErrorBean(ErrorConsts.CODE_10002, "用户信息为空");
        }
        Map<String, Object> userSellerMap = JsonStringUtils.getMap(userSeller);
        userSellerMap.put("id", id);
        if (!userSellerMap.get("sellerPicture").equals("null") && userSellerMap.get("sellerPicture").toString().lastIndexOf(".") == -1) {
            String picture = WxFileUtils.downloadImage(accessTokenService.selectAccessToken(WxPayConsts.APPID).getAccessToken(), userSellerMap.get("sellerPicture").toString());
            userSellerMap.put("sellerPicture", picture);
        }
        if (StringUtils.isNull(userSellerMap.get("phone") + "") || PatternUtils.patternPhone(userSellerMap.get("phone").toString())) {
            Log.warn("电话号码错误");
            return new ErrorBean(ErrorConsts.CODE_10002, "电话号码错误");
        }
        try {
            int count = sellerService.updateSellerDetails(userSellerMap);
            if (count < 1) {
                return new ErrorBean(ErrorConsts.CODE_10004, "修改失败");
            }
        } catch (Exception e) {
            return new ErrorBean(ErrorConsts.CODE_10004, "修改失败");
        }
        return new SuccessBean("修改成功");
    }

    /**
     * 开店关店
     *
     * @param request
     * @param token
     * @param businessSign
     * @return
     */
    @RequestMapping(value = "updatebusinesssign", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    //http://localhost:8081/shanduonewretail/jseller/updatebusinesssign?token=1&businessSign=1
    public ResultBean updateBusinessSign(HttpServletRequest request, String token, String businessSign) {
        if (StringUtils.isNull(token)) {
            Log.warn("token为空");
            return new ErrorBean(ErrorConsts.CODE_10001, "token为空");
        }
        String id = baseService.checkUserToken(token);
        if (null == id) {
            Log.warn("token失效");
            return new ErrorBean(ErrorConsts.CODE_10001, "token失效");
        }
        try {
            int count = sellerService.updateBusinessSign(businessSign, id);
            if (count < 1) {
                return new ErrorBean(ErrorConsts.CODE_10004, "修改失败");
            }
        } catch (Exception e) {
            return new ErrorBean(ErrorConsts.CODE_10004, "修改失败");
        }
        return new SuccessBean("修改成功");
    }

    /**
     * 查询店铺所有类型
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "selectselleralltype", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    //http://localhost:8081/shanduonewretail/jseller/selectselleralltype
    public ResultBean selectSellerAllType(HttpServletRequest request) {
        List<Map<String, Object>> sellerAllTypeList;
        try {
            sellerAllTypeList = sellerService.selectSellerType();
        } catch (Exception e) {
            return new ErrorBean(ErrorConsts.CODE_10004, "查询失败");
        }
        return new SuccessBean(sellerAllTypeList);
    }

  /**
   *  查询该业务员店铺数、业绩
   * @param request
   * @param token
   * @param startDate
   * @param endDate
   * @param page
   * @param pageSize
   * @return
   */
    @RequestMapping(value = "selectsalesmansubordinate", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    //http://localhost:8081/shanduonewretail/jseller/selectsalesmansubordinate?token=1
    public ResultBean selectSalesmanSubordinate(HttpServletRequest request, String token,String startDate, String endDate, String page, String pageSize) {
        if (StringUtils.isNull(token)) {
            Log.warn("token为空");
            return new ErrorBean(ErrorConsts.CODE_10001, "token为空");
        }
        if(StringUtils.isNull(page) || !page.matches("^\\d*$")) {
			Log.warn("page is error waith page:{}", page);
			return new ErrorBean(ErrorConsts.CODE_10002, "页码错误");
		}
		if(StringUtils.isNull(pageSize) || !pageSize.matches("^\\d*$")) {
			Log.warn("pageSize is error waith pageSize:{}", pageSize);
			return new ErrorBean(ErrorConsts.CODE_10002, "记录错误");
		}
        JSONObject json = orderController.isDate(startDate, endDate);
		if(json != null) {
			return new ErrorBean();
		}
        String id = baseService.checkUserToken(token);
        if (null == id) {
            Log.warn("token失效");
            return new ErrorBean(ErrorConsts.CODE_10001, "token失效");
        }
        List<Map<String, Object>> sellerList = new ArrayList<Map<String, Object>>();
        Map<String, Object> salesmanMap = new HashMap<>();
        try {
        	Integer sellerNum = sellerService.selectSubordinateCount(id);
    		salesmanMap.put("sellerNum", sellerNum);
    		Page pages = new Page(sellerNum, Integer.valueOf(pageSize), Integer.valueOf(page));
    		salesmanMap.put("totalPage", pages.getTotalPage());
        	if("1".equals(page)){
            	Double salesmanAchievement = sellerService.selectSalesmanAchievement(id, startDate, endDate);
            	salesmanMap.put("salesmanAchievement", salesmanAchievement);
        	}
            sellerList = sellerService.selectSalesmanSubordinate(id,Integer.valueOf(page),Integer.valueOf(pageSize));
            Map<String, Object> sellerMap = new HashMap<>();
            for(int i=0;i<sellerList.size();i++){
            	sellerMap = sellerList.get(i);
            	Double money=orderService.sumSellerMoney(sellerMap.get("id").toString(), null, startDate, endDate);
            	sellerMap.put("sellerMoney", money);
            }
            salesmanMap.put("sellerList", sellerList);
        } catch (Exception e) {
            return new ErrorBean(ErrorConsts.CODE_10004, "查询失败");
        }
        return new SuccessBean(salesmanMap);
    }
    
    /**
     *  查询该区域管理业务员数、业绩
     * @param request
     * @param token
     * @param startDate
     * @param endDate
     * @param page
     * @param pageSize
     * @return
     */
      @RequestMapping(value = "selectregionsubordinate", method = {RequestMethod.POST, RequestMethod.GET})
      @ResponseBody
      //http://localhost:8081/shanduonewretail/jseller/selectregionsubordinate?token=1
      public ResultBean selectRegionSubordinate(HttpServletRequest request, String token,String startDate, String endDate, String page, String pageSize) {
          if (StringUtils.isNull(token)) {
              Log.warn("token为空");
              return new ErrorBean(ErrorConsts.CODE_10001, "token为空");
          }
          if(StringUtils.isNull(page) || !page.matches("^\\d*$")) {
  			Log.warn("page is error waith page:{}", page);
  			return new ErrorBean(ErrorConsts.CODE_10002, "页码错误");
  		}
  		if(StringUtils.isNull(pageSize) || !pageSize.matches("^\\d*$")) {
  			Log.warn("pageSize is error waith pageSize:{}", pageSize);
  			return new ErrorBean(ErrorConsts.CODE_10002, "记录错误");
  		}
          JSONObject json = orderController.isDate(startDate, endDate);
  		if(json != null) {
  			return new ErrorBean();
  		}
          String id = baseService.checkUserToken(token);
          if (null == id) {
              Log.warn("token失效");
              return new ErrorBean(ErrorConsts.CODE_10001, "token失效");
          }
          Map<String, Object> regionMap = new HashMap<>();
          try {
          	regionMap = userService.listParent(id,Integer.valueOf(page),Integer.valueOf(pageSize), startDate, endDate,1);
          	if("1".equals(page)){
              	Double regionAchievement = sellerService.selectRegionAchievement(id, startDate, endDate);
              	regionMap.put("regionAchievement", regionAchievement);
          	}
          } catch (Exception e) {
              return new ErrorBean(ErrorConsts.CODE_10004, "查询失败");
          }
          return new SuccessBean(regionMap);
      }
      /**
       *  查询区域管理数、业绩
       * @param request
       * @param token
       * @param startDate
       * @param endDate
       * @param page
       * @param pageSize
       * @return
       */
        @RequestMapping(value = "selectmanagesubordinate", method = {RequestMethod.POST, RequestMethod.GET})
        @ResponseBody
        //http://localhost:8081/shanduonewretail/jseller/selectmanagesubordinate?token=1
        public ResultBean selectManageSubordinate(HttpServletRequest request, String token,String startDate, String endDate, String page, String pageSize) {
            if (StringUtils.isNull(token)) {
                Log.warn("token为空");
                return new ErrorBean(ErrorConsts.CODE_10001, "token为空");
            }
            if(StringUtils.isNull(page) || !page.matches("^\\d*$")) {
    			Log.warn("page is error waith page:{}", page);
    			return new ErrorBean(ErrorConsts.CODE_10002, "页码错误");
    		}
    		if(StringUtils.isNull(pageSize) || !pageSize.matches("^\\d*$")) {
    			Log.warn("pageSize is error waith pageSize:{}", pageSize);
    			return new ErrorBean(ErrorConsts.CODE_10002, "记录错误");
    		}
            JSONObject json = orderController.isDate(startDate, endDate);
    		if(json != null) {
    			return new ErrorBean();
    		}
            String id = baseService.checkUserToken(token);
            if (null == id) {
                Log.warn("token失效");
                return new ErrorBean(ErrorConsts.CODE_10001, "token失效");
            }
            Map<String, Object> manageMap = new HashMap<>();
            try {
            	manageMap = userService.listParent(id,Integer.valueOf(page),Integer.valueOf(pageSize), startDate, endDate,2);
            	if("1".equals(page)){
            		Integer sellerNum = sellerService.selectSellerCount();//店铺总数
            		manageMap.put("sellerNum", sellerNum);
                	Double totalAchievement = sellerService.selectManageAchievement(id, startDate, endDate);
                	manageMap.put("totalAchievement", totalAchievement);
            	}
            	
            } catch (Exception e) {
                return new ErrorBean(ErrorConsts.CODE_10004, "查询失败");
            }
            return new SuccessBean(manageMap);
        }
}
