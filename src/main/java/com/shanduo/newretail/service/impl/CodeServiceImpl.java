package com.shanduo.newretail.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.newretail.mapper.PhoneVerifyCodeMapper;
import com.shanduo.newretail.service.CodeService;

@Service
@Transactional
public class CodeServiceImpl implements CodeService {

	private static final Logger log = LoggerFactory.getLogger(CodeServiceImpl.class);
	
	@Autowired
	private PhoneVerifyCodeMapper codeMapper;
	
}
