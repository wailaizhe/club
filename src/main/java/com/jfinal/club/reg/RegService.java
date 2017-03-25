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

import com.jfinal.kit.HashKit;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.club.common.authcode.AuthCodeService;
import com.jfinal.club.common.kit.EmailKit;
import com.jfinal.kit.Ret;
import com.jfinal.club.common.model.Account;
import com.jfinal.club.common.model.AuthCode;
import com.jfinal.club.my.message.MessageService;
import java.util.Date;

/**
 * 注册账号、激活账号业务
 */
public class RegService {

	public static final RegService me = new RegService();
	private final Account accountDao = new Account().dao();

	/**
	 * 用户名是否已被注册
	 */
	public boolean isUserNameExists(String userName) {
		userName = userName.toLowerCase().trim();
		return Db.queryInt("select id from account where userName = ? limit 1", userName) != null;
	}

	/**
	 * 昵称是否已被注册，昵称不区分大小写，以免存在多个用户昵称看起来一个样的情况
	 *
	 *  mysql 的 where 字句与 order by 子句默认不区分大小写，区分大小写需要在
	 *  字段名或字段值前面使用 binary 关键字例如：
	 *  where nickName = binary "jfinal" 或者 where binary nickName = "jfinal"，前者性能要高
	 *
	 *  为了避免不同的 mysql 配置破坏掉 mysql 的 where 不区分大小写的行为，这里在 sql 中使用
	 *  lower(...) 来处理，参数 nickName 也用 toLowerCase() 方法来处理，再次确保不区分大小写
	 */
	public boolean isNickNameExists(String nickName) {
		nickName = nickName.toLowerCase().trim();
		return Db.queryInt("select id from account where lower(nickName) = ? limit 1", nickName) != null;
	}

	/**
	 * 账户注册，hashedPass = sha256(32字符salt + pass)
	 */
	public Ret reg(String userName, String password, String nickName, String ip) {
		if (StrKit.isBlank(userName) || StrKit.isBlank(password) || StrKit.isBlank(nickName)) {
			return Ret.fail("msg", "邮箱、密码或昵称不能为空");
		}

		userName = userName.toLowerCase().trim();	// 邮件全部存为小写
		password = password.trim();
		nickName = nickName.trim();

		if (nickName.contains("@") || nickName.contains("＠")) { // 全角半角都要判断
			return Ret.fail("msg", "昵称不能包含 \"@\" 字符");
		}
		if (nickName.contains(" ") || nickName.contains("　")) { // 检测是否包含半角或全角空格
			return Ret.fail("msg", "昵称不能包含空格");
		}
		if (isNickNameExists(nickName)) {
			return Ret.fail("msg", "昵称已被注册，请换一个昵称");
		}
		if (isUserNameExists(userName)) {
			return Ret.fail("msg", "邮箱已被注册，如果忘记密码，可以使用密码找回功能");
		}

		// 密码加盐 hash
		String salt = HashKit.generateSaltForSha256();
		password = HashKit.sha256(salt + password);

		// 创建账户
		Account account = new Account();
		account.setUserName(userName);
		account.setPassword(password);
		account.setSalt(salt);
		account.setNickName(nickName);
		account.setStatus(Account.STATUS_REG);
		account.setCreateAt(new Date());
		account.setIp(ip);
		account.setAvatar(Account.AVATAR_NO_AVATAR);  // 注册时设置默认头像

		if (account.save()) {
			String authCode =  AuthCodeService.me.createRegActivateAuthCode(account.getInt("id"));
			if (sendRegActivateAuthEmail(authCode, account)) {
				return Ret.ok("msg", "注册成功，激活邮件已发送，请查收并激活账号：" + userName);
			} else {
				return Ret.fail("msg", "注册成功，但是激活邮件发送失败，可能是邮件服务器出现故障，请去JFinal官方QQ群留言给群主，多谢！");
			}
		} else {
			return Ret.fail("msg", "注册失败，account 保存失败，请告知管理员");
		}
	}

	/**
	 * 发送账号激活授权邮件
	 */
	private boolean sendRegActivateAuthEmail(String authCode, Account reg) {
		String title = "JFinal 会员激活邮件";
		String content = "在浏览器地址栏里输入并访问下面激活链接即可完成账户激活：\n\n"
				+ " http://www.jfinal.com/reg/activate?authCode="
				+ authCode;

		String emailServer = PropKit.get("emailServer");
		String fromEmail = PropKit.get("fromEmail");
		String emailPass = PropKit.get("emailPass");
		String toEmail = reg.getStr("userName");
		try {
			EmailKit.sendEmail(emailServer, fromEmail, emailPass, toEmail, title, content);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 激活账号，返回 false 表示激活码已过期或者不存在
	 * 	激活账号不要去自动登录，激活邮件如果发错到了别人的邮箱，会有别人冒用的可能
	 * 并且登录功能还有额外有选择过期时间的功能
	 */
	public Ret activate(String authCodeId) {
		AuthCode authCode = AuthCodeService.me.getAuthCode(authCodeId);
		if (authCode != null && authCode.isValidRegActivateAuthCode()) {
			// 更新账户状态为已激活， status 的 where 条件必须为 reg，以防被锁定账户重新激活
			int n = Db.update("update account set status = ? where id = ? and status = ?", Account.STATUS_OK, authCode.get("accountId"), Account.STATUS_REG);
			if (n > 0) {
                sendWelcomeMessage(authCode.getInt("accountId"));
				return Ret.ok("msg", "账号激活成功，欢迎加入 JFinal 极速开发社区！");
			} else {
				return Ret.fail("msg", "未找到需要激活的账号，可能是账号已经激活或已经被锁定，请联系管理员");
			}
		} else {
			return Ret.fail("msg", "authCode 不存在或已经失效，可以尝试在登录页再次发送激活邮件");
		}
	}

    /**
     * 激活成功后立即发送欢迎系统私信
     */
    private void sendWelcomeMessage(Integer accountId) {
        try {   // try catch 确保主流程一定成功
            String sysMsg =
                    "您好，我是 JFinal 极速开发社区站长 James，非常欢迎您的加入。" +
                    "<br/><br/>JFinal 社区是一个专注于极速开发的分享、交流平台，" +
                    "社区将提供高品质、专业化的极速开发项目、以及项目的分享与反馈，极大提升开发效率与代码质量。" +
                    "<br/><br/>我们倡议：所有会员使用真实头像！";
            MessageService.me.sendSystemMessage(1, accountId, sysMsg);
        } catch (Exception e) {
            LogKit.error("发送激活欢迎系统消息异常：" + e.getMessage(), e);
		}
	}

	public Ret reSendActivateEmail(String userName) {
		if (StrKit.isBlank(userName) || userName.indexOf('@') == -1) {
			return Ret.fail("msg", "email 格式不正确，请重新输入");
		}
		userName = userName.toLowerCase().trim();   // email 转成小写
		if ( ! isUserNameExists(userName)) {
			return Ret.fail("msg", "email 没有被注册，无法收取激活邮件，请先去注册");
		}

		// 根据 userName 查找未激活的账户：Account.STATUS_REG
		Account account = accountDao.findFirst("select * from account where userName=? and status = ? limit 1", userName, Account.STATUS_REG);
		if (account == null) {
			return Ret.fail("msg", "该账户已经激活，可以直接登录");
		}

		String authCode = AuthCodeService.me.createRegActivateAuthCode(account.getId());
		if (sendRegActivateAuthEmail(authCode, account)) {
			return Ret.ok("msg", "激活码已发送至邮箱，请收取激活邮件并进行激活");
		} else {
			return Ret.fail("msg", "激活邮件发送失败，可能是邮件服务器出现故障，请去JFinal官方QQ群留言给群主，多谢！");
		}
	}
}

