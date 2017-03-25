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

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.club.common.kit.SqlKit;
import com.jfinal.club.common.model.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 提到我业务，分为 @我 以及评论/回复我
 */
public class ReferMeService {

	public static final ReferMeService me = new ReferMeService();

	final ReferMe dao = new ReferMe().dao();
	final NewsFeed newsFeedDao = new NewsFeed().dao();
	final Project projectDao = new Project().dao();
	final Share shareDao = new Share().dao();
	final Feedback feedbackDao = new Feedback().dao();

	final String referMePageCacheName = "referMePage";
	final int pageSize = 15;

	/**
	 * 创建 project 保存时的 refer_me 以及 remind 记录，project_reply 暂时没有考虑过要创建
	 */
	public void createProjectReferMe(List<Integer> referAccounts, int newsFeedId, Project project) {
		// 针对 project.content 中的 @我 生成 refer_me
		createReferMe(referAccounts, newsFeedId, ReferMe.TYPE_AT_ME);

		clearCache();
	}

	/**
	 * 创建分享保存时的 refer_me 以及 remind 记录
	 */
	public void createShareReferMe(List<Integer> referAccounts, int newsFeedId, Share share) {
		// 针对 share.content 中的 @我 生成 refer_me
		createReferMe(referAccounts, newsFeedId, ReferMe.TYPE_AT_ME);

		// 针对 share 所对应的 project，向 project 的作者生成 refer_me
		Project project = projectDao.findById(share.getProjectId());
		// 当 share 作者与 project 作者不是同一个人的时候才创建 refer_me 记录
		if (project != null && project.getAccountId() != share.getAccountId().intValue()
				&& !referAccounts.contains(project.getAccountId())) {     // 如果生成了 at_me 类型记录，那么也不再生成 comment_me 记录了
			createReferMe(project.getAccountId(), newsFeedId, ReferMe.TYPE_SHARE_REF_MY_PROJECT);
		}

		clearCache();
	}

	/**
	 * 创建反馈保存时的 refer_me 以及 remind 记录
	 */
	public void createFeedbackReferMe(List<Integer> referAccounts, int newsFeedId, Feedback feedback) {
		// 针对 feedback.content 中的 @我 生成 refer_me
		createReferMe(referAccounts, newsFeedId, ReferMe.TYPE_AT_ME);

		// 针对 feedback 所对应的 project，向 project 的作者生成 refer_me
		Project project = projectDao.findById(feedback.getProjectId());
		// 当 share 作者与 project 作者不是同一个人的时候才创建 refer_me 记录
		if (project != null && project.getAccountId() != feedback.getAccountId().intValue()
				&& !referAccounts.contains(project.getAccountId())) {     // 如果生成了 at_me 类型记录，那么也不再生成 comment_me 记录了
			createReferMe(project.getAccountId(), newsFeedId, ReferMe.TYPE_FEEDBACK_TO_MY_PROJECT);
		}

		clearCache();
	}

	/**
	 * 创建 share reply 操作的 refer_me 记录，类型为 at_me
	 * 同时还会创建 reply 所对应 article 作者的 refer_me 记录，类型为 comment_me
	 * 注意：所有 refer_me 相关的 remind 也会被创建
	 *  @param referAccounts 需要生成 refer_me 记录的目标账号 id
	 *  @param newsFeedId 动态 id
	 *  @param shareId 该 ShareReply 所对应的 Share 对象的 id
	 *  @param replyAccountId 该 ShareReply 的创建者
	 */
	public void createShareReplyReferMe(List<Integer> referAccounts, int newsFeedId, int shareId, int replyAccountId) {
		// 针对 reply.content 中的 @我 生成 refer_me
		createReferMe(referAccounts, newsFeedId, ReferMe.TYPE_AT_ME);

		// 针对 reply 所对应的 article，向 article 的作者生成 comment_me 型的 refer_me
		Share share = shareDao.findById(shareId);
		// 当 shareReply 作者与 share 作者不是同一个人的时候才创建 refer_me 记录
		if (share != null && share.getAccountId() != replyAccountId
				&& !referAccounts.contains(share.getAccountId())) {     // 如果生成了 at_me 类型记录，那么也不再生成 comment_me 记录了
			createReferMe(share.getAccountId(), newsFeedId, ReferMe.TYPE_COMMENT_ME);
		}

		clearCache();
	}

	private void createReferMe(List<Integer> referAccounts, int newsFeedId, int type) {
		for (Integer referAccountId : referAccounts) {
			createReferMe(referAccountId, newsFeedId, type);
		}
	}

	/**
	 * 生成一条 refer_me，同时也生成一条 remind 提醒用户
	 */
	private void createReferMe(int referAccountId, int newsFeedId, int type) {
		ReferMe rm = new ReferMe();
		rm.setReferAccountId(referAccountId);       // 接收者
		rm.setNewsFeedId(newsFeedId);                 // newsFeedId
		rm.setType(type);                                        // @我 类型的 referType
		rm.setCreateAt(new Date());
		rm.save();

		// 每一条 refer_me 都创建 remind 提醒
		RemindService.me.createRemindOfReferMe(referAccountId);
	}

	/**
	 * 创建 feedback reply 操作的 refer_me 记录，类型为 at_me
	 */
	public void createFeedbackReplyReferMe(List<Integer> referAccounts, int newsFeedId, int feedbackId, int replyAccountId) {
		// 针对 reply.content 中的 @我 生成 refer_me
		createReferMe(referAccounts, newsFeedId, ReferMe.TYPE_AT_ME);

		// 针对 reply 所对应的 article，向 article 的作者生成 comment_me 型的 refer_me
		Feedback feedback = feedbackDao.findById(feedbackId);
		// 当 feedbackReply 作者与 feedback 作者不是同一个人的时候才创建 refer_me 记录
		if (feedback != null && feedback.getAccountId() != replyAccountId
				&&  !referAccounts.contains(feedback.getAccountId())) {   // 如果生成了 at_me 类型记录，那么也不再生成 comment_me 记录了
			createReferMe(feedback.getAccountId(), newsFeedId, ReferMe.TYPE_COMMENT_ME);
		}

		clearCache();
	}

	/**
	 * 个人空间模块的 @提到我 消息
	 */
	public Page<NewsFeed> paginate(int accountId, int pageNum) {
		String cacheKey = accountId + "_" + pageNum;
		Page<NewsFeed> newsFeedPage = CacheKit.get(referMePageCacheName, cacheKey);
		if (newsFeedPage == null) {
			// 先获取 refer_me 的数据
			String s = "select newsFeedId";
			String f = "from refer_me where referAccountId=? order by id desc";
			Page<ReferMe> referMePage = dao.paginate(pageNum, pageSize, s, f, accountId);
			if (referMePage.getList().size() == 0) {
				newsFeedPage =  new Page<NewsFeed>(new ArrayList<NewsFeed>(), pageNum, pageSize, 0, 0);
				CacheKit.put(referMePageCacheName, cacheKey, newsFeedPage);
				return newsFeedPage;
			}

            // TODO 将来用 AccountService.me.join(...) 代替 inner join 关联操作
			StringBuilder sql = new StringBuilder("select nf.*, a.avatar, a.nickName ");
			sql.append("from news_feed nf inner join account a on nf.accountId=a.id where nf.id in(");
			apppendNewsFeedIds(referMePage.getList(), sql);
			sql.append(") order by id desc");

			List<NewsFeed> newsFeedList = newsFeedDao.find(sql.toString());
			newsFeedPage = new Page<NewsFeed>(newsFeedList, pageNum, pageSize, referMePage.getTotalPage(), referMePage.getTotalRow());

			// 重用 NewsFeed 模块的 loadRefData 功能
			NewsFeedService.me.loadRefData(newsFeedPage);
			CacheKit.put(referMePageCacheName, cacheKey, newsFeedPage);
		}
		return newsFeedPage;
	}

	private void apppendNewsFeedIds(List<ReferMe> list, StringBuilder ret) {
		for (int i=0, size=list.size(); i<size; i++) {
			if (i > 0) {
				ret.append(", ");
			}
			ret.append(list.get(i).getNewsFeedId());
		}
	}

	/**
	 * 删除 news_feed 记录的同时，删掉其对应的 refer_me 记录
	 */
	public void deleteByNewsFeedIds(List<Integer> newsFeedIds) {
		if (newsFeedIds.size() > 0) {
			StringBuilder sql = new StringBuilder("delete from refer_me where newsFeedId in");
			SqlKit.joinIds(newsFeedIds, sql);
			Db.update(sql.toString());
			clearCache();
		}
	}

	public void clearCache() {
		CacheKit.removeAll(referMePageCacheName);
	}
}
