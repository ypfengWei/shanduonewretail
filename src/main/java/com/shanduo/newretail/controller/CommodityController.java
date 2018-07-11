package com.shanduo.newretail.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shanduo.newretail.entity.common.ErrorBean;
import com.shanduo.newretail.entity.common.ResultBean;
import com.shanduo.newretail.entity.common.SuccessBean;
import com.shanduo.newretail.service.CommodityService;
import com.shanduo.newretail.util.StringUtils;

/**
 * 商品控制层
 * @ClassName: CommodityController
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author lishan
 * @date 2018年4月16日 下午3:12:29
 */
@Controller
@RequestMapping(value = "activity")
public class CommodityController {

	private static final Logger log = LoggerFactory.getLogger(CommodityController.class);

	@Autowired
	private CommodityService cmmodityService;

	
	
	/**
	 * 查看单个活动详情
	 * @Title: activityDetails
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param activityId
	 * @param @param lon
	 * @param @param lat
	 * @param @return    设定文件
	 * @return ResultBean    返回类型
	 * @throws
	 */
	@RequestMapping(value = "details", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ResultBean details(HttpServletRequest request, String activityId, String lon, String lat) {
		if(StringUtils.isNull(activityId)) {
			log.error("活动ID为空");
			return new ErrorBean();
		}
		
		return new SuccessBean();
	}
	
	
}