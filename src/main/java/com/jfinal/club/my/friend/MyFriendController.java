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

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.club.common.controller.BaseController;
import com.jfinal.club.common.interceptor.FrontAuthInterceptor;
import com.jfinal.kit.Ret;
import com.jfinal.club.common.model.Account;
import com.jfinal.club.my.like.LikeInterceptor;
import com.jfinal.club.my.newsfeed.RemindService;

/**
 * FriendController
 */
@Before({FrontAuthInterceptor.class, FriendInterceptor.class, LikeInterceptor.class})
public class MyFriendController extends BaseController {

	static final MyFriendService srv = MyFriendService.me;

	/**
	 * 关注列表
	 */
	@ActionKey("/my/follow")
	public void follow() {
		Page<Account> followPage = srv.getFollowPage(getLoginAccountId(), getParaToInt("p", 1));
		setAttr("followPage", followPage);
		render("follow.html");
	}

	/**
	 * 粉丝列表
	 */
	@ActionKey("/my/fans")
	public void fans() {
		Page<Account> fansPage = srv.getFansPage(getLoginAccountId(), getParaToInt("p", 1));
        RemindService.me.resetRemindOfNewFans(getLoginAccountId()); // 重置粉丝增加提醒
		setAttr("fansPage", fansPage);
		render("fans.html");
	}

	/**
	 * 加好友
	 */
	@ActionKey("/friend/add")
	@Clear({FrontAuthInterceptor.class, FriendInterceptor.class, LikeInterceptor.class})// 拦截器中的返回值不符合要求，需要定制返回值，所以 clear 掉
	public void add() {
		if (notLogin()) {
			renderJson(Ret.fail("msg", "登录后才能添加好请先登录"));// 定制返回值
			return ;
		}
		int accountId = getLoginAccountId();
		int friendId = getParaToInt("friendId");
		Ret ret = srv.addFriend(accountId, friendId);
		ret.set("friendRelation", srv.getFriendRelation(accountId, friendId));
		renderJson(ret);
	}

	/**
	 * 删好友
	 */
	@ActionKey("/friend/delete")
	public void delete() {
		int accountId = getLoginAccountId();
		int friendId = getParaToInt("friendId");
		Ret ret = srv.deleteFriend(accountId, friendId);
		ret.set("friendRelation", srv.getFriendRelation(accountId, friendId));
		renderJson(ret);
	}

    /**
     * 获取好友关系，目前用于用户空间关注/粉丝列表页面 ajax 动态获取关系
     */
    @ActionKey("/friend/getFriendRelation")
    @Clear({FrontAuthInterceptor.class, FriendInterceptor.class, LikeInterceptor.class})
    public void getFriendRelation() {
        if (notLogin()) {
            renderJson(Ret.fail());
            return ;
        }

        int friendRelation = srv.getFriendRelation(getLoginAccountId(), getParaToInt("friendId"));
        renderJson(Ret.ok("friendRelation", friendRelation));
    }
}
