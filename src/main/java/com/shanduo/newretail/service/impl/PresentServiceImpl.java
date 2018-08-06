package com.shanduo.newretail.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.newretail.consts.DefaultConsts;
import com.shanduo.newretail.entity.PresentRecord;
import com.shanduo.newretail.mapper.PresentRecordMapper;
import com.shanduo.newretail.service.PresentService;
import com.shanduo.newretail.service.SellerService;
import com.shanduo.newretail.util.OrderIdUtils;
import com.shanduo.newretail.util.Page;

@Service
public class PresentServiceImpl implements PresentService {

	private static final Logger log = LoggerFactory.getLogger(PresentServiceImpl.class);
	
	@Autowired
	private PresentRecordMapper presentRecordMapper;
	@Autowired
	private SellerService sellerService;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int savePresent(String userId, String money, String typeId, String name, String openingBank, String bankName,
			String cardNumber) {
		PresentRecord presentRecord = new PresentRecord();
		presentRecord.setId(OrderIdUtils.getId());
		presentRecord.setUserId(userId);
		presentRecord.setAmountCash(new BigDecimal(money));
		presentRecord.setTypeid(typeId);
		presentRecord.setUserName(name);
		if(DefaultConsts.NUMBER_2.equals(typeId)) {
			presentRecord.setOpeningBank(openingBank);
			presentRecord.setBankName(bankName);
			presentRecord.setCardNumber(cardNumber);
		}
		int i = presentRecordMapper.insertSelective(presentRecord);
		if(i < 1) {
			log.warn("提现录入失败");
			throw new RuntimeException();
		}
		sellerService.updateMoney(new BigDecimal(money), userId, "1");
		return 1;
	}

	@Override
	public PresentRecord getPresentRecord(String id,String state) {
		return presentRecordMapper.getPresentRecord(id, state);
	}

	@Override
	public int updateSucceed(String id) {
		PresentRecord presentRecord = new PresentRecord();
		presentRecord.setId(id);
		presentRecord.setPresentTime(new Date());
		presentRecord.setState("2");
		return presentRecordMapper.updateByPrimaryKeySelective(presentRecord);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateReject(String id) {
		PresentRecord presentRecord = new PresentRecord();
		presentRecord.setId(id);
		presentRecord.setState("3");
		int i = presentRecordMapper.updateByPrimaryKeySelective(presentRecord);
		if(i < 1) {
			log.warn("拒绝提现失败");
			throw new RuntimeException();
		}
		presentRecord = getPresentRecord(id, "3");
		sellerService.updateMoney(presentRecord.getAmountCash(), presentRecord.getUserId(), "0");
		return 1;
	}

	@Override
	public Map<String, Object> listPresent(String state, Integer pageNum, Integer pageSize) {
		int totalRecord = presentRecordMapper.countPresentRecord(state);
		Page page = new Page(totalRecord, pageSize, pageNum);
		pageNum = (page.getPageNum() - 1) * page.getPageSize();
		List<PresentRecord> listPresentRecord = presentRecordMapper.listPresentRecord(state, pageNum, page.getPageSize());
		Map<String, Object> resultMap = new HashMap<String, Object>(4);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalPage", page.getTotalPage());
		resultMap.put("data", listPresentRecord);
		if(pageNum == 0) {
			Double countMeny = presentRecordMapper.snmStateMoney(state);
			resultMap.put("countMoney", countMeny);
		}
		return resultMap;
	}

	@Override
	public Map<String, Object> listPresents(String sellerId, Integer pageNum, Integer pageSize) {
		int totalRecord = presentRecordMapper.countUserPresentRecord(sellerId);
		Page page = new Page(totalRecord, pageSize, pageNum);
		pageNum = (page.getPageNum() - 1) * page.getPageSize();
		List<PresentRecord> listPresentRecord = presentRecordMapper.listUserPresentRecord(sellerId, pageNum, page.getPageSize());
		Map<String, Object> resultMap = new HashMap<String, Object>(4);
		resultMap.put("page", page.getPageNum());
		resultMap.put("totalPage", page.getTotalPage());
		resultMap.put("data", listPresentRecord);
		if(pageNum == 0) {
			Double countMeny = presentRecordMapper.snmUserMoney(sellerId);
			resultMap.put("countMoney", countMeny);
		}
		return resultMap;
	}

	@Override
	public Map<String, String> getPresent(String sellerId) {
		return presentRecordMapper.getPresent(sellerId);
	}

}
