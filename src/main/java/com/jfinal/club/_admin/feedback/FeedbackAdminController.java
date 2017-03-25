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

package com.jfinal.club._admin.feedback;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.club.common.controller.BaseController;
import com.jfinal.kit.Ret;
import com.jfinal.club.common.model.Feedback;
import com.jfinal.club.common.model.FeedbackReply;
import com.jfinal.club.feedback.FeedbackService;
import com.jfinal.club.index.IndexService;
import java.util.List;

/**
 * 反馈管理控制器
 * 
 * 注意：sql 语句与业务逻辑要写在业务层，在此仅由于时间仓促偷懒的做法
 *     后续版本会改掉这样的用法，请小伙伴们不要效仿
 */
public class FeedbackAdminController extends BaseController {

	static FeedbackAdminService feedbackAdminSrv = FeedbackAdminService.me;
	static Feedback dao = new Feedback().dao();

	public void index() {
		Page<Feedback> feedbackPage = dao.paginate(getParaToInt("p", 1), 10, "select *", "from feedback order by createAt desc");
		setAttr("feedbackPage", feedbackPage);
		render("index.html");
	}

	/**
	 * 屏蔽贴子，目前先做成使用举报量 report 值屏蔽
	 */
	public void block() {
		int feedbackId = getParaToInt("id");
		// 让 report 加上一个足够被屏蔽的值即可
		Db.update("update feedback set report = report + ? where id=?", Feedback.REPORT_BLOCK_NUM, feedbackId);
		
		FeedbackService.me.clearHotFeedbackCache();	// 清缓存
		IndexService.me.clearCache();
		renderJson(Ret.ok("msg", "屏蔽贴子成功"));
	}

	/**
	 * 贴子解除屏蔽
	 */
	public void unblock() {
		int feedbackId = getParaToInt("id");
		Db.update("update feedback set report = 0 where id=?", feedbackId);
		
		FeedbackService.me.clearHotFeedbackCache();		// 清缓存
		IndexService.me.clearCache();
		renderJson(Ret.ok("msg", "贴子解除屏蔽成功"));
	}

	/**
	 * 删除 feedback
	 */
	public void delete() {
		feedbackAdminSrv.delete(getParaToInt("id"));
		renderJson(Ret.ok("msg", "feedback 删除成功"));
	}

	/**
	 * 显示 feedback reply 列表
	 */
	public void replyList() {
		int feedbackId = getParaToInt("feedbackId");
		List<FeedbackReply> feedbackReplyList = feedbackAdminSrv.getReplyList(feedbackId);
		setAttr("feedbackReplyList", feedbackReplyList);
		setAttr("feedbackId", feedbackId);
		render("reply.html");
	}

	/**
	 * 删除 feedback reply
	 */
	public void deleteReply() {
		int replyId = getParaToInt("replyId");
		feedbackAdminSrv.deleteReply(replyId);
		renderJson(Ret.ok("msg", "feedback reply 删除成功"));
	}
}



