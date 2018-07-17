package com.shanduo.newretail.service;

/**
 * 短信验证码业务层
 * @ClassName: CodeService
 * @Description: TODO
 * @author fanshixin
 * @date 2018年7月11日 下午4:13:11
 *
 */
public interface CodeService {

	/**
	 * 发送验证码
	 * @Title: saveCode
	 * @Description: TODO
	 * @param @param phone
	 * @param @param codes
	 * @param @param codeType
	 * @param @return
	 * @return int
	 * @throws
	 */
	int saveCode(String phone, String codes, String codeType);
	
	/**
	 * 效验验证码
	 * @Title: checkCode
	 * @Description: TODO
	 * @param @param phone
	 * @param @param codes
	 * @param @param codeType
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	boolean checkCode(String phone, String codes, String codeType);
	
	/**
	 * 效验发送频率
	 * @Title: checkSend
	 * @Description: TODO
	 * @param @param phone
	 * @param @param codeType
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	boolean checkSend(String phone,String codeType);
}
