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

package com.jfinal.club.common.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.StrKit;
import com.jfinal.club.login.LoginService;

/**
 * 需要登录才能授权的操作，例如文件下载
 * 
 * 未登录将被重定向到登录界面，登录成功后又会再返回到原来想跳去的 url
 * 注意在登录表单中有 returnUrl 变量的传递才能跳到原来想跳去的 url
 * 详见登录页面表单传参
 */
public class FrontAuthInterceptor implements Interceptor {
	public void intercept(Invocation inv) {
		if (inv.getController().getAttr(LoginService.loginAccountCacheName) != null) {
			inv.invoke();
		} else {
			String queryString = inv.getController().getRequest().getQueryString();
			if (StrKit.isBlank(queryString)) {
				inv.getController().redirect("/login?returnUrl=" + inv.getActionKey());
			} else {
				inv.getController().redirect("/login?returnUrl=" + inv.getActionKey() + "?" + queryString);
			}
		}
	}
}

