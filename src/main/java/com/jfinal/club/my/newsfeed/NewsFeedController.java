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

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.club.common.controller.BaseController;
import com.jfinal.club.common.interceptor.AdminAuthInterceptor;
import com.jfinal.club.common.interceptor.FrontAuthInterceptor;
import com.jfinal.kit.Ret;
import com.jfinal.club.common.kit.SensitiveWordsKit;
import com.jfinal.club.common.model.NewsFeed;
import com.jfinal.club.common.safe.RestTime;
import com.jfinal.club.my.friend.FriendInterceptor;
import com.jfinal.club.my.like.LikeInterceptor;

/**
 * 动态消息
 */
@Before({FrontAuthInterceptor.class, FriendInterceptor.class, LikeInterceptor.class})
public class NewsFeedController extends BaseController {

	static NewsFeedService srv = NewsFeedService.me;
	static NewsFeedReplyService newsFeedReplyService = NewsFeedReplyService.me;

	@ActionKey("/my")
	public void mewsFeed() {
		Page<NewsFeed> newsFeedPage = srv.paginate(getLoginAccountId(), getParaToInt("p", 1));
		setAttr("newsFeedPage", newsFeedPage);
		setAttr("paginateLink", "/my?p=");                  // 用于指定重用页面分页宏所使用的 link
		render("index.html");
	}

    /**
     * 热门动态，暂时实现为显示 jfinal 作者个人空间中的动态
     */
    @ActionKey("/my/hot")
    public void hot() {
        int jfinalId = 1;
        Page<NewsFeed> newsFeedPage = srv.paginate(jfinalId, getParaToInt("p", 1));
        setAttr("newsFeedPage", newsFeedPage);
        setAttr("paginateLink", "/my/hot?p=");
        render("index.html");
    }

    /**
     * 该隐藏功能，方便管理员查看整站除了私信以外产生的数据
     * 便于对数据进行管理，保障数据品质
     */
    @Before(AdminAuthInterceptor.class)
    @ActionKey("/my/all")
    public void all() {
        Page<NewsFeed> newsFeedPage = srv.paginateForAllNewsFeed(getParaToInt("p", 1));
        setAttr("newsFeedPage", newsFeedPage);
        setAttr("paginateLink", "/my/all?p=");
        render("index.html");
    }

	public void referMe() {
		Page<NewsFeed> newsFeedPage = ReferMeService.me.paginate(getLoginAccountId(), getParaToInt("p", 1));
		RemindService.me.resetRemindOfReferMe(getLoginAccountId()); // 重置提醒 remind 的 referMe 字段
		setAttr("newsFeedPage", newsFeedPage);
		setAttr("paginateLink", "/my/referMe?p=");      // 用于指定重用页面分页宏所使用的 link
		render("index.html");
	}

	/**
	 * 响应 ajax 请求，响应 news feed 中的回复列表
	 */
    @Clear({FrontAuthInterceptor.class, FriendInterceptor.class, LikeInterceptor.class})// FrontAuthInterceptor 中的返回值无法满足需求，故 clear 以后用 if (notLogin()) 手动接管
	public void showReplyList() {
        if (notLogin()) {
            renderHtml("notLogin"); // 前端 ajax 收到 "notLogin" 后做额外处理
            return ;
        }

		Ret ret = newsFeedReplyService.getNewsFeedReplyList(getParaToInt("newsFeedId"));
		setAttr("replyList", ret.get("replyList"));
		setAttr("showAllReplyUrl", ret.get("showAllReplyUrl"));
		render("_news_feed_reply_list.html");
	}

	/**
	 * 保存 news feed reply 到 project_reply、share_reply、feedback_reply 中去
	 */
	public void saveNewsFeedReply() {
		// 提前添加一个登录验证，因为将来可能要与 "/user" 整合，可能要采用 @Clear 方案
		if (notLogin()) {
			renderJson(Ret.fail("msg", "登录后才可以评论"));
			return ;
		}
		// RestTime 的调用可以考虑移到业务层中去
		String restTimeMsg = RestTime.checkRestTime(getLoginAccount());
		if (restTimeMsg != null) {
			renderJson(Ret.fail("msg", restTimeMsg));
			return ;
		}
		String replyContent = getPara("replyContent");
		if (StrKit.isBlank(replyContent)) {
			renderJson(Ret.fail("msg", "回复内容不能为空"));
			return ;
		}
		if (SensitiveWordsKit.checkSensitiveWord(replyContent) != null) {
			renderJson(Ret.fail("msg", "回复内容不能包含敏感词"));
			return ;
		}

		Ret ret = newsFeedReplyService.saveReply(getParaToInt("newsFeedId"), getLoginAccountId(), replyContent);
		ret.set("loginAccount", getLoginAccount());     // 放入 loginAccount 供 renderToString 使用

		// 用模板引擎生成 HTML 片段 replyItem
		String replyItem = renderToString("/_view/my/newsfeed/_news_feed_reply_item.html", ret);
		
		ret.set("replyItem", replyItem);
		renderJson(ret);
	}
}
