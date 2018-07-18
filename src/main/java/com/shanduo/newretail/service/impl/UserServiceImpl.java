package com.shanduo.newretail.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.newretail.consts.DefaultConsts;
import com.shanduo.newretail.entity.ToUser;
import com.shanduo.newretail.entity.service.TokenInfo;
import com.shanduo.newretail.mapper.ToUserMapper;
import com.shanduo.newretail.mapper.UserTokenMapper;
import com.shanduo.newretail.service.SellerService;
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
	@Autowired
	private UserTokenMapper tokenMapper;
	@Autowired
	private SellerService sellerService;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int saveUser(String openId, String phone, String password, String parentId, String typrId, String name) {
		String id = UUIDGenerator.getUUID();
		password = MD5Utils.getInstance().getMD5(password);
//		String name = phone.replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
		ToUser user = new ToUser();
		user.setId(id);
		user.setOpenId(openId);
		user.setName(name);
		user.setMobilePhone(phone);
		user.setPassword(password);
		user.setJurisdiction(typrId);
		int i = userMapper.insertSelective(user);
		if(i < 1) {
			log.warn("regin user is error waith phone:{} and password:{}", phone, password);
			throw new RuntimeException();
		}
		if(!typrId.equals(DefaultConsts.ROLE_MERCHANT)) {
			return 1;
		}
		i = sellerService.insertSeller(id, name, phone, parentId);
		if(i < 1) {
			log.warn("regin seller is error waith phone:{} and password:{}", phone, password);
			throw new RuntimeException();
		}
		return 1;
	}

	@Override
	public boolean chackPhone(String phone) {
		ToUser user = userMapper.getPhone(phone);
		if(user != null) {
			return true;
		}
		return false;
	}

	@Override
	public String saveToken(String userId) {
		String token = UUIDGenerator.getUUID();
		int i = tokenMapper.saveToken(token, userId);
		if(i < 1) {
			log.warn("token is error waith userId:{}", userId);
			return "";
		}
		return token;
	}

	@Override
	public TokenInfo loginUser(String phone, String password) {
		password = MD5Utils.getInstance().getMD5(password);
		ToUser user = userMapper.getLogin(phone, password);
		if(user == null) {
			log.warn("login is error waith phone:{} and password:{}", phone, password);
			return null;
		}
		String token = saveToken(user.getId());
		if("".equals(token)) {
			return null;
		}
		return new TokenInfo(token, user);
	}
	
	@Override
	public boolean checkUserPassword(String userId, String password) {
		ToUser user = userMapper.selectByPrimaryKey(userId);
		password = MD5Utils.getInstance().getMD5(password);
		if(password.equals(user.getPassword())) {
			return false;
		}
		return true;
	}
	
	@Override
	public int updateUser(String userId, String parameter, String typrId) {
		ToUser user = new ToUser();
		user.setId(userId);
		switch (typrId) {
		case DefaultConsts.NUMBER_1:
			user.setMobilePhone(parameter);
			break;
		case DefaultConsts.NUMBER_2:
			parameter = MD5Utils.getInstance().getMD5(parameter);
			user.setPassword(parameter);
			break;
		case DefaultConsts.NUMBER_3:
			user.setName(parameter);
			break;
		default:
			break;
		}
		return userMapper.updateByPrimaryKeySelective(user);
	}

	@Override
	public int updatePassswordByPhone(String phone, String password) {
		password = MD5Utils.getInstance().getMD5(password);
		return userMapper.updatePassswordByPhone(phone, password);
	}

}
