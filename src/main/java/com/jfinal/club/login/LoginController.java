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

package com.jfinal.club.login;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.club.common.kit.IpKit;
import com.jfinal.kit.Ret;

/**
 * 登录控制器
 */
public class LoginController extends Controller {

	static final LoginService srv = LoginService.me;

	/**
	 * 显示登录界面
	 */
	public void index() {
		keepPara("returnUrl");  // 保持住 returnUrl 这个参数，以便在登录成功后跳转到该参数指向的页面
		render("index.html");
	}

	/**
	 * 登录
	 */
	@Before(LoginValidator.class)
	public void doLogin() {
		boolean keepLogin = getParaToBoolean("keepLogin", false);
		String loginIp = IpKit.getRealIp(getRequest());
		Ret ret = srv.login(getPara("userName"), getPara("password"), keepLogin, loginIp);
		if (ret.isOk()) {
			String sessionId = ret.getStr(LoginService.sessionIdName);
			int maxAgeInSeconds = ret.getAs("maxAgeInSeconds");
			setCookie(LoginService.sessionIdName, sessionId, maxAgeInSeconds, true);
			setAttr(LoginService.loginAccountCacheName, ret.get(LoginService.loginAccountCacheName));

			ret.set("returnUrl", getPara("returnUrl", "/"));    // 如果 returnUrl 存在则跳过去，否则跳去首页
		}
		renderJson(ret);
	}

	/**
	 * 退出登录
	 */
	@Clear
	@ActionKey("/logout")
	public void logout() {
		srv.logout(getCookie(LoginService.sessionIdName));
		removeCookie(LoginService.sessionIdName);
		redirect("/");
	}

	/**
	 * 显示忘记密码页面
	 */
	public void forgetPassword() {
		render("forget_password.html");
	}

	/**
	 * 发送找回密码邮件
	 */
	public void sendRetrievePasswordEmail() {
		Ret ret = srv.sendRetrievePasswordAuthEmail(getPara("email"));
		renderJson(ret);
	}

	/**
	 * 1：keepPara("authCode") 将密码找回链接中问号挂参的 authCode 传递到页面
	 * 2：在密码找回页面中与用户输入的新密码一起传回给 doPassReturn 进行密码修改
	 */
	public void retrievePassword() {
		keepPara("authCode");
		render("retrieve_password.html");
	}

	/**
	 * ajax 密码找回
	 * 1：判断 authCode 是否有效
	 * 2：有效则更新密码
	 */
	public void doRetrievePassword() {
		Ret ret = srv.retrievePassword(getPara("authCode"), getPara("newPassword"));
		renderJson(ret);
	}

	public void captcha() {
		renderCaptcha();
	}
}

