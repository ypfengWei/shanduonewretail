package com.shanduo.newretail.entity.common;

import java.util.Map;

import net.sf.json.JSONObject;

public class Result {

	public static JSONObject success(Object object) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.accumulate("success", "true");
		jsonObject.accumulate("result", object);
		return jsonObject;
	}
	
	public static JSONObject success(Map<String, Object> map) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.accumulate("success", "true");
		//遍历map中的键
		for (String key : map.keySet()) {
		  jsonObject.accumulate(key, map.get(key));
		}
		return jsonObject;
	}
	
	public static JSONObject error(Integer errorCode,String errCodeDes) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.accumulate("success", "false");
		jsonObject.accumulate("errCode", errorCode);
		jsonObject.accumulate("errCodeDes", errCodeDes);
		return jsonObject;
	}
	
}
