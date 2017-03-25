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

package com.jfinal.club.my.favorite;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.club.common.controller.BaseController;
import com.jfinal.club.common.interceptor.FrontAuthInterceptor;
import com.jfinal.kit.Ret;
import com.jfinal.club.common.model.Favorite;
import com.jfinal.club.my.friend.FriendInterceptor;
import com.jfinal.club.my.like.LikeInterceptor;
import java.util.List;

/**
 * 收藏控制器
 */
@Before({FrontAuthInterceptor.class, FriendInterceptor.class, LikeInterceptor.class})
public class FavoriteController extends BaseController {

    static FavoriteService srv = FavoriteService.me;

    /**
     * 个人空间首页的收藏列表
     */
    public void index() {
        List<Favorite> favoriteList = srv.findAll(getLoginAccountId());
        setAttr("favoriteList", favoriteList);
        render("index.html");
    }

    /**
     * 个人空间根据 id 删除收藏
     */
    public void delete() {
        srv.deleteByFavoriteId(getLoginAccountId(), getParaToInt("id"));
        redirect("/my/favorite");
    }

    /**
     * 用于 article detail 页面添加/取消收藏功能
     */
    @ActionKey("/favorite")
    @Clear({FrontAuthInterceptor.class, FriendInterceptor.class, LikeInterceptor.class})
    public void favorite() {
        if (notLogin()) {
            renderJson(Ret.fail("msg", "请先登录"));
            return ;
        }

        Ret ret = srv.favorite(getLoginAccountId(), getPara("refType"), getParaToInt("refId"), getParaToBoolean("isAdd"));
        renderJson(ret);
    }
}
