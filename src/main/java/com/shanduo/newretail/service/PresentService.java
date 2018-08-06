package com.shanduo.newretail.service;

import java.util.Map;

import com.shanduo.newretail.entity.PresentRecord;

/**
 * 
 * @ClassName: PresentService
 * @Description: TODO
 * @author fanshixin
 * @date 2018年7月19日 下午3:57:08
 *
 */
public interface PresentService {

	/**
	 * 申请提现
	 * @Title: savePresent
	 * @Description: TODO
	 * @param @param userId
	 * @param @param money
	 * @param @param typeId
	 * @param @param name
	 * @param @param openingBank
	 * @param @param bankName
	 * @param @param cardNumber
	 * @param @return
	 * @return int
	 * @throws
	 */
	int savePresent(String userId, String money, String typeId, String name, String openingBank, String bankName, String cardNumber);
	
	/**
	 * 查询单条提现记录
	 * @Title: getPresentRecord
	 * @Description: TODO
	 * @param @param id
	 * @param @param state
	 * @param @return
	 * @return PresentRecord
	 * @throws
	 */
	PresentRecord getPresentRecord(String id, String state);
	
	/**
	 * 提现成功
	 * @Title: updateSucceed
	 * @Description: TODO
	 * @param @param id
	 * @param @return
	 * @return int
	 * @throws
	 */
	int updateSucceed(String id);
	
	/**
	 * 拒绝提现
	 * @Title: updateReject
	 * @Description: TODO
	 * @param @param id
	 * @param @return
	 * @return int
	 * @throws
	 */
	int updateReject(String id);
	
	/**
	 * 管理员查询提现记录
	 * @Title: listPresent
	 * @Description: TODO
	 * @param @param state
	 * @param @param pageNum
	 * @param @param pageSize
	 * @param @return
	 * @return Map<String,Object>
	 * @throws
	 */
	Map<String, Object> listPresent(String state,Integer pageNum,Integer pageSize);
	
	/**
	 * 卖家查询提现记录
	 * @Title: listPresents
	 * @Description: TODO
	 * @param @param sellerId
	 * @param @param pageNum
	 * @param @param pageSize
	 * @param @return
	 * @return Map<String,Object>
	 * @throws
	 */
	Map<String, Object> listPresents(String sellerId,Integer pageNum,Integer pageSize);
	
	
	/**
	 * 卖家查询提现过的银行卡
	 * @Title: getPresent
	 * @Description: TODO
	 * @param @param sellerId
	 * @param @return
	 * @return Map<String, String>
	 * @throws
	 */
	Map<String, String> getPresent(String sellerId);
}
