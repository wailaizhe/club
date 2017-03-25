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

package com.jfinal.club.document;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.club.common.controller.BaseController;
import com.jfinal.club.common.interceptor.AdminAuthInterceptor;
import com.jfinal.club.common.model.Document;
import java.util.List;

/**
 * 文档控制器
 */
public class DocumentController extends BaseController {

	final static DocumentService srv = DocumentService.me;

	@ActionKey("/doc")
	@Before(DocumentSeo.class)
	public void doc() {
		Integer mainMenu = getParaToInt(0);
		Integer subMenu = getParaToInt(1);
		if (mainMenu == null && subMenu == null) {
			mainMenu = 1;
			subMenu = 1;
		}
		if (mainMenu == null || subMenu == null) {
			renderError(404);
		}

		Document doc = srv.findById(mainMenu, subMenu);
		if (doc == null) {
			renderError(404);
		}

		List<Document> menu = srv.getMenu();
		setAttr("mainMenuList", menu);
		setAttr("doc", doc);
		render("index.html");
	}

	/**
	 * 有时会手动改数据库，需要清下缓存
	 */
	@Before(AdminAuthInterceptor.class)
	public void clear() {
		srv.clearCache();
	}
}


