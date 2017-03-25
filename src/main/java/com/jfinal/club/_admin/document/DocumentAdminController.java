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

package com.jfinal.club._admin.document;

import com.jfinal.club.common.controller.BaseController;
import com.jfinal.kit.Ret;
import com.jfinal.club.common.model.Document;
import java.util.List;

/**
 * 文档管理控制器
 * 暂不支持主菜单 doc 的显示，主菜单 doc 现在仅用于我自己来 todolist 和大纲
 * 
 * 注意：由于针对 jfinal 3.0 发布改了界面，为了早点发布，后台管理中的
 *     文档管理界面来不及添加进来，3.0 发布后首先会添加这里
 */
public class DocumentAdminController extends BaseController {

	static DocumentAdminService srv = DocumentAdminService.me;

	public void index() {
		List<Document> docList = srv.getDocList();
		setAttr("docList", docList);
		render("index.html");
	}

	public void add() {
		List<Document> docList = srv.getDocList();
		setAttr("docList", docList);
		render("add.html");
	}

	public void save() {
		Document doc = getModel(Document.class, "doc");
		Ret ret = srv.save(doc);
		renderJson(ret);
	}

	public void edit() {
		Document doc = srv.getById(getParaToInt("mainMenu"), getParaToInt("subMenu"));
		setAttr("doc", doc);
		render("edit.html");
	}

	public void update() {
		Document doc = getModel(Document.class, "doc");
		Ret ret = srv.update(getParaToInt("oldMainMenu"), getParaToInt("oldSubMenu"), doc);
		renderJson(ret);
	}

	public void delete() {
		srv.delete(getParaToInt("mainMenu"), getParaToInt("subMenu"));
		renderJson(Ret.ok("msg", "document 删除成功"));
	}
}




