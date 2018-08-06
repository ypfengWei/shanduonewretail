package com.shanduo.newretail.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shanduo.newretail.consts.DefaultConsts;
import com.shanduo.newretail.entity.AppUpgrade;
import com.shanduo.newretail.entity.ToUser;
import com.shanduo.newretail.entity.service.TokenInfo;
import com.shanduo.newretail.entity.service.UserInfo;
import com.shanduo.newretail.mapper.AppUpgradeMapper;
import com.shanduo.newretail.mapper.ToUserMapper;
import com.shanduo.newretail.mapper.UserSellerMapper;
import com.shanduo.newretail.mapper.UserTokenMapper;
import com.shanduo.newretail.service.SellerService;
import com.shanduo.newretail.service.UserService;
import com.shanduo.newretail.util.MD5Utils;
import com.shanduo.newretail.util.Page;
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
	@Autowired
	private UserSellerMapper userSellerMapper;
	@Autowired
	private AppUpgradeMapper appUpgradeMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int saveUser(String openId, String phone, String password, String parentId, String typeId, String name) {
		String id = UUIDGenerator.getUUID();
		password = MD5Utils.getInstance().getMD5(password);
//		String name = phone.replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
		ToUser user = new ToUser();
		user.setId(id);
		user.setOpenId(openId);
		user.setName(name);
		user.setMobilePhone(phone);
		user.setPassword(password);
		user.setJurisdiction(typeId);
		user.setParentId(parentId);
		int i = userMapper.insertSelective(user);
		if(i < 1) {
			log.warn("regin user is error waith phone:{} and password:{}", phone, password);
			throw new RuntimeException();
		}
		if(!typeId.equals(DefaultConsts.ROLE_MERCHANT)) {
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

	@Override
	public String selectAdministratorsId() {
		
		return userMapper.selectAdministratorsId();
	}

	@Override
	public ToUser selectUser(String id) {
		return userMapper.selectByPrimaryKey(id);
	}

	@Override
	public Map<String, Object> listParent(String parentId, Integer pageNum, Integer pageSize,String startDate, String endDate,int type) {
		int totalRecord = userMapper.countParent(parentId);
		Page page = new Page(totalRecord, pageSize, pageNum);
		pageNum = (page.getPageNum() - 1) * page.getPageSize();
		List<UserInfo> list = userMapper.listParent(parentId, pageNum, page.getPageSize());
		UserInfo userInfo = new UserInfo();
		for(int i=0;i<list.size();i++){
			userInfo = list.get(i);
			if(1==type){
				Double achievement = userSellerMapper.selectSalesmanAchievement(userInfo.getId(), startDate, endDate);
				userInfo.setAchievement(achievement);
				Integer sellerNum = userSellerMapper.selectSubordinateCount(userInfo.getId());
				userInfo.setSellerNum(sellerNum);
			}else{
				Double achievement = userSellerMapper.selectRegionAchievement(userInfo.getId(), startDate, endDate);
				userInfo.setAchievement(achievement);
				Integer sellerNum = userSellerMapper.selectSubordinateSellerCount(userInfo.getId());
				userInfo.setSellerNum(sellerNum);
			}
			
		}
		Map<String, Object> resultMap = new HashMap<String, Object>(3);
		resultMap.put("totalPage", page.getTotalPage());
		resultMap.put("subordinateNum", totalRecord);
		resultMap.put("data", list);
		return resultMap;
	}

	@Override
	public int updateopenId(String openId, String phone) {
		
		return userMapper.updateopenId(openId, phone);
	}

	@Override
	public AppUpgrade selectApp(Integer appType) {
		
		return appUpgradeMapper.selectApp(appType);
	}

}
