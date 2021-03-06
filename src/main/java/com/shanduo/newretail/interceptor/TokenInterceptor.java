package com.shanduo.newretail.interceptor;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.shanduo.newretail.consts.ErrorConsts;
import com.shanduo.newretail.entity.common.ErrorBean;

import net.sf.json.JSONObject;

/**
 * token拦截器
 * @ClassName: TokenInterceptor
 * @Description: TODO
 * @author fanshixin
 * @date 2018年4月21日 下午3:22:26
 *
 */
public class TokenInterceptor implements HandlerInterceptor {

	private static final Logger log = LoggerFactory.getLogger(TokenInterceptor.class);

	/***
	 *  检查Token 是否有效   需要拦截的路径 就在 spring 的配置文件中配置
	 */
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		boolean check = false;
		ErrorBean errorBean = new ErrorBean();
		String token = request.getParameter("token");
		if ("".equals(token) || token == null) {
			log.warn("");
			errorBean = new ErrorBean(ErrorConsts.CODE_10001,"");
			check = true;
		}
		//如果为空就直接出去了
		if(check){
			request.setAttribute("errorBean", errorBean);
			JSONObject responseJSONObject = JSONObject.fromObject(errorBean);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json; charset=utf-8");
			PrintWriter out = null;
			out = response.getWriter();
			out.append(responseJSONObject.toString());
			out.close();
			return false;
		}
		return true;
	}

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub

	}

	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub

	}

}
