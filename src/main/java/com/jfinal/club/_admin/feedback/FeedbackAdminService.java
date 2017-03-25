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
import com.jfinal.club.common.model.FeedbackReply;
import com.jfinal.club.my.feedback.MyFeedbackService;
import java.util.List;

/**
 * feedback 管理业务
 * TODO FeedbackAdminController 中的业务逻辑后续版本挪到这里来
 */
public class FeedbackAdminService {

	public static final FeedbackAdminService me = new FeedbackAdminService();

	/**
	 * 删除 feedback
	 */
	public void delete(int feedbackId) {
		Integer accountId = Db.queryInt("select accountId from feedback where id=? limit 1", feedbackId);
		if (accountId != null) {
			MyFeedbackService.me.delete(accountId, feedbackId);
		}
	}

	/**
	 * 获取 reply list
	 */
	public List<FeedbackReply> getReplyList(int feedbackId) {
		return new FeedbackReply().find("select * from feedback_reply where feedbackId=? order by id desc", feedbackId);
	}

	/**
	 * 删除 feedback reply
	 */
	public void deleteReply(int feedbackReplyId) {
		Integer accountId = Db.queryInt("select accountId from feedback_reply where id=? limit 1", feedbackReplyId);
		if (accountId != null) {
			MyFeedbackService.me.deleteFeedbackReplyById(accountId, feedbackReplyId);
		}
	}
}
