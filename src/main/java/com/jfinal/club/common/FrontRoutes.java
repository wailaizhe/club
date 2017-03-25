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

package com.jfinal.club.common;

import com.jfinal.config.Routes;
import com.jfinal.club.common.upload.UploadController;
import com.jfinal.club.document.DocumentController;
import com.jfinal.club.download.DownloadController;
import com.jfinal.club.feedback.FeedbackController;
import com.jfinal.club.index.IndexController;
import com.jfinal.club.login.LoginController;
import com.jfinal.club.my.favorite.FavoriteController;
import com.jfinal.club.my.friend.MyFriendController;
import com.jfinal.club.my.feedback.MyFeedbackController;
import com.jfinal.club.my.like.LikeController;
import com.jfinal.club.my.message.MessageController;
import com.jfinal.club.my.newsfeed.NewsFeedController;
import com.jfinal.club.my.project.MyProjectController;
import com.jfinal.club.my.setting.MySettingController;
import com.jfinal.club.my.share.MyShareController;
import com.jfinal.club.project.ProjectController;
import com.jfinal.club.reg.RegController;
import com.jfinal.club.share.ShareController;
import com.jfinal.club.user.feedback.UserFeedbackController;
import com.jfinal.club.user.friend.UserFriendController;
import com.jfinal.club.user.newsfeed.UserNewsFeedController;
import com.jfinal.club.user.project.UserProjectController;
import com.jfinal.club.user.share.UserShareController;

/**
 * 前台路由
 */
public class FrontRoutes extends Routes {

	public void config() {
		setBaseViewPath("/_view");
		
		add("/", IndexController.class, "/index");
		add("/share", ShareController.class);
		add("/feedback", FeedbackController.class);
		add("/project", ProjectController.class);
		add("/login", LoginController.class);
		add("/reg", RegController.class);
		add("/upload", UploadController.class);
		add("/download", DownloadController.class);
        add("/like", LikeController.class);
		add("/doc", DocumentController.class, "/document");

		// 个人空间：由于共用了相同的拦截器，后续可将其拆分到 MyRoutes 中去，可减少拦截器配置冗余
		add("/my", NewsFeedController.class, "/my/newsfeed");
		add("/my/project", MyProjectController.class);
		add("/my/share", MyShareController.class);
		add("/my/feedback", MyFeedbackController.class);
		add("/my/setting", MySettingController.class);
		add("/my/friend", MyFriendController.class);
        add("/my/message", MessageController.class);
        add("/my/favorite", FavoriteController.class);

		// 用户空间
		add("/user", UserNewsFeedController.class, "/user/newsfeed");
		add("/user/share", UserShareController.class);
		add("/user/feedback", UserFeedbackController.class);
		add("/user/project", UserProjectController.class);
        add("/user/friend", UserFriendController.class);
	}
}
