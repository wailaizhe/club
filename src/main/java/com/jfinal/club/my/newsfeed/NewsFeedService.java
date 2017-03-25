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
import com.jfinal.club.common.account.AccountService;
import com.jfinal.club.common.kit.SqlKit;
import com.jfinal.club.common.model.*;
import java.util.Date;
import java.util.List;

/**
 * news feed 业务
 */
public class NewsFeedService {

	public static final NewsFeedService me = new NewsFeedService();
	final String newsFeedPageCacheName = "newsFeedPage";
	final int pageSize = 15;
	final NewsFeed dao = new NewsFeed().dao();
	final Project projectDao = new Project().dao();
	final Share shareDao = new Share().dao();
	final ShareReply shareReplyDao = new ShareReply().dao();
	final Feedback feedbackDao = new Feedback().dao();
	final FeedbackReply feedbackReplyDao = new FeedbackReply().dao();

	/**
	 * 共用的分页查询
	 */
	private Page<NewsFeed> doPaginate(String cacheName, Object cacheKey, int pageNum, String select, String from, Object... paras) {
        Page<NewsFeed> newsFeedPage = dao.paginate(pageNum, pageSize, select, from, paras);
        AccountService.me.joinNickNameAndAvatar(newsFeedPage.getList());
        loadRefData(newsFeedPage);
        CacheKit.put(cacheName, cacheKey, newsFeedPage);
		return newsFeedPage;
	}

    /**
     * 个人空间模块的动态消息，显示自己以及所有关注用户的动态消息
     */
    public Page<NewsFeed> paginate(int accountId, int pageNum) {
        String cacheKey =  accountId + "_" + pageNum;
        Page<NewsFeed> newsFeedPage = CacheKit.get(newsFeedPageCacheName, cacheKey);
        if (newsFeedPage == null) {
            String select = "select nf.*";
            StringBuilder from = new StringBuilder()
                    .append("from ( ")
                    .append("       select ft.accountId, ft.friendId from friend ft union all (select ").append(accountId).append(", ").append(accountId).append(") ")
                    .append(") as f inner join news_feed nf on f.friendId=nf.accountId and f.accountId=? order by id desc");
            newsFeedPage = doPaginate(newsFeedPageCacheName, cacheKey, pageNum, select, from.toString(), accountId);
        }
        return newsFeedPage;
    }

    /**
     * 所有动态消息，不添加任何条件
     */
    Page<NewsFeed> paginateForAllNewsFeed(int pageNum) {
        String cacheKey = "all_" + pageNum;
        Page<NewsFeed> newsFeedPage = CacheKit.get(newsFeedPageCacheName, cacheKey);
        if (newsFeedPage == null) {
            String select = "select nf.*";
            String from = "from news_feed nf order by id desc";
            newsFeedPage = doPaginate(newsFeedPageCacheName, cacheKey, pageNum, select, from);
        }
        return newsFeedPage;
    }

	/**
	 * user 模块的动态消息，目前为止与个人空间模块的动态消息仅多一个 accountId 对 news_feed 表的查询条件
	 */
	public Page<NewsFeed> paginateForUserSpace(int accountId, int pageNum) {
		String cacheKey =  accountId + "_user_" + pageNum;
        Page<NewsFeed> newsFeedPage = CacheKit.get(newsFeedPageCacheName, cacheKey);
        if (newsFeedPage == null) {
            String select = "select nf.*";
            String from = "from news_feed nf where nf.accountId=? order by id desc";
            newsFeedPage = doPaginate(newsFeedPageCacheName, cacheKey, pageNum, select, from, accountId);
        }
        return newsFeedPage;
	}

	/**
	 * id
	 * accountId         发布该动态的用户
	 * refType             动态引用类型
	 * refId                 动态引用所关联的 id，与 refType 配合，可唯一确定是某个表中的某条记录
	 * refParentType   refId 对象的父对象，例如 share_reply 的父对象是 share。此字段貌似可以去掉，或许只是用于界面 ajax 动态显示时传参时方便，暂时保留
	 * refParentId       refId 对象的父对象的 id
	 * createAt
	 *
	 * 为了方便，页面展示被分成了三个 div：text、ref、refParent
	 * 1：text 用来展示 reply 动态的 reply.content值
	 * 2：ref 用来展示 article 动态时，显示一个超链接，由于article发布者就是动态发布者，所以没有采用微信图文形式展示方式
	 * 3：refParent 用来展示 reply 所关联的 article，采用微信图文消息的展现方式
	 * 此方案只是为了显示方便，字段间的逻辑关系，必须严格按照字段说明来理解，以免混淆
	 */
	void loadRefData(Page<NewsFeed> newsFeedPage) {
		List<NewsFeed> list = newsFeedPage.getList();
		for (NewsFeed nf : list) {
			if (nf.getRefType() == NewsFeed.REF_TYPE_PROJECT) {
				String sql = "select p.id, p.title, p.accountId from project p where p.id = ? limit 1";
				Project ref = projectDao.findFirst(sql, nf.getRefId());
				if (ref != null) {
					ref.put("href", "/project/" + ref.getId());
				}
				nf.put("ref", ref);
			}
			else if (nf.getRefType() == NewsFeed.REF_TYPE_PROJECT_REPLY) {
				throw new IllegalStateException("暂时没有 project reply 类型的动态，以后添加");
			}
			else if (nf.getRefType() == NewsFeed.REF_TYPE_SHARE) {
				String sql = "select s.id, s.title, s.accountId from share s where s.id = ? limit 1";
				Share ref = shareDao.findFirst(sql, nf.getRefId());
				if (ref != null) {
					ref.put("href", "/share/" + ref.getId());
				}
				nf.put("ref", ref);
			}
			else if (nf.getRefType() == NewsFeed.REF_TYPE_SHARE_REPLY) {
				String sql = "select sr.id, sr.content, sr.accountId, a.avatar from share_reply sr, account a where sr.id = ? and sr.accountId=a.id limit 1";
				ShareReply ref = shareReplyDao.findFirst(sql, nf.getRefId());
				if (ref != null) {
					nf.put("text", ref.getContent());
				}

				sql = "select s.id, s.title, s.accountId, a.avatar from share s, account a where s.id = ? and s.accountId=a.id limit 1";
				Share refParent  = shareDao.findFirst(sql, nf.getRefParentId());
				if (refParent != null) {
					refParent.put("href", "/share/" + refParent.getId());
				}
				nf.put("refParent", refParent);
			}
			else if (nf.getRefType() == NewsFeed.REF_TYPE_FEEDBACK) {
				String sql = "select f.id, f.title, f.accountId from feedback f where f.id = ? limit 1";
				Feedback ref = feedbackDao.findFirst(sql, nf.getRefId());
				if (ref != null) {
					ref.put("href", "/feedback/" + ref.getId());
				}
				nf.put("ref", ref);
			}
			else if (nf.getRefType() == NewsFeed.REF_TYPE_FEEDBACK_REPLY) {
				String sql = "select fr.id, fr.content, fr.accountId, a.avatar from feedback_reply fr, account a where fr.id = ? and fr.accountId=a.id limit 1";
				FeedbackReply ref = feedbackReplyDao.findFirst(sql, nf.getRefId());
				if (ref != null) {
					nf.put("text", ref.getContent());
				}

				sql = "select f.id, f.title, f.accountId, a.avatar from feedback f, account a where f.id = ? and f.accountId=a.id limit 1";
				Feedback refParent  = feedbackDao.findFirst(sql, nf.getRefParentId());
				if (refParent != null) {
					refParent.put("href", "/feedback/" + refParent.getId());
				}
				nf.put("refParent", refParent);
			}
			else {
				throw new IllegalStateException("错误的news_feed type 值：" + nf.getRefType());
			}
		}
	}

	/**
	 * 创建项目动态
	 */
	public void createProjectNewsFeed(int accountId, Project project, List<Integer> referAccounts) {
		NewsFeed nf = new NewsFeed();
		nf.setAccountId(accountId);
		nf.setRefType(NewsFeed.REF_TYPE_PROJECT);
		nf.setRefId(project.getId());
		nf.setCreateAt(new Date());
		nf.save();

		clearCache();

		// 添加项目 @提到我 消息，以及 remind 记录
		ReferMeService.me.createProjectReferMe(referAccounts, nf.getId(), project);
	}

	/**
	 * 创建项目回复动态，暂时不用
	public void createProjectReplyNewsFeed(int accountId, ProjectReply projectReply) {
	    clearCache();
	} */

	/**
	 * 创建分享动态
	 */
	public void createShareNewsFeed(int accountId, Share share, List<Integer> referAccounts) {
		NewsFeed nf = new NewsFeed();
		nf.setAccountId(accountId);
		nf.setRefType(NewsFeed.REF_TYPE_SHARE);
		nf.setRefId(share.getId());
		nf.setCreateAt(new Date());
		nf.save();

		clearCache();

		// 添加分享 @提到我 消息，以及 remind 记录
		ReferMeService.me.createShareReferMe(referAccounts, nf.getId(), share);
	}

	/**
	 * 创建分享回复动态
	 */
	public void createShareReplyNewsFeed(int accountId, ShareReply shareReply, List<Integer> referAccounts) {
		NewsFeed nf = new NewsFeed();
		nf.setAccountId(accountId);
		nf.setRefType(NewsFeed.REF_TYPE_SHARE_REPLY);
		nf.setRefId(shareReply.getId());
		nf.setRefParentType(NewsFeed.REF_TYPE_SHARE);
		nf.setRefParentId(shareReply.getShareId());
		nf.setCreateAt(new Date());
		nf.save();

		clearCache();

		// 添加分享回复 @提到我 消息，以及 remind 记录
		ReferMeService.me.createShareReplyReferMe(referAccounts, nf.getId(), shareReply.getShareId(), accountId);
	}

	/**
	 * 创建反馈动态
	 */
	public void createFeedbackNewsFeed(int accountId, Feedback feedback, List<Integer> referAccounts) {
		NewsFeed nf = new NewsFeed();
		nf.setAccountId(accountId);
		nf.setRefType(NewsFeed.REF_TYPE_FEEDBACK);
		nf.setRefId(feedback.getId());
		nf.setCreateAt(new Date());
		nf.save();

		clearCache();

		// 添加反馈 @提到我 消息，以及 remind 记录
		ReferMeService.me.createFeedbackReferMe(referAccounts, nf.getId(), feedback);
	}

	/**
	 * 创建反馈回复动态
	 */
	public void createFeedbackReplyNewsFeed(int accountId, FeedbackReply feedbackReply, List<Integer> referAccounts) {
		NewsFeed nf = new NewsFeed();
		nf.setAccountId(accountId);
		nf.setRefType(NewsFeed.REF_TYPE_FEEDBACK_REPLY);
		nf.setRefId(feedbackReply.getId());
		nf.setRefParentType(NewsFeed.REF_TYPE_FEEDBACK);
		nf.setRefParentId(feedbackReply.getFeedbackId());
		nf.setCreateAt(new Date());
		nf.save();

		clearCache();

		// 添加反馈回复 @提到我 消息，以及 remind 记录
		ReferMeService.me.createFeedbackReplyReferMe(referAccounts, nf.getId(), feedbackReply.getFeedbackId(), accountId);
	}

	/**
	 * 删除 news_feed，同时也要删除相应的 refer_me
	 * remind 不用处理，因为当前 remind 可能已经被查看，再次让 remind 减 1 则不合理
	 * @param refType 引用类型
	 * @param refIdList 引用目标对象的 id 列表
	 */
	private void deleteByRef(int refType, List<Integer> refIdList) {
		if (refType == NewsFeed.REF_TYPE_PROJECT_REPLY) {
			throw new IllegalArgumentException("暂不支持 NewsFeed.REF_TYPE_PROJECT_REPLY 类型");
		}
		if (refType < NewsFeed.REF_TYPE_PROJECT || refType > NewsFeed.REF_TYPE_FEEDBACK_REPLY ) {
			throw new IllegalArgumentException("refType 不正确：" + refType);
		}
		// 解决在没有 reply 的 article 时抛异常的问题，删除 article 本身的 news_feed 时 refIdList.size() 等于 1
		if (refIdList == null || refIdList.size() == 0) {
			return ;
		}

		// 删掉 refer_me 记录，每一条 refer_me 记录都对应着一个 newsFeedId
		StringBuilder where = new StringBuilder(" where refType=? and refId in ");
		SqlKit.joinIds(refIdList, where);
		List<Integer> newsFeedIds = Db.query("select id from news_feed" + where, refType);
		ReferMeService.me.deleteByNewsFeedIds(newsFeedIds);

		// 删掉 news_feed 记录
		Db.update("delete from news_feed" + where, refType);
		clearCache();
	}

	private List<Integer> wrapToIdList(int id) {
		List<Integer> ret = new java.util.ArrayList<Integer>();
		ret.add(id);
		return ret;
	}

	/**
	 * 删除 project 所关联的 news_feed
	 */
	public void deleteByProjectId(int projectId) {
		deleteByRef(NewsFeed.REF_TYPE_PROJECT, wrapToIdList(projectId));
	}

	/**
	 * 删除 share 所关联的 news_feed
	 */
	public void deleteByShareId(int shareId) {
		deleteByRef(NewsFeed.REF_TYPE_SHARE, wrapToIdList(shareId));
	}

	/**
	 * 删除 share_reply 所关联的 news_feed
	 */
	public void deleteByShareReplyId(int shareReplyId) {
		deleteByRef(NewsFeed.REF_TYPE_SHARE_REPLY, wrapToIdList(shareReplyId));
	}

	/**
	 * 批量删除 share_reply 所关联的 news_feed
	 */
	public void deleteByShareReplyIdList(List<Integer> shareReplyIdList) {
		deleteByRef(NewsFeed.REF_TYPE_SHARE_REPLY, shareReplyIdList);
	}

	/**
	 * 删除 feedback 所关联的 news_feed
	 */
	public void deleteByFeedbackId(int feedbackId) {
		deleteByRef(NewsFeed.REF_TYPE_FEEDBACK, wrapToIdList(feedbackId));
	}

	/**
	 * 删除 feedback_reply 所关联的 news_feed
	 */
	public void deleteByFeedbackReplyId(int feedbackReplyId) {
		deleteByRef(NewsFeed.REF_TYPE_FEEDBACK_REPLY, wrapToIdList(feedbackReplyId));
	}

	/**
	 * 批量删除 feedback_reply 所关联的 news_feed
	 */
	public void deleteByFeedbackReplyIdList(List<Integer> feedbackReplyIdList) {
		deleteByRef(NewsFeed.REF_TYPE_FEEDBACK_REPLY, feedbackReplyIdList);
	}

	public void clearCache() {
		CacheKit.removeAll(newsFeedPageCacheName);
	}
}
