package com.shanduo.newretail.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shanduo.newretail.entity.ToUser;
import com.shanduo.newretail.entity.UserToken;
import com.shanduo.newretail.mapper.ToUserMapper;
import com.shanduo.newretail.mapper.UserTokenMapper;
import com.shanduo.newretail.service.BaseService;

/**
 * 
 * @ClassName: BaseServiceImpl
 * @Description: TODO
 * @author fanshixin
 * @date 2018年7月12日 上午10:28:01
 *
 */
@Service
public class BaseServiceImpl implements BaseService {

	private static final Logger log = LoggerFactory.getLogger(BaseServiceImpl.class);
	
	@Autowired
	private UserTokenMapper tokenMapper;
	@Autowired
	private ToUserMapper userMapper;
	
	@Override
	public String checkUserToken(String token) {
		UserToken userToken = tokenMapper.selectByPrimaryKey(token);
		if(userToken == null) {
			log.warn("token is lose efficacy waith token:{}",token);
			return null;
		}
		return userToken.getUserId();
	}

	@Override
	public boolean checkUserRole(String userId, String role) {
		ToUser user = userMapper.selectByPrimaryKey(userId);
		if(user == null) {
			return true;
		}
		return !user.getJurisdiction().equals(role);
	}

}
