package com.cn.dsyg.interceptor;

import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.cn.common.util.Constants;
import com.cn.common.util.StringUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * @name FrameInterceptor.java
 * @author Frank
 * @time 2015-7-21下午9:27:57
 * @version 1.0
 */
public class FrameInterceptor extends AbstractInterceptor {

	private static final long serialVersionUID = 5384539864431422087L;
	
	private static final Logger log = LogManager.getLogger(FrameInterceptor.class);

	@SuppressWarnings("unchecked")
	@Override
	public String intercept(ActionInvocation arg0) throws Exception {
		//验证用户是否登录
		String islogin = (String) ActionContext.getContext().getSession().get(Constants.SESSION_ISLOGIN);
		if(StringUtil.isBlank(islogin)) {
			//未登录
			log.info("user is not login.");
			return "login";
		} else {
			if(!Constants.SESSION_FLAG_IS_LOGIN.equals(islogin)) {
				//session过期
				log.info("session is time out.");
				return "login";
			}
			//判断当前用户是否有权限访问此资源
			Map<String, Integer> map = (Map<String, Integer>) ActionContext.getContext().getSession().get(Constants.SESSION_RESOURCE_MAP);
			if(map != null && map.size() > 0) {
				map.put("/frame/showLeftAction.action", 1);
				map.put("/frame/showMainFrameAction.action", 1);
				String currentUrl = arg0.getProxy().getNamespace() + "/" + arg0.getProxy().getActionName() + ".action";
				if(map.get(currentUrl) != null && map.get(currentUrl) == 0) {
					return "noauthority";
				}
				return arg0.invoke();
			}
			return "noauthority";
		}
	}
}
