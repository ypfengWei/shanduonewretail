package com.shanduo.newretail.service;

import java.util.List;
import java.util.Map;

import com.shanduo.newretail.entity.AppUpgrade;
import com.shanduo.newretail.entity.ToUser;
import com.shanduo.newretail.entity.service.TokenInfo;

/**
 * 用户操作业务层
 * @ClassName: UserService
 * @Description: TODO
 * @author fanshixin
 * @date 2018年7月12日 上午10:25:49
 *
 */
public interface UserService {

	/**
	 * 注册
	 * @Title: saveUser
	 * @Description: TODO
	 * @param @param openId
	 * @param @param phone
	 * @param @param password
	 * @param @param parentId
	 * @param @param typrId
	 * @param @param name
	 * @param @return
	 * @return int
	 * @throws
	 */
	int saveUser(String openId, String phone, String password, String parentId, String typrId, String name);
	
	/**
	 * 检查手机号是否已存在
	 * @Title: chackPhone
	 * @Description: TODO
	 * @param @param phone
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	boolean chackPhone(String phone);
	
	/**
	 * 生成token
	 * @Title: saveToken
	 * @Description: TODO
	 * @param @param userId
	 * @param @return
	 * @return String
	 * @throws
	 */
	String saveToken(String userId);
	
	/**
	 * 登录
	 * @Title: loginUser
	 * @Description: TODO
	 * @param @param phone
	 * @param @param password
	 * @param @return
	 * @return TokenInfo
	 * @throws
	 */
	TokenInfo loginUser(String phone, String password);
	
	/**
	 * 检查密码是否正确
	 * @Title: checkUserPassword
	 * @Description: TODO
	 * @param @param userId
	 * @param @param password
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	boolean checkUserPassword(String userId, String password);
	
	/**
	 * 修改用户信息
	 * @Title: updateUser
	 * @Description: TODO
	 * @param @param userId
	 * @param @param parameter
	 * @param @param typrId 类型:1.手机号;2.密码;3.昵称;
	 * @param @return
	 * @return int
	 * @throws
	 */
	int updateUser(String userId, String parameter, String typrId);
	
	/**
	 * 验证码修改密码
	 * @Title: updatePassswordByPhone
	 * @Description: TODO
	 * @param @param phone
	 * @param @param password
	 * @param @return
	 * @return int
	 * @throws
	 */
	int updatePassswordByPhone(String phone, String password);
	
	/**
	 * 查询管理员账号
	 * @return
	 */
	String selectAdministratorsId();

	ToUser selectUser(String id);
	
	int updateopenId(String openId, String phone);
	
	AppUpgrade selectApp(Integer appType);
	
	/**
	 * 查询下级
	 * @Title: listParent
	 * @Description: TODO
	 * @param @param parentId
	 * @param @param pageNum
	 * @param @param pageSize
	 * @param @return
	 * @return Map<String, Object>
	 * @throws
	 */
	Map<String, Object> listParent(String parentId, Integer pageNum, Integer pageSize,String startDate, String endDate,int type);
	
	 
}
