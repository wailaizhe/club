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

package com.jfinal.club.user.newsfeed;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.club.common.controller.BaseController;
import com.jfinal.club.common.model.NewsFeed;
import com.jfinal.club.my.friend.FriendInterceptor;
import com.jfinal.club.my.like.LikeInterceptor;
import com.jfinal.club.my.newsfeed.NewsFeedService;
import com.jfinal.club.user.common.UserSpaceInterceptor;

/**
 * url: "/user/123"
 * 点击用户的头像、nickName 或者 @nickName 查看某位用户的项目、分享、反馈
 */
@Before({UserSpaceInterceptor.class, FriendInterceptor.class, LikeInterceptor.class})
public class UserNewsFeedController extends BaseController {

	static NewsFeedService newsFeedSrv = NewsFeedService.me;

	/**
	 * SimpleRestfulHandler 让 index() 方法不能使用 urlPara，所以在此使用 detail 方法
	 */
	@ActionKey("/user")
	public void newsfeed() {
		Page<NewsFeed> newsFeedPage = newsFeedSrv.paginateForUserSpace(getParaToInt(), getParaToInt("p", 1));
		setAttr("newsFeedPage", newsFeedPage);
        setAttr("paginateLink", "/user/" + getParaToInt() + "?p=");                  // 用于指定重用页面分页宏所使用的 link
		render("index.html");
	}
}
