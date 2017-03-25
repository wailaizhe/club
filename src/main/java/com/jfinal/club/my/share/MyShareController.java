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

package com.jfinal.club.my.share;

import com.jfinal.aop.Before;
import com.jfinal.club.common.controller.BaseController;
import com.jfinal.club.common.interceptor.FrontAuthInterceptor;
import com.jfinal.club.common.model.Share;
import com.jfinal.club.common.safe.RestTime;
import com.jfinal.club.my.friend.FriendInterceptor;
import com.jfinal.club.my.like.LikeInterceptor;
import com.jfinal.club.project.ProjectService;
import java.util.List;

/**
 * 我的分享
 */
@Before({FrontAuthInterceptor.class, FriendInterceptor.class, LikeInterceptor.class})
public class MyShareController extends BaseController {

	static final MyShareService srv = MyShareService.me;

	public void index() {
		List<Share> shareList = srv.findAll(getLoginAccountId());
		setAttr("shareList", shareList);
		render("index.html");
	}

	public void add() {
		setAttr("projectList", ProjectService.me.getAllProject("id, name"));    // 关联项目下拉列表
		render("add.html");
	}

	@Before({MyShareValidator.class, RestTime.class})
	public void save() {
		srv.save(getLoginAccountId(), getModel(Share.class));
		renderJson("isOk", true);
	}

	public void edit() {
		Share share = srv.findById(getLoginAccountId(), getParaToInt("id"));
		setAttr("share", share);
		setAttr("projectList", ProjectService.me.getAllProject("id, name"));    // 关联项目下拉列表
		render("edit.html");
	}

	@Before(MyShareValidator.class)
	public void update() {
		srv.update(getLoginAccountId(), getModel(Share.class));
		renderJson("isOk", true);
	}

	public void delete() {
		srv.delete(getLoginAccountId(), getParaToInt("id"));
		redirect("/my/share");
	}
}