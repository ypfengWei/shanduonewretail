package com.shanduo.newretail.util;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.shanduo.newretail.entity.service.TokenInfo;

/**
 * 接口返回数据处理类
 * @ClassName: ResultUtils
 * @Description: TODO
 * @author fanshixin
 * @date 2018年7月19日 上午10:26:36
 *
 */
public class ResultUtils {

	/**
	 * 正确返回
	 * @Title: success
	 * @Description: TODO
	 * @param @param object
	 * @param @return
	 * @return JSONObject
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject success(Object object) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("success", true);
		if(object instanceof TokenInfo){
			TokenInfo token = (TokenInfo) object;
            jsonObject.put("token", token.getToken());
            jsonObject.put("userId", token.getUserId());
            jsonObject.put("name", token.getName());
            jsonObject.put("phone", token.getPhone());
            jsonObject.put("jurisdiction", token.getJurisdiction());
            return jsonObject;
        }
		if(object instanceof Map) {
        	Map<String, ?> map = (Map<String, ?>) object;
    		//遍历map中的键
    		for (String key : map.keySet()) {
    		  jsonObject.put(key, map.get(key));
    		}
    		return jsonObject;
		}
		if(object instanceof List<?>) {
			jsonObject.put("data", object);
		}
        jsonObject.put("result", object);
		return jsonObject;
	}
	
	public static JSONObject success(String str,Object object) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("success", true);
		jsonObject.put(str, object);
		return jsonObject;
	}
	/**
	 * 出错返回
	 * @Title: error
	 * @Description: TODO
	 * @param @param errorCode
	 * @param @param errCodeDes
	 * @param @return
	 * @return JSONObject
	 * @throws
	 */
	public static JSONObject error(Integer errorCode,String errCodeDes) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("success", false);
		jsonObject.put("errCode", errorCode);
		jsonObject.put("errCodeDes", errCodeDes);
		return jsonObject;
	}
	
}
