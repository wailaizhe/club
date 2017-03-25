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

package com.jfinal.club.user.friend;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.club.common.model.Account;

/**
 * 非自己的其他用户的好友业务
 * 关键点：其他用户的好友与自己的好友关系是不同的，所以好友下方的链接文字以及 js 方法调用都将不同
 */
public class UserFriendService {

    public static final UserFriendService me = new UserFriendService();
    final String userFollowCacheName = "userFollowList";
    final String userFansCacheName = "userFansList";
    final Account accountDao = new Account().dao();
    final int pageSize = 15;

    /**
     * 获取非自己的某位用户关注列表
     * 与 MyFriendService 中不同，非自己用户所关注的人与自己的好友关系需要单独计算
     * MyFriendService 的 getFollowList 中的目标用户列表已经具备了被关注的条件
     */
    public Page<Account> getFollowPage(int accountId, int pageNum) {
        String select = "select f.friendId, a.id, a.nickName, a.avatar";
        StringBuilder sql = new StringBuilder();
        sql.append("from friend f inner join account a ");
        sql.append("on f.friendId = a.id where f.accountId = ? order by f.createAt desc");
        return accountDao.paginate(pageNum, pageSize, select, sql.toString(), accountId);
    }

    /**
     * 获取非自己的某位用户粉丝列表
     * 与 MyFriendService 中不同，非自己用户的粉丝与自己的好友关系需要单独计算
     * MyFriendService 的 getFansList 中的目标用户列表已经具备了关注了自己的条件
     */
    public Page<Account> getFansPage(int accountId, int pageNum) {
        String select = "select f.accountId, a.id, a.nickName, a.avatar";
        StringBuilder sql = new StringBuilder();
        sql.append("from friend f inner join account a ");
        sql.append("on f.accountId = a.id where f.friendId = ? order by f.createAt desc");
        return accountDao.paginate(pageNum, pageSize, select, sql.toString(), accountId);
    }

    /**
     * MyFriendService 中的 clearCache(int, int) 会调用该方法，其它地方不使用
     * TODO 暂未启用
     * 1：在 getFollowList() 与 getFansList() 中 put 数据，设置一个合理的过期时间
     * 2：在 add(accountId, friendId) 与 delete(accountId, friendId) 中调用此 clearCache 方法
     */
    public void clearCache(int userId) {
        CacheKit.remove(userFollowCacheName, userId);
        CacheKit.remove(userFansCacheName, userId);
    }
}


