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

package com.jfinal.club._admin.share;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.club.common.controller.BaseController;
import com.jfinal.kit.Ret;
import com.jfinal.club.common.model.Share;
import com.jfinal.club.common.model.ShareReply;
import com.jfinal.club.index.IndexService;
import com.jfinal.club.share.ShareService;
import java.util.List;

/**
 * 分享管理控制器
 * 
 * 注意：sql 语句与业务逻辑要写在业务层，在此仅由于时间仓促偷懒的做法
 *     后续版本会改掉这样的用法，请小伙伴们不要效仿
 */
public class ShareAdminController extends BaseController {

	static ShareAdminService shareAdminSrv = ShareAdminService.me;
	static Share dao = new Share().dao();

	public void index() {
		Page<Share> sharePage = dao.paginate(getParaToInt("p", 1), 10, "select *", "from share order by createAt desc");
		setAttr("sharePage", sharePage);
		render("index.html");
	}

	/**
	 * 屏蔽贴子，目前先做成
	 */
	public void block() {
		int shareId = getParaToInt("id");
		Db.update("update share set report = report + ? where id=?", Share.REPORT_BLOCK_NUM, shareId);
		
		ShareService.me.clearHotShareCache();
		IndexService.me.clearCache();
		renderJson(Ret.ok("msg", "屏蔽贴子成功"));
	}

	/**
	 * 贴子解除屏蔽
	 */
	public void unblock() {
		int shareId = getParaToInt("id");
		Db.update("update share set report = 0 where id=?", shareId);
		
		ShareService.me.clearHotShareCache();
		IndexService.me.clearCache();
		renderJson(Ret.ok("msg", "贴子解除屏蔽成功"));
	}

	/**
	 * 删除 share
	 */
	public void delete() {
		shareAdminSrv.delete(getParaToInt("id"));
		renderJson(Ret.ok("msg", "share 删除成功"));
	}

	/**
	 * 显示 share reply 列表
	 */
	public void replyList() {
		int shareId = getParaToInt("shareId");
		List<ShareReply> shareReplyList = shareAdminSrv.getReplyList(shareId);
		setAttr("shareReplyList", shareReplyList);
		setAttr("shareId", shareId);
		render("reply.html");
	}

	/**
	 * 删除 share reply
	 */
	public void deleteReply() {
		int replyId = getParaToInt("replyId");
		shareAdminSrv.deleteReply(replyId);
		renderJson(Ret.ok("msg", "share reply 删除成功"));
	}
}



