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

package com.jfinal.club.common.pageview;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;
import org.joda.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 维护 project_page_view、share_page_view、feedback_page_view 数据
 */
public class PageViewService {
	public static final PageViewService me = new PageViewService();

	@SuppressWarnings("serial")
	private Map<String, String> actionKeyToCacheName = new HashMap<String, String>(){{
		// 只统计 project、share、feedback 详情页
		put("/project/detail", "projectPageView");
		put("/share/detail", "sharePageView");
		put("/feedback/detail", "feedbackPageView");
	}};

	public void updateToDataBase() {
		Date date = LocalDate.now().toDate();
		doUpdateToDataBase("project", date);
		doUpdateToDataBase("share", date);
		doUpdateToDataBase("feedback", date);
	}

	/**
	 * project_page_view(projectId, visitDate, visitCount)
	 */
	@SuppressWarnings("unchecked")
	private void doUpdateToDataBase(String articleType, Date date) {
		String cacheName = articleType + "PageView";
		List<Integer> ids = CacheKit.getKeys(cacheName);
		for (Integer id : ids) {
			Integer visitCount = CacheKit.get(cacheName, id);
			// 获取以后立即清除，因为获取后的值将累加到数据表中。或许放在 for 循环的最后一行为好
			CacheKit.remove(cacheName, id);

			int n = Db.update(getUpdateSql(articleType), visitCount, id, date);
			if (n == 0) {   // 记录不存在则插入新记录
				Record pageView = new Record();
				pageView.set(articleType + "Id", id);
				pageView.set("visitDate", date);
				pageView.set("visitCount", visitCount);
				Db.save(articleType + "_page_view", pageView);
			}
		}
	}

	private String getUpdateSql(String articleType) {
		return "update " + articleType + "_page_view set visitCount = visitCount + ?" +
				" where "+ articleType + "Id = ? and visitDate = ?";
	}

	/**
	 * 1：通过 actionKey + ip地址，去 pageViewId 缓存中去找 article id 是否存在
	 * 2：如果不存在，则将当前 article 的 visitCount 加 1 并缓存，否则直跳过不处理
	 * 3：不同类型的数据缓存分别为：xxxPageView
	 * 5：引入 pageViewId 缓存，是为了防止刷榜
	 */
	public void processPageView(String actionKey, Integer id, String ip) {
		if (id == null) {
			throw new IllegalArgumentException("id 值不能为 null.");
		}
		String cacheName = actionKeyToCacheName.get(actionKey);
		if (cacheName == null) {
			throw new RuntimeException("不支持的 actionKey： " + actionKey);
		}

		if (ip == null) {
			ip = "127.0.0.1";
		}

		// actionKey + ip 当成 key用于区分：project、share、feedback
		String pageViewKey = actionKey + ip;
		Integer idInCache = CacheKit.get("pageViewIp", pageViewKey);

		// 为了避免恶意刷榜，id 在 cache中不存在，或者存在但不等于 id，才去做计数，否则直接跳过
		if (idInCache == null || !id.equals(idInCache)) {
			Integer visitCount = CacheKit.get(cacheName, id);
			visitCount = (visitCount != null ? visitCount + 1 : 1);
			CacheKit.put(cacheName, id, visitCount);

			// 将当前访问者的 actionKey + ip ---> id 放入缓存
			CacheKit.put("pageViewIp", pageViewKey, id);
		}
	}
}
