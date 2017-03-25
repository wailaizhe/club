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

package com.jfinal.club.my.newsfeed;

import com.jfinal.club.common.account.AccountService;
import com.jfinal.kit.Ret;
import com.jfinal.club.common.model.FeedbackReply;
import com.jfinal.club.common.model.NewsFeed;
import com.jfinal.club.common.model.ShareReply;
import com.jfinal.club.feedback.FeedbackService;
import com.jfinal.club.share.ShareService;
import java.util.ArrayList;
import java.util.List;

/**
 * 动态回复业务
 */
public class NewsFeedReplyService {

	public static final NewsFeedReplyService me = new NewsFeedReplyService();
	NewsFeed newsFeedDao =  new NewsFeed().dao();
	ShareReply shareReplyDao = new ShareReply().dao();
	FeedbackReply feedbackReplyDao = new FeedbackReply().dao();

	/**
	 * 返回的数据根据 refType 指向 share_reply、feedback_reply
	 */
	public Ret getNewsFeedReplyList(int newsfeedId) {
		NewsFeed nf = newsFeedDao.findById(newsfeedId);

		List list;
		String url;
		// 获取 project_reply 列表，不存在 refParent
		if (nf.isRefTypeProject()) {
			list = new ArrayList(0);
			url = "/project/" + nf.getRefId();
		}
		// 获取 project_reply 列表，存在 refParent
		else if (nf.isRefTypeProjectReply()) {
			list = new ArrayList(0);
			url = "/project/" + nf.getRefParentId();
			throw new RuntimeException("不支持 project_reply");
		}

		// 获取 share_reply 列表，不存在 refParent
		else if (nf.isRefTypeShare()) {
			// 将来做缓存的话，就要将此查询代码放入 ShareSerivce 中去。没做缓存可以谅解写在此处，因为缓存由各自的 service 管理
			list = shareReplyDao.find("select * from share_reply where shareId=? order by id desc limit 10", nf.getRefId());
			url = "/share/" + nf.getRefId();
		}
		// 获取 share_reply 列表，存在 refParent
		else if (nf.isRefTypeShareReply()) {
			list = shareReplyDao.find("select * from share_reply where shareId=? order by id desc limit 10", nf.getRefParentId());
			url = "/share/" + nf.getRefParentId();
		}

		// 获取 feedback_reply 列表，不存在 refParent
		else if (nf.isRefTypeFeedback()) {
			list = feedbackReplyDao.find("select * from feedback_reply where feedbackId=? order by id desc limit 10", nf.getRefId());
			url = "/feedback/" + nf.getRefId();
		}
		// 获取 feedback_reply 列表，存在 refParent
		else if (nf.isRefTypeFeedbackReply()) {
			list = feedbackReplyDao.find("select * from feedback_reply where feedbackId=? order by id desc limit 10", nf.getRefParentId());
			url = "/feedback/" + nf.getRefParentId();
		}

		else {
			throw new RuntimeException(" news_feed.refType 不正确，当前 refType 值为：" + nf.getRefType());
		}

		// 将 account 表中的 nickName、avatar 属性 join 过来
		AccountService.me.joinNickNameAndAvatar(list);
		return Ret.create("replyList", list).set("showAllReplyUrl", url);
	}

	/**
	 * 保存 news feed 列表中的回复到相应的表中去
	 * 通过 refType 与 refParentType 转调 ProjectService、
	 * ShareService、FeedbackService 相应的 saveReply(...) 方法
	 */
	public Ret saveReply(int newsFeedId, int accountId, String content) {
		NewsFeed nf = newsFeedDao.findById(newsFeedId);

		if (nf.isRefTypeProject()) {
			int projectId = nf.getRefId();
			throw new RuntimeException("项目暂时不支持回复功能，等 share、feedbac 的回复功测试完成后，立即开发");
		} else if (nf.isRefTypeProjectReply()) {
			int projectId = nf.getRefParentId();
			throw new RuntimeException("项目暂时不支持回复功能，等 share、feedbac 的回复功测试完成后，立即开发");
		} else if (nf.isRefTypeShare()) {
			int shareId = nf.getRefId();
			return ShareService.me.saveReply(shareId, accountId, content);
		} else if (nf.isRefTypeShareReply()) {
			int shareId = nf.getRefParentId();
			return ShareService.me.saveReply(shareId, accountId, content);
		} else if (nf.isRefTypeFeedback()) {
			int feedbackId = nf.getRefId();
			return FeedbackService.me.saveReply(feedbackId, accountId, content);
		} else if (nf.isRefTypeFeedbackReply()) {
			int feedbackId = nf.getRefParentId();
			return FeedbackService.me.saveReply(feedbackId, accountId, content);
		} else {
			throw new RuntimeException(" news_feed.refType 不正确，当前 refType 值为：" + nf.getRefType());
		}
	}
}


