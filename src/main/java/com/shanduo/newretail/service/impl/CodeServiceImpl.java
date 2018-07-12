package com.shanduo.newretail.service.impl;

import java.text.Format;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.newretail.entity.PhoneVerifyCode;
import com.shanduo.newretail.mapper.PhoneVerifyCodeMapper;
import com.shanduo.newretail.service.CodeService;
import com.shanduo.newretail.util.UUIDGenerator;

/**
 * 
 * @ClassName: CodeServiceImpl
 * @Description: TODO
 * @author fanshixin
 * @date 2018年7月11日 下午4:13:34
 *
 */
@Service
public class CodeServiceImpl implements CodeService {

	private static final Logger log = LoggerFactory.getLogger(CodeServiceImpl.class);
	
	@Autowired
	private PhoneVerifyCodeMapper codeMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int saveCode(String phone, String codes, String codeType) {
		PhoneVerifyCode code = new PhoneVerifyCode();
		code.setId(UUIDGenerator.getUUID());
		code.setPhone(phone);
		code.setCode(codes);
		code.setCodeType(codeType);
		int i = codeMapper.insertSelective(code);
		if(i < 0) {
			log.error("录入验证码失败");
			throw new RuntimeException();
		}
		return 1;
	}

	@Override
	public boolean checkCode(String phone, String codes, String codeType) {
		PhoneVerifyCode code = codeMapper.getCode(phone, codes, codeType, getDate(10));
		if(code == null) {
			log.warn("code is error waith phone:{} and code:{}", phone, codes);
			return true;
		}
		return false;
	}

	@Override
	public boolean checkSend(String phone, String codeType) {
		PhoneVerifyCode code = codeMapper.getCodes(phone, codeType, getDate(1));
		if(code != null) {
			log.warn("code is restrict waith phone:{}", phone);
			return true;
		}
		return false;
	}
	
	public String getDate(int minute) {
		long time = System.currentTimeMillis();
		Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(time - 1000 * 60 * minute);
	}
}
