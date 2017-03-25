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

import com.jfinal.club.common.model.base.BaseAuthCode;

/**
 * 授权码，目前已用于：
 * 1：账号激活
 * 2：密码找回
 * 未来随着业务增加可添加新类型，可能需要加 data 字段传递业务所需的额外数据
 */
@SuppressWarnings("serial")
public class AuthCode extends BaseAuthCode<AuthCode> {

	public static final int TYPE_REG_ACTIVATE = 0;			// 注册激活
	public static final int TYPE_RETRIEVE_PASSWORD = 1;		// 找回密码

	/**
	 * 在保存前保障 type 正确，随着 type 的增加，需要修改此处的代码
	 */
	public boolean save() {
		int type = getType();
		if (type < TYPE_REG_ACTIVATE || type > TYPE_RETRIEVE_PASSWORD) {
			throw new RuntimeException("授权码类型不正确: " + type);
		}
		return super.save();
	}

	public boolean update() {
		throw new RuntimeException("授权码不支持更新操作");
	}

	/**
	 * 是否是有效的注册激活授权码
	 */
	public boolean isValidRegActivateAuthCode() {
		return notExpired() && isTypeRegActivate();
	}

	/**
	 * 是否是有效的密码找回授权码
	 */
	public boolean isValidRetrievePasswordAuthCode() {
		return notExpired() && isTypeRetrievePassword();
	}

	/**
	 * 是否是账号激活授权码
	 */
	public boolean isTypeRegActivate() {
		return getType() == TYPE_REG_ACTIVATE;
	}

	/**
	 * 是否是密码找回授权码
	 */
	public boolean isTypeRetrievePassword() {
		return getType() == TYPE_RETRIEVE_PASSWORD;
	}

	/**
	 * 是否已过期
	 */
	public boolean isExpired() {
		return getExpireAt() < System.currentTimeMillis();
	}

	/**
	 * 是否未过期
	 */
	public boolean notExpired() {
		return ! isExpired();
	}
}

