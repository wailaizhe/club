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

package com.jfinal.club.common.authcode;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.kit.Ret;
import com.jfinal.club.common.model.AuthCode;

/**
 * 授权码业务
 * 用于一切需要授权的业务，例如：
 * 1：邮件激活
 * 2：密码找回
 * 3：未来一切需要授权码的场景
 */
public class AuthCodeService {
	
	public static final AuthCodeService me = new AuthCodeService();
	private static final AuthCode dao = new AuthCode().dao();

	/**
	 * 创建注册激活授权码，一个小时后过期，3600 秒
	 */
	public String createRegActivateAuthCode(int accountId) {
		return createAuthCode(accountId, AuthCode.TYPE_REG_ACTIVATE, 3600);
	}

	/**
	 * 创建密码找回授权码，一个小时后过期，3600 秒
	 */
	public String createRetrievePasswordAuthCode(int accountId) {
		return createAuthCode(accountId, AuthCode.TYPE_RETRIEVE_PASSWORD, 3600);
	}

	/**
	 * 获取授权码，授权码只能使用一次，在被获取后会被立即删除
	 */
	public AuthCode getAuthCode(String authCodeId) {
		if (StrKit.notBlank(authCodeId)) {
			AuthCode authCode = dao.findById(authCodeId.trim());    // authCode 是唯一的
			if (authCode != null) {
				authCode.delete();   // 只要找到 authCode，则立即删除
				return authCode;
			}
		}
		return null;
	}

	/**
	 * 创建授权码，并自动保存到数据库
	 * @param accountId 用户账号id
	 * @param authType 授权类型
	 * @param expireTime 授权码过期时长，过期时长是指授权码自创建时间起直到过期的时间长度，单位为秒
	 */
	private String createAuthCode(int accountId, int authType, int expireTime) {
		long et = expireTime;   // 使用 long et 为了避免 int 数值溢出，造成保存到数据库中的数值错误
		long expireAt = System.currentTimeMillis() + (et * 1000);

		AuthCode ac = new  AuthCode();
		ac.setId(StrKit.getRandomUUID());
		ac.setAccountId(accountId);
		ac.setType(authType);
		ac.setExpireAt(expireAt);

		if (ac.save()) {
			return ac.getId();
		} else {
			throw new RuntimeException("保存 auth_code 记录失败，请联系管理员");
		}
	}

	/**
	 * 看一眼授权码，未过期时则不删除
	 */
	public Ret peekAuthCode(String id) {
		AuthCode authCode = dao.findById(id);
		if (authCode != null) {
			if (authCode.notExpired()) {
				return Ret.ok("authCode", authCode);
			} else {
				authCode.delete();
				return Ret.fail("msg", "授权码已过期");
			}
		} else {
			return Ret.fail("msg", "授权码不存在");
		}
	}
	
	/**
	 * 主动清除未使用过的过期授权码
	 * 不用经常调用，因为授权码在第一次使用时会自动删除，过期的未删除的授权码仅是未使用过的
	 */
	public int clearExpiredAuthCode() {
		return Db.update("delete from auth_code where expireAt < ?", System.currentTimeMillis());
	}
}







