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

package com.jfinal.club.common.safe;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.club.common.model.Account;
import com.jfinal.club.login.LoginService;
import org.joda.time.LocalTime;

/**
 * 休息时间对待内容提交的策略
 */
public class RestTime implements Interceptor {

	// 动态加载在休息时间可允许的最大的id 值
	private static final int restTimeMaxId = 44079;   //PropKit.getInt("restTimeMaxId");

	private static final LocalTime workTime = new LocalTime(8, 30);     // LocalTime.parse("08:30");
	private static final LocalTime restTime = new LocalTime(22, 0);       // LocalTime.parse("22:00");

	public void intercept(Invocation inv) {
		Account loginAccount = inv.getController().getAttr(LoginService.loginAccountCacheName);
		String msg = checkRestTime(loginAccount);
		if (msg != null) {
			inv.getController().renderJson("msg", msg);
		} else {
			inv.invoke();
		}
	}

	/**
	 * 如果是休息时间则返回提示信息，否则返回 null
	 */
	public static String checkRestTime(Account loginAccount) {
		// 如果账号 id 小于restTimeMaxId 则放行，允许任何时候发布内容
		if (loginAccount.getId() <= restTimeMaxId) {
			return null;
		}

		LocalTime now = LocalTime.now();
		if (now.isBefore(workTime)) {
			return "太早了点吧，赶紧吃早饭去，吃完后再来发哈！";
		} else if (now.isAfter(restTime)) {
			return "夜深了，早点休息，明天再来发哈！";
		} else {
			return null;
		}
	}
}
