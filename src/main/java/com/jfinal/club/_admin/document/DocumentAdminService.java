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

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.kit.Ret;
import com.jfinal.club.common.model.Document;
import com.jfinal.club.document.DocumentService;
import java.util.Date;
import java.util.List;

/**
 * document 管理业务
 */
public class DocumentAdminService  {

	public static final DocumentAdminService me = new DocumentAdminService();
	final static Document dao = new Document().dao();

	// 加载一级文档，即便是 publish 为 0 的也加载
	public List<Document> getDocList() {
		List<Document> docList = dao.find("select * from document where subMenu = 0 order by mainMenu asc");
		for (Document pDoc : docList) {
			loadSubDocList(pDoc);
		}
		return docList;
	}

	// 加载二级文档，文档最多分两级目录，三级甚至更多级目录直接在 content 中体现
	private void loadSubDocList(Document pDoc) {
		int mainMenu = pDoc.getMainMenu();
		String sql = "select * from document where mainMenu = ? and subMenu > 0 order by subMenu asc";
		List<Document> subDocList = dao.find(sql, mainMenu);
		pDoc.put("subDocList", subDocList);
	}

	public Document getById(int mainMenu, int subMenu) {
		return dao.findById(mainMenu, subMenu);
	}

	public Ret save(Document doc) {
		if (isExists(doc)) {
			return Ret.fail("msg", "mainMenu 与 subMenu 组合已经存在");
		}
		doc.setCreateAt(new Date());
		doc.setUpdateAt(new Date());
		doc.save();
		DocumentService.me.clearCache();    // 清缓存
		return Ret.ok();
	}

	public Ret update(int oldMainMenu, int oldSubMenu, Document doc) {
		// 当 mainMenu 或 subMenu 值也被修改的时候，判断一下新值是否已经存在
		if (oldMainMenu != doc.getMainMenu() || oldSubMenu != doc.getSubMenu()) {
			if (isExists(doc)) {
				return Ret.fail("msg", "mainMenu 或 subMenu 已经存在，不能使用");
			}
		}
		doc.setUpdateAt(new Date());
		doc.update();
		if (oldMainMenu != doc.getMainMenu() || oldSubMenu != doc.getSubMenu()) {
			Db.update("update document set mainMenu=?, subMenu=? where mainMenu=? and subMenu=?",
						doc.getMainMenu(), doc.getSubMenu(), oldMainMenu, oldSubMenu);
		}
		DocumentService.me.clearCache();    // 清缓存
		return Ret.ok();
	}

	public void delete(int mainMenu, int subMenu) {
		Db.update("delete from document where mainMenu=? and subMenu=? limit 1", mainMenu, subMenu);
		DocumentService.me.clearCache();    // 清缓存
	}

	private boolean isExists(Document doc) {
		String sql = "select mainMenu from document where mainMenu=? and subMenu=? limit 1";
		return Db.queryInt(sql , doc.getMainMenu(), doc.getSubMenu()) != null;
	}
}
