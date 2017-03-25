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

package com.jfinal.club._admin.common;

import com.jfinal.config.Routes;
import com.jfinal.club._admin.document.DocumentAdminController;
import com.jfinal.club._admin.feedback.FeedbackAdminController;
import com.jfinal.club._admin.index.IndexAdminController;
import com.jfinal.club._admin.account.AccountAdminController;
import com.jfinal.club._admin.project.ProjectAdminController;
import com.jfinal.club._admin.share.ShareAdminController;
import com.jfinal.club.common.interceptor.AdminAuthInterceptor;

/**
 * 后台管理路由
 * 注意：自 jfinal 3.0 开始，baesViewPath 改为在 Routes 中独立配置
 *      并且支持 Routes 级别的 Interceptor，这类拦截器将拦截所有
 *      在此 Routes 中添加的 Controller，行为上相当于 class 级别的拦截器
 *      Routes 级别的拦截器特别适用于后台管理这样的需要统一控制权限的场景
 *      减少了代码冗余
 */
public class AdminRoutes extends Routes {

	public void config() {
		// 添加后台管理拦截器，将拦截在此方法中注册的所有 Controller
		addInterceptor(new AdminAuthInterceptor());
		
		setBaseViewPath("/_view/_admin");
		
		add("/admin", IndexAdminController.class, "/index");
		add("/admin/account", AccountAdminController.class, "/account");
		add("/admin/project", ProjectAdminController.class, "/project");
		add("/admin/share", ShareAdminController.class, "/share");
		add("/admin/feedback", FeedbackAdminController.class, "/feedback");
		add("/admin/doc", DocumentAdminController.class, "/document");
	}
}





