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

package com.jfinal.club.my.message;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.club.common.account.AccountService;
import com.jfinal.club.common.controller.BaseController;
import com.jfinal.club.common.interceptor.FrontAuthInterceptor;
import com.jfinal.kit.Ret;
import com.jfinal.club.common.kit.SensitiveWordsKit;
import com.jfinal.club.common.model.Account;
import com.jfinal.club.common.model.Message;
import com.jfinal.club.my.friend.FriendInterceptor;
import com.jfinal.club.my.like.LikeInterceptor;
import com.jfinal.club.my.newsfeed.RemindService;

/**
 * 我的信息/私信
 */
@Before({FrontAuthInterceptor.class, FriendInterceptor.class, LikeInterceptor.class})
public class MessageController extends BaseController {

    MessageService srv = MessageService.me;

    /**
     * 所有私信往来
     */
    @ActionKey("/my/message")
	public void message() {
        Page<Message> messagePage = srv.paginate(getParaToInt("p", 1), getLoginAccountId());
        RemindService.me.resetRemindOfMessage(getLoginAccountId());
        setAttr("messagePage", messagePage);
        render("index.html");
	}

    /**
     * 与某一用户的私信
     */
    public void friend() {
        int friendId = getParaToInt();
        Page<Message> messagePage = srv.paginate(getParaToInt("p", 1), getLoginAccountId(), friendId);

        Account friend = new Account().set("id", friendId);
        AccountService.me.join("id", friend, "nickName", "avatar");

        setAttr("messagePage", messagePage);
        setAttr("friend", friend);
        render("one_friend_message.html");
    }

    /**
     * 发送私信
     */
    public void send() {
        // RestTime 的调用可以考虑移到业务层中去，发送私信暂时不开启 testTime 的 check
//        String restTimeMsg = RestTime.checkRestTime(getLoginAccount());
//        if (restTimeMsg != null) {
//            renderJson(Ret.error("msg", restTimeMsg).getData());
//            return ;
//        }
        String replyContent = getPara("replyContent");
        if (StrKit.isBlank(replyContent)) {
            renderJson(Ret.fail("msg", "私信内容不能为空"));
            return ;
        }
        if (SensitiveWordsKit.checkSensitiveWord(replyContent) != null) {
            renderJson(Ret.fail("msg", "私信内容不能包含敏感词"));
            return ;
        }

        Ret ret = srv.send(getLoginAccountId(), getParaToInt("friendId"), replyContent);
        if (ret.isFail()) {
            renderJson(ret);
            return ;
        }

        ret.set("loginAccount", getLoginAccount());     // 放入 loginAccount 供 renderToString 使用

        // 用模板引擎生成 HTML 片段 replyItem
        String replyItem = renderToString("/_view/my/message/_one_friend_message_reply_item.html", ret);
        
        ret.set("replyItem", replyItem);
        renderJson(ret);
    }

    /**
     * 删除某一条私信
     */
    public void deleteByMessageId() {
        Ret ret = srv.deleteByMessageId(getLoginAccountId(), getParaToInt("messageId"));
        renderJson(ret);
    }

    /**
     * 删除某一个用户的所有私信往来
     */
    public void deleteByFriendId() {
        Ret ret = srv.deleteByFriendId(getLoginAccountId(), getParaToInt("friendId"));
        renderJson(ret);
    }

}
