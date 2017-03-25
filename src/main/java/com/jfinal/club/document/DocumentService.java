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

import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.club.common.model.Document;
import java.util.List;

/**
 * 文档业务
 */
public class DocumentService {

	public static final DocumentService me = new DocumentService();
	final Document dao = new Document().dao();
	final String docCacheName = "doc";
	final String menuCacheKey = "menu";

	// 加载一级文档，只加载 publish 为发布的
	public List<Document> getMenu() {
		List<Document> docList = CacheKit.get(docCacheName, menuCacheKey);
		if (docList == null) {
			String sql = "select mainMenu, subMenu, title from document where subMenu = 0 and publish=? order by mainMenu asc";
			docList = dao.find(sql, Document.PUBLISH_YES);
			for (Document pDoc : docList) {
				loadSubMenu(pDoc);
			}
			CacheKit.put(docCacheName, menuCacheKey, docList);
		}
		return docList;
	}

	// 加载二级文档，只加载 publish 为发布的
	private void loadSubMenu(Document pDoc) {
		int mainMenu = pDoc.getMainMenu();
		String sql = "select mainMenu, subMenu, title from document where mainMenu=? and subMenu>0 and publish=? order by subMenu asc";
		List<Document> subDocList = dao.find(sql, mainMenu, Document.PUBLISH_YES);
		pDoc.put("subMenuList", subDocList);
	}

	public Document findById(int mainMenu, int subMenu) {
		// 暂不支持主菜单 doc 的显示，主菜单 doc 现在仅用于我自己来 todolist 和大纲
		if (subMenu == 0) {
			return null;
		}

		String cacheKey = buildCacheKey(mainMenu, subMenu);
		Document doc = CacheKit.get(docCacheName, cacheKey);
		if (doc == null) {
			String sql = "select * from document where mainMenu=? and subMenu=? and publish=? limit 1";
			doc = dao.findFirst(sql, mainMenu, subMenu, Document.PUBLISH_YES);
			CacheKit.put(docCacheName, cacheKey, doc);
		}
		return doc;
	}

	private String buildCacheKey(int mainMenu, int subMenu) {
		return mainMenu + "_" + subMenu;
	}

	public void clearCache() {
		CacheKit.removeAll(docCacheName);
	}
}


