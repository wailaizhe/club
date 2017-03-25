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

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.club.common.controller.BaseController;

/**
 * 关注/粉丝数量拦截器：用于用户空间显示头像下方的关注/粉丝
 */
public class FriendInterceptor implements Interceptor {

    public static final String followNum = "_followNum";
    public static final String fansNum = "_fansNum";
    public static final String friendRelation = "_friendRelation";

    public void intercept(Invocation inv) {
        inv.invoke();

        BaseController c = (BaseController) inv.getController();
        boolean isUserSpace = inv.getActionKey().startsWith("/user");
        if (isUserSpace) {
            handleUserSpaceFriend(c);
        } else {
            handleMySpaceFriend(c);
        }
    }

    /**
     * 处理用户空间 "/user" 关注/粉丝数量，以及好友关系
     */
    private void handleUserSpaceFriend(BaseController c) {
        int userId = c.getParaToInt();
        // 如果当前访问者已经登录，利用 myId 与 userId 去查询好友关系
        if (c.isLogin()) {
            int myId = c.getLoginAccountId();
            // 业务层获取好友关系
            int friendRelations = MyFriendService.me.getFriendRelation(myId, userId);
            c.setAttr(friendRelation, friendRelations);
        }
        // 如果当前访问者未登录，无法确定好友关系，则认为该访问者与 user 无好友关系
        else {
            // 值为 0 表示无好友关系，详情见 MyFriendService.getFriendRelation() 注释中的说明
            int friendRelations = 0;
            c.setAttr(friendRelation, friendRelations);
        }

        // 设置关注/粉丝数量
        int[] ret = MyFriendService.me.getFollowAndFansCount(userId);
        c.setAttr(followNum, ret[0]);
        c.setAttr(fansNum, ret[1]);
    }

    /**
     * 处理我的空间 "/my" 关注/粉丝数量，但好友关系不需要处理
     * 因为不需要 friendRelation 变量，直接显示 "更换头像" 链接即可
     */
    private void handleMySpaceFriend(BaseController c) {
        // 个人空间有 FrontAuthInterceptor 保障过登录，不用 isLogin() 来判断
        // 如果抛异常则必须要改代码修正 bug，clear 掉该拦截器就可以了，主要对于 ajax 做个清除
        int myId = c.getLoginAccountId();

        // 设置关注/粉丝数量
        int[] ret = MyFriendService.me.getFollowAndFansCount(myId);
        c.setAttr(followNum, ret[0]);
        c.setAttr(fansNum, ret[1]);
    }
}