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

package com.jfinal.club._admin.account;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.club.common.controller.BaseController;
import com.jfinal.kit.Ret;
import com.jfinal.club.common.model.Account;
import com.jfinal.club.common.model.Session;
import com.jfinal.club.login.LoginService;
import java.util.List;

/**
 * 账户管理
 * 
 * 注意：sql 语句与业务逻辑要写在业务层，在此仅由于时间仓促偷懒的做法
 *     后续版本会改掉这样的用法，请小伙伴们不要效仿
 */
public class AccountAdminController extends BaseController {

	static Account dao = new Account().dao();

	public void index() {
		Page<Account> accountPage = dao.paginate(getParaToInt("p", 1), 100, "select *", "from account order by createAt desc");
		setAttr("accountPage", accountPage);
		render("index.html");
	}

	public void edit() {
		Account account = dao.findById(getParaToInt("id"));
		setAttr("account", account);
		render("fancy_editor.html");
	}

	/**
	 * 账户锁定
	 */
	public void lock() {
		int accountId = getParaToInt("id");
		// 暂时不允许屏蔽对 10 以内 id 账号，这些是管理员账号，以免误操作
		if (accountId <= 10) {
			renderJson(Ret.fail("msg", "目前不允许屏蔽管理员账号"));
			return ;
		}

		int n = Db.update("update account set status = ? where id=?", Account.STATUS_LOCK_ID, accountId);

		// 锁定后，强制退出登录，避免继续搞破坏
		List<Session> sessionList = Session.dao.find("select * from session where accountId = ?", accountId);
		if (sessionList != null) {
			for (Session session : sessionList) {			// 处理多客户端同时登录后的多 session 记录
				LoginService.me.logout(session.getId());    // 清除登录 cache，强制退出
			}
		}

		if (n > 0) {
			renderJson(Ret.ok("msg", "锁定成功"));
		} else {
			renderJson(Ret.fail("msg", "锁定失败"));
		}
	}

	/**
	 * 账户解锁
	 */
	public void unlock() {
		int accountId = getParaToInt("id");
		// 如果账户未激活，则不能被解锁
		int n = Db.update("update account set status = ? where status != ? and id = ?", Account.STATUS_OK , Account.STATUS_REG , accountId);
		Db.update("delete from session where accountId = ?", accountId);
		if (n > 0) {
			renderJson(Ret.ok("msg", "解锁成功"));
		} else {
			renderJson(Ret.fail("msg", "解锁失败，可能是账户未激活，请查看账户详情"));
		}
	}
}
