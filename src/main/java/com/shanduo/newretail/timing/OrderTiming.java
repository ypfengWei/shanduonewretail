package com.shanduo.newretail.timing;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.shanduo.newretail.service.OrderService;
import com.shanduo.newretail.util.IOSXGHighUtils;

/**
 * 每日定时工具类
 * @ClassName: EverydayTiming
 * @Description: TODO
 * @author fanshixin
 * @date 2018年6月14日 下午3:22:35
 *
 */
@Component
public class OrderTiming {

	private static final Logger log = LoggerFactory.getLogger(OrderTiming.class);
	
	@Autowired
	private OrderService orderService;
	
	@Scheduled(cron = "0 0/1 * * * ?")
	public void delTiming() {
		List<String> list = orderService.listPending();
		for (int i = 0; i < list.size(); i++) {
			//ios推送
		    IOSXGHighUtils.getInstance().pushSingleAccount(list.get(i));
		    //安卓推送
		    
		}
		log.info("推送 {} 家店铺接单",list.size());
	}
	
}
