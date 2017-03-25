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

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.club.common.controller.BaseController;
import com.jfinal.club.common.model.Account;
import com.jfinal.club.my.friend.FriendInterceptor;
import com.jfinal.club.my.like.LikeInterceptor;
import com.jfinal.club.user.common.UserSpaceInterceptor;

/**
 * UserFriendController
 */
@Before({UserSpaceInterceptor.class, FriendInterceptor.class, LikeInterceptor.class})
public class UserFriendController extends BaseController {

    static final UserFriendService srv = UserFriendService.me;

    /**
     * 用户关注列表
     */
    @ActionKey("/user/follow")
    public void follow() {
        Page<Account> followPage = srv.getFollowPage(getParaToInt(), getParaToInt("p", 1));
        setAttr("followPage", followPage);
        render("follow.html");
    }

    /**
     * 用户粉丝列表
     */
    @ActionKey("/user/fans")
    public void fans() {
        Page<Account> fansPage = srv.getFansPage(getParaToInt(), getParaToInt("p", 1));
        setAttr("fansPage", fansPage);
        render("fans.html");
    }
}
