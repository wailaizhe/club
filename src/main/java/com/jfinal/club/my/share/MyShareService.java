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

package com.jfinal.club.my.share;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.club.common.kit.SqlKit;
import com.jfinal.club.common.model.Share;
import com.jfinal.club.index.IndexService;
import com.jfinal.club.my.favorite.FavoriteService;
import com.jfinal.club.my.like.LikeService;
import com.jfinal.club.my.newsfeed.NewsFeedService;
import com.jfinal.club.my.newsfeed.ReferMeKit;
import com.jfinal.club.share.ShareService;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * MyShareService
 */
public class MyShareService  {

	public static final MyShareService me = new MyShareService();
	private final Share dao = new Share().dao();

	public List<Share> findAll(int accountId) {
		return dao.find("select * from share where accountId=? order by createAt desc", accountId);
	}

	public Share findById(int accountId, int shareId) {
		return dao.findFirst("select * from share where accountId=? and id=?", accountId, shareId);
	}

	public Page<Share> paginate(int accountId, int pageNumber, int pageSize) {
		return dao.paginate(pageNumber, pageSize, "select * ", "from share where accountId=? order by createAt", accountId);
	}

	public void save(int accountId, Share share) {
		share.setAccountId(accountId);
		share.setCreateAt(new Date());
		share.setClickCount(0);
		List<Integer> referAccounts = ReferMeKit.buildAtMeLink(share);
		share.save();

		// 添加创建分享动态消息
		NewsFeedService.me.createShareNewsFeed(accountId, share, referAccounts);

		ShareService.me.clearHotShareCache();     // 清缓存，以后改成更好的方式
		IndexService.me.clearCache();
	}

	public void update(int accountId, Share share) {
		if (Db.queryInt("select accountId from share where id=? limit 1", share.getId()) != accountId) {
			throw new RuntimeException("个人空间只能操作属于自己的分享");
		}
		share.update();

		ShareService.me.clearHotShareCache();     // 清缓存，以后改成更好的方式
		IndexService.me.clearCache();
	}

	/**
	 * 删除 share 之前，先删除 news_feed，NewsFeedService 会自动删除相应的 refer_me
	 * 管理员调用该方法删除 share 时，需要先获取一下该 share 所对应的 accountId，只有这个对上了，才可以删除
	 */
	public void delete(final int accountId, final int shareId) {
		Db.tx(new IAtom() {
			public boolean run() throws SQLException {
				// 先删除所对应的 share_reply 列表
				List<Integer> shareReplyIdList = Db.query("select id from share_reply where shareId=?", shareId);
				deleteShareReplyByIdList(shareReplyIdList);

				// 再删除 news_feed
				NewsFeedService.me.deleteByShareId(shareId);

                // 再删除收藏、点赞数据
                FavoriteService.me.deleteByShareDeleted(shareId);
                LikeService.me.deleteByShareDeleted(shareId);

				// 最后删除 share
				return Db.update("delete from share where accountId=? and id=?", accountId, shareId) > 0;
			}
		});

		IndexService.me.clearCache();
	}

	/**
	 * 删除 share 之下的所有 share_reply，先删除 news_feed
	 */
	private void deleteShareReplyByIdList(List<Integer> shareReplyIdList) {
		// 先删除 news_feed
		NewsFeedService.me.deleteByShareReplyIdList(shareReplyIdList);

		// 再删除 share_reply
		if (shareReplyIdList.size() > 0) {  // idList 有可能为空，不加判断会出异常
			StringBuilder sql = new StringBuilder("delete from share_reply where id in");
			SqlKit.joinIds(shareReplyIdList, sql);
			Db.update(sql.toString());
		}
	}

	/**
	 * 管理员调用该方法删除 share_reply 时，需要先获取一下该 share_reply 所对应的 accountId，只有这个对上了，才可以删除
	 */
	public void deleteShareReplyById(final int accountId, final int shareReplyId) {
		Db.tx(new IAtom() {
			public boolean run() throws SQLException {
				// 先删除 news_feed
				NewsFeedService.me.deleteByShareReplyId(shareReplyId);

				// 再删除 share_reply
				return Db.update("delete from share_reply where accountId=? and id=?", accountId, shareReplyId) > 0;
			}
		});
	}
}

