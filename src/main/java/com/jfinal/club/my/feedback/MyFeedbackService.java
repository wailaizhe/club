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

package com.jfinal.club.my.feedback;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.club.common.kit.SqlKit;
import com.jfinal.club.common.model.Feedback;
import com.jfinal.club.feedback.FeedbackService;
import com.jfinal.club.index.IndexService;
import com.jfinal.club.my.favorite.FavoriteService;
import com.jfinal.club.my.like.LikeService;
import com.jfinal.club.my.newsfeed.NewsFeedService;
import com.jfinal.club.my.newsfeed.ReferMeKit;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * MyFeedbackService
 */
public class MyFeedbackService  {

	public static final MyFeedbackService me = new MyFeedbackService();
	private final Feedback dao = new Feedback().dao();

	public List<Feedback> findAll(int accountId) {
		return dao.find("select * from feedback where accountId=? order by createAt desc", accountId);
	}

	public Feedback findById(int accountId, int feedbackId) {
		return dao.findFirst("select * from feedback where accountId=? and id=?", accountId, feedbackId);
	}

	public Page<Feedback> paginate(int accountId, int pageNumber, int pageSize) {
		return dao.paginate(pageNumber, pageSize, "select * ", "from feedback where accountId=? order by createAt", accountId);
	}

	public void save(int accountId, Feedback feedback) {
		feedback.setAccountId(accountId);
		feedback.setCreateAt(new Date());
		feedback.setClickCount(0);
		List<Integer> referAccounts = ReferMeKit.buildAtMeLink(feedback);
		feedback.save();

		// 添加创建反馈动态消息
		NewsFeedService.me.createFeedbackNewsFeed(accountId, feedback, referAccounts);

		FeedbackService.me.clearHotFeedbackCache();     // 清缓存，以后改成更好的方式
		IndexService.me.clearCache();
	}

	public void update(int accountId, Feedback feedback) {
		if (Db.queryInt("select accountId from feedback where id=? limit 1", feedback.getId()) != accountId) {
			throw new RuntimeException("个人空间只能操作属于自己的反馈");
		}
		feedback.update();

		FeedbackService.me.clearHotFeedbackCache();     // 清缓存，以后改成更好的方式
		IndexService.me.clearCache();
	}

	/**
	 * 删除 feedback 之前，先删除 news_feed，NewsFeedService 会自动删除相应的 refer_me
	 * 管理员调用该方法删除 feedback 时，需要先获取一下该 feedback 所对应的 accountId，只有这个对上了，才可以删除
	 */
	public void delete(final int accountId, final int feedbackId) {
		Db.tx(new IAtom() {
			public boolean run() throws SQLException {
				// 先删除所对应的 feedback_reply 列表
				List<Integer> feedbackReplyIdList = Db.query("select id from feedback_reply where feedbackId=?", feedbackId);
				deleteFeedbackReplyByIdList(feedbackReplyIdList);

				// 再删除 news_feed
				NewsFeedService.me.deleteByFeedbackId(feedbackId);

                // 再删除收藏、点赞数据
                FavoriteService.me.deleteByFeedbackDeleted(feedbackId);
                LikeService.me.deleteByFeedbackDeleted(feedbackId);

				// 最后删除 feedback
				return Db.update("delete from feedback where accountId=? and id=?", accountId, feedbackId) > 0;
			}
		});

		IndexService.me.clearCache();
	}

	/**
	 * 删除 feedback 之下的所有 feedback_reply，先删除 news_feed
	 */
	private void deleteFeedbackReplyByIdList(List<Integer> feedbackReplyIdList) {
		// 先删除 news_feed
		NewsFeedService.me.deleteByFeedbackReplyIdList(feedbackReplyIdList);

		// 再删除 feedback_reply
		if (feedbackReplyIdList.size() > 0) {  // idList 有可能为空，不加判断会出异常
			StringBuilder sql = new StringBuilder("delete from feedback_reply where id in");
			SqlKit.joinIds(feedbackReplyIdList, sql);
			Db.update(sql.toString());
		}
	}

	/**
	 * 管理员调用该方法删除 feedback_reply 时，需要先获取一下该 feedback_reply 所对应的 accountId，只有这个对上了，才可以删除
	 */
	public void deleteFeedbackReplyById(final int accountId, final int feedbackReplyId) {
		Db.tx(new IAtom() {
			public boolean run() throws SQLException {
				// 先删除 news_feed
				NewsFeedService.me.deleteByFeedbackReplyId(feedbackReplyId);

				// 再删除 feedback_reply
				return Db.update("delete from feedback_reply where accountId=? and id=?", accountId, feedbackReplyId) > 0;
			}
		});
	}
}