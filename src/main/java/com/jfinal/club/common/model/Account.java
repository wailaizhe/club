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

package com.jfinal.club.common.model;

import com.jfinal.club.common.safe.JsoupFilter;
import com.jfinal.club.common.model.base.BaseAccount;

/**
 * Account
 */
public class Account extends BaseAccount<Account> {
	private static final long serialVersionUID = 1L;

	public static final String AVATAR_NO_AVATAR = "x.jpg";    // 刚注册时使用默认头像

	public static final int STATUS_LOCK_ID = -1;	// 锁定账号，无法做任何事情
	public static final int STATUS_REG = 0;			// 注册、未激活
	public static final int STATUS_OK = 1;			// 正常、已激活

	public boolean isStatusOk() {
		return getStatus() == STATUS_OK;
	}

	public boolean isStatusReg() {
		return getStatus() == STATUS_REG;
	}

	public boolean isStatusLockId() {
		return getStatus() == STATUS_LOCK_ID;
	}

	/**
	 * 过滤掉 nickName 中的 html 标记，恶意脚本
	 */
	protected void filter(int filterBy) {
		JsoupFilter.filterAccountNickName(this);
	}

	public Account removeSensitiveInfo() {
		remove("password", "salt");
		return this;
	}
}
