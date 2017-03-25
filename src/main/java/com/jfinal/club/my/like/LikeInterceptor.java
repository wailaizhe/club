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

package com.jfinal.club.my.like;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.club.common.account.AccountService;
import com.jfinal.club.common.controller.BaseController;
import com.jfinal.club.common.model.Account;

/**
 * 用于显示 "/my" 个人空间与 "/user" 空间的点赞数量
 */
public class LikeInterceptor implements Interceptor {

    public static final String likeNum = "_likeNum";

    public void intercept(Invocation inv) {
        inv.invoke();

        BaseController c = (BaseController) inv.getController();
        boolean isUserSpace = inv.getActionKey().startsWith("/user");
        if (isUserSpace) {
            handleUserSpaceLikeCount(c);
        } else {
            handleMySpaceLikeCount(c);
        }
    }

    private void handleUserSpaceLikeCount(BaseController c) {
        Account account = AccountService.me.getById(c.getParaToInt());
        c.setAttr(likeNum, account.getLikeCount());
    }

    private void handleMySpaceLikeCount(BaseController c) {
        Account account = AccountService.me.getById(c.getLoginAccountId());
        c.setAttr(likeNum, account.getLikeCount());
    }
}
