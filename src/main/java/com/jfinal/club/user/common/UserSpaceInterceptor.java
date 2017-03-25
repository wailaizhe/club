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

package com.jfinal.club.user.common;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.club.common.account.AccountService;
import com.jfinal.club.common.controller.BaseController;
import com.jfinal.club.common.model.Account;

/**
 * UserSpaceInterceptor 用户空间拦截器
 */
public class UserSpaceInterceptor implements Interceptor {

    public void intercept(Invocation inv) {
        BaseController c = (BaseController)inv.getController();

        // 如果登录用户进入的用户空间是自己的空间，则重定向到 /my/xxx
        if (c.isLogin() && c.getParaToInt() == c.getLoginAccountId()) {
            String newActionKey = inv.getActionKey().replaceFirst("/user", "/my");
            c.redirect(newActionKey, true);
            return ;
        }

        // 为用户空间注入 Account user 对象，如果未传入 userId 或者用户找不到，则返回 404
        c.checkUrlPara(1);
        Account user = AccountService.me.getUsefulById(c.getParaToInt());
        if (user == null) {
            c.renderError(404);
            return ;
        }
        c.setAttr("user", user);

        inv.invoke();
    }
}
