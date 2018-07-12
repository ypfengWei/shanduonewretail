package com.shanduo.newretail.service;

/**
 * 权限业务层
 * @ClassName: BaseService
 * @Description: TODO
 * @author fanshixin
 * @date 2018年7月12日 上午10:04:52
 *
 */
public interface BaseService {

	/**
	 * 检查token
	 * @Title: checkUserToken
	 * @Description: TODO
	 * @param @param token
	 * @param @return
	 * @return String
	 * @throws
	 */
	String checkUserToken(String token);
	
	/**
	 * 检查权限
	 * @Title: checkUserRole
	 * @Description: TODO
	 * @param @param userId 用户ID
	 * @param @param role 权限ID
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	boolean checkUserRole(String userId, String role);
}
