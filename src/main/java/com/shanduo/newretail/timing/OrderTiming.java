package com.shanduo.newretail.timing;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.shanduo.newretail.service.OrderService;
import com.shanduo.newretail.util.IOSXGHighUtils;
import com.shanduo.newretail.util.XGHighUtils;

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

//	private static final Logger log = LoggerFactory.getLogger(OrderTiming.class);
	
	@Autowired
	private OrderService orderService;
	
	@Scheduled(cron = "0 0/1 * * * ?")
	public void delTiming() {
		List<String> list = orderService.listPending();
		if(null == list || list.isEmpty() || list.size() == 0) {
			return;
		}
		//ios批量推送
		IOSXGHighUtils.getInstance().pushAccountListIOS(list);
		//安卓推送
		XGHighUtils.getInstance().pushAccountList(list);
//		log.info("推送 {} 家店铺接单",list.size());
	}
	
}
