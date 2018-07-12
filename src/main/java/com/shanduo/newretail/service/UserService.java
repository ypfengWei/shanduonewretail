package com.shanduo.newretail.service;

/**
 * 用户操作业务层
 * @ClassName: UserService
 * @Description: TODO
 * @author fanshixin
 * @date 2018年7月12日 上午10:25:49
 *
 */
public interface UserService {

	int saveUser(String phone, String password);
	
	boolean chackPhone(String phone);
}
