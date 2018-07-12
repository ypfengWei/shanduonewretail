package com.shanduo.newretail.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.newretail.consts.DefaultConsts;
import com.shanduo.newretail.entity.ToUser;
import com.shanduo.newretail.mapper.ToUserMapper;
import com.shanduo.newretail.service.UserService;
import com.shanduo.newretail.util.MD5Utils;
import com.shanduo.newretail.util.UUIDGenerator;

/**
 * 
 * @ClassName: UserServiceImpl
 * @Description: TODO
 * @author fanshixin
 * @date 2018年7月12日 上午10:26:32
 *
 */
@Service
public class UserServiceImpl implements UserService {

	private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Autowired
	private ToUserMapper userMapper;
	
	@Override
	@Transactional
	public int saveUser(String phone, String password) {
		String id = UUIDGenerator.getUUID();
		password = MD5Utils.getInstance().getMD5(password);
		ToUser user = new ToUser();
		user.setId(id);
		user.setName("");
		user.setMobilePhone(phone);
		user.setPassword(password);
		user.setJurisdiction(DefaultConsts.ROLE_MERCHANT);
		int i = userMapper.insertSelective(user);
		if(i < 1) {
			throw new RuntimeException();
		}
		return 1;
	}

	@Override
	public boolean chackPhone(String phone) {
		ToUser user = userMapper.getUserByPhone(phone);
		if(user != null) {
			return true;
		}
		return false;
	}

}
