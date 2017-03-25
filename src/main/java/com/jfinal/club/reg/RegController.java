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

package com.jfinal.club.reg;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.club.common.kit.IpKit;
import com.jfinal.kit.Ret;

/**
 * 注册控制器
 */
public class RegController extends Controller {

	private static final RegService srv = RegService.me;

	public void index() {
		render("index.html");
	}

	/**
	 * 注册操作
 	 */
	@Before(RegValidator.class)
	public void save() {
		String ip = IpKit.getRealIp(getRequest());
		Ret ret = srv.reg(getPara("userName"), getPara("password"), getPara("nickName"), ip);
		if (ret.isOk()) {
			ret.set("regEmail", getPara("userName"));
		}
		renderJson(ret);
	}

	/**
	 * 显示还没激活页面
	 */
	public void notActivated() {
		render("not_activated.html");
	}

	/**
	 * 重发激活邮件
	 */
	public void reSendActivateEmail() {
		Ret ret = srv.reSendActivateEmail(getPara("email"));
		renderJson(ret);
	}

	/**
	 * 激活，发送给用户注册邮箱中的带有 authCode 的激活链接指向该 action
	 */
	public void activate() {
		Ret ret = srv.activate(getPara("authCode"));
		setAttr("ret", ret);
		render("activate.html");
	}

	public void captcha() {
		renderCaptcha();
	}
}
