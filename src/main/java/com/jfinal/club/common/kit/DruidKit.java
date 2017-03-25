/**
 * 请勿将俱乐部专享资源复制给其他人，保护知识产权即是保护我们所在的行业，进而保护我们自己的利益
 * 即便是公司的同事，也请尊重 JFinal 作者的努力与付出，不要复制给同事
 * 
 * 如果你尚未加入俱乐部，请立即删除该项目，或者现在加入俱乐部：http://jfinal.com/club
 * 
 * 俱乐部将提供 jfinal-club 项目文档与设计资源、专用 QQ 群，以及作者在俱乐部定期的分享与答疑，
 * 价值远比仅仅拥有 jfinal club 项目源代码要大得多
 * 
 * JFinal 俱乐部是五年以来首次寻求外部资源的尝试，以便于有资源创建更加
 * 高品质的产品与服务，为大家带来更大的价值，所以请大家多多支持，不要将
 * 首次的尝试扼杀在了摇篮之中
 */

package com.jfinal.club.common.kit;

import com.jfinal.plugin.druid.DruidStatViewHandler;
import com.jfinal.plugin.druid.IDruidStatViewAuth;
import com.jfinal.club.common.interceptor.AdminAuthInterceptor;
import com.jfinal.club.common.model.Account;
import com.jfinal.club.login.LoginService;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * 创建 DruidStatViewHandler 的工具类
 * 
 * 可通过 "/assets/druid" 访问到 druid 提供的 sql 监控与统计功能
 * 方便找到慢 sql，进而对慢 sql 进行优化
 * 注意：这里的访问路径是下面代码中指定的，可以设置为任意路径
 * 
 * 注意 druid 监控模块中使用的静态资源文件如 .html .css 被打包在了 druid 的jar 包之中
 * 如果你的项目在前端有 nginx 代理过了这些静态资源，需要将这些资源解压出来并放到
 * 正确的目录下面
 * 
 * 具体到该配置中的 url 为 "/assets/druid"，那么相关静态资源需要解压到该目录之下
 */
public class DruidKit {

	public static DruidStatViewHandler getDruidStatViewHandler() {
		return  new DruidStatViewHandler("/assets/druid", new IDruidStatViewAuth() {
			public boolean isPermitted(HttpServletRequest request) {
				String sessionId = getCookie(request, LoginService.sessionIdName);
				if (sessionId != null) {
					Account loginAccount = LoginService.me.getLoginAccountWithSessionId(sessionId);
					return AdminAuthInterceptor.isAdmin(loginAccount);
				}
				return false;
			}
		});
	}

	public static String getCookie(HttpServletRequest request, String name) {
		Cookie cookie = getCookieObject(request, name);
		return cookie != null ? cookie.getValue() : null;
	}

	private static Cookie getCookieObject(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null)
			for (Cookie cookie : cookies)
				if (cookie.getName().equals(name))
					return cookie;
		return null;
	}
}
