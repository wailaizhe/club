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

package com.jfinal.club.my.friend;

import com.jfinal.plugin.activerecord.ActiveRecordException;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.kit.Ret;
import com.jfinal.club.common.model.Account;
import com.jfinal.club.my.newsfeed.NewsFeedService;
import com.jfinal.club.my.newsfeed.RemindService;
import com.jfinal.club.user.friend.UserFriendService;
import java.util.Date;
import java.util.List;

/**
 * FriendService
 */
public class MyFriendService {

	public static final MyFriendService me = new MyFriendService();
    final String myFollowCacheName = "myFollowList";
    final String myFansCacheName = "myFansList";
    final String followAndFansTotalCacheName = "followAndFansTotal";
    final Account accountDao = new Account().dao();
    final int pageSize = 15;

	/**
	 * 获取关注列表
	 * 暂时不使用 AccountService.join(...)
	 * isMyFans 用来表示 A 关注 B 时，是否 B 同时也关注了 A，当 isMyFans != null 时为互粉状态
     *
     * 踩坑： t join 到 f2 时必须添加 and f2.friendId = ?，否则 join 出来很多重复数据，而且必须
     *       使用 and 连接两个 join 的条件，不能使用 where...
	 */
	public Page<Account> getFollowPage(int accountId, int pageNum) {
        String select = "select t.*, f2.friendId as isMyFans";
		StringBuilder from = new StringBuilder();
		from.append("from (");
		from.append("    select f.friendId, a.id, a.nickName, a.avatar from friend f inner join account a ");
		from.append("    on f.friendId = a.id where f.accountId = ? order by f.createAt desc");
		from.append(") as t left join friend f2 on t.friendId = f2.accountId and f2.friendId = ?");
		return accountDao.paginate(pageNum, pageSize, select, from.toString(), accountId, accountId);
	}

	/**
	 * 获取粉丝列表
	 * 暂时不使用 AccountService.join(...)
	 * isMyFriend 用来表示 A 被 B 关注时，是否 A 同时也关注了 B，当 isMyFirend != null 时为互粉状态
	 */
	public Page<Account> getFansPage(int accountId, int pageNum) {
		String select = "select t.*, f2.accountId as isMyFriend";
        StringBuilder from = new StringBuilder();
        from.append(" from ( ");
		from.append("    select f.accountId, a.id, a.nickName, a.avatar from friend f inner join account a ");
		from.append("    on f.accountId = a.id where f.friendId = ? order by f.createAt desc");
		from.append(") as t left join friend f2 on t.accountId = f2.friendId and f2.accountId = ?");
		return accountDao.paginate(pageNum, pageSize, select, from.toString(), accountId, accountId);
	}

	/**
	 * 加好友
	 */
	public Ret addFriend(int accountId, int friendId) {
        if (accountId == friendId) {
            return Ret.fail("msg", "不能添加自己为好友");
        }
		Record friend = new Record().set("accountId", accountId).set("friendId", friendId).set("createAt", new Date());
		try {
            Db.save("friend", "accountId, friendId", friend);
            // TODO 改为更细粒度的缓存以后该这样用： NewsFeedService.me.clearCache(accountId); 即只删除指令用户的缓存
            // TODO 备忘：由于 news feed 列表与好友有关：显示自己与好友的 news feed，所以要清掉相关 newsfeed
            NewsFeedService.me.clearCache();
            RemindService.me.createRemindOfNewFans(friendId);   // 向增加粉丝的用户发送提醒
            return Ret.ok();
        } catch (ActiveRecordException e) {
            // 快速多次点击关注按钮时，插入重复值时会抛异常，返回成功
            if (e.getCause() instanceof com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException) {
                return Ret.ok();
            }
            return Ret.fail("msg", "添加关注失败");
        }
	}

	/**
	 * 删好友
	 */
	public Ret deleteFriend(int accountId, int friendId) {
		// 后续可能会为了优化而构建一条 accoutId 与 friendId 的数据，这条数据不允许删除
		if (accountId == friendId) {
			return Ret.fail("msg", "accountId 与 friendId 相等");
		}
		Db.update("delete from friend where accountId=? and friendId=?", accountId, friendId);
        // TODO 改为更细粒度的缓存以后该这样用： NewsFeedService.me.clearCache(accountId); 即只删除指令用户的缓存
        // TODO 备忘：由于 news feed 列表与好友有关：显示自己与好友的 news feed，所以要清掉相关 newsfeed
        NewsFeedService.me.clearCache();
		return Ret.ok();
	}

	/**
	 * 查询 accountId 与 friendId 之间的关系，返回值为 -1、1、2、3、4 表达的含义分别为：
	 * -1：accountId 与 friendId 值相同
	 * 0： accountId 与 friendId 无任何关系
	 * 1： accountId 关注了 friendId
	 * 2： friendId 关注了 accountId
	 * 3： accountId 与 friendId 互相关注
	 */
	public int getFriendRelation(int accountId, int friendId) {
		if (accountId == friendId) {
			return -1;                  // accountId 与 friendId 相同
		}

		List<Record> list = Db.find(
				"select accountId, friendId from friend where accountId= ? and friendId= ? union all " +
				"select accountId, friendId from friend where accountId= ? and friendId= ?",
				accountId, friendId, friendId, accountId);
		if (list.size() == 0) {
			return 0;                   // 两个账号无任何关系
		}
		if (list.size() == 1) {
			if (list.get(0).getInt("accountId") == accountId) {
				return 1;               // accountId 关注了 friendId
			} else {
				return 2;               // friendId 关注了 accountId
			}
		}
		if (list.size() == 2) {
			return 3;                   // accountId 与 friendId 互相关注
		}
		throw new RuntimeException("不可能存在的第五种关系，正常情况下该异常永远不可能抛出");
	}

    /**
     * 获取关注与粉丝总数
     * 将其缓存起来
     */
    public int[] getFollowAndFansCount(int accountId) {
        // 两种 sql 都可以实现功能，注意这里要使用 union all，需要避免去重
        String sql = "select count(*) from friend f1 where accountId = ? union all " +
                     "select count(*) from friend f2 where friendId = ? ";
        List<Long> list = Db.query(sql, accountId, accountId);
        return new int[]{list.get(0).intValue(), list.get(1).intValue()};
        // String sql =  "select * from " +
        //             "   (select count(*) from friend f1 where accountId = ?) as t1 ," +
        //             "   (select count(*) from friend f2 where friendId = ?) as t2";
        // List<Object[]> list = Db.query(sql, accountId, accountId);
        // return new int[]{((Long)list.get(0)[0]).intValue(), ((Long)list.get(0)[1]).intValue()};
    }

    /**
     * 无论是添加还是删除好友，都调用一次该方法，调用的时候 accountId 与 friendId 的次序无关紧要
     * TODO 暂未启用
     * 1：在 getFollowList() 与 getFansList() 中 put 数据，设置一个合理的过期时间
     * 2：在 add(accountId, friendId) 与 delete(accountId, friendId) 中调用此 clearCache 方法
     */
    public void clearCache(int accountId, int friendId) {
        CacheKit.remove(myFollowCacheName, accountId);
        CacheKit.remove(myFollowCacheName, friendId);

        CacheKit.remove(myFansCacheName, accountId);
        CacheKit.remove(myFansCacheName, friendId);

        CacheKit.remove(followAndFansTotalCacheName, accountId);
        CacheKit.remove(followAndFansTotalCacheName, friendId);

        UserFriendService.me.clearCache(accountId);
        UserFriendService.me.clearCache(friendId);
    }
}


