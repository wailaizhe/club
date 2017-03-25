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

package com.jfinal.club.project;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.club.common.safe.JsoupFilter;
import com.jfinal.club.common.model.Project;
import java.util.List;

/**
 * ProjectService
 */
public class ProjectService {

	public static final ProjectService me = new ProjectService();
	private final Project dao = new Project().dao();

	public Page<Project> paginate(int pageNumber) {
		Page<Project> projectPage = dao.paginate(pageNumber, 15,
				"select p.id, substring(p.title, 1, 100) as title, substring(p.content, 1, 180) as content, a.avatar, a.id as accountId",
				"from project p inner join account a on p.accountId = a.id where report < ? ", Project.REPORT_BLOCK_NUM);
		// 列表页显示 content 的摘要信息需要过滤为纯文本，去除所有标记
		JsoupFilter.filterArticleList(projectPage.getList(), 50, 120);
		return projectPage;
	}

	public Project findById(int projectId) {
		return dao.findFirst("select p.* , a.avatar, a.nickName from project p inner join account a on p.accountId = a.id where p.id =? and p.report < ?  limit 1", projectId, Project.REPORT_BLOCK_NUM);
	}

	public Project findById(int projectId, String columns) {
		return dao.findFirst("select " + columns +  " from project where id =? and report < ?  limit 1", projectId, Project.REPORT_BLOCK_NUM);
	}

	public List<Project> getHotProject() {
		return dao.findByCache("hotProject", "hotProject", "select id, title from project  where report < ?  order by createAt asc limit 10", Project.REPORT_BLOCK_NUM);
	}

	public void clearHotProjectCache() {
		CacheKit.remove("hotProject", "hotProject");
	}

	/**
	 * 暂时用于个人空间的创建、更新 share、feedback 模块，用于显示关联项目的下拉列表，将来改成按热度排序
	 * 项目数量多了以后考虑用输入框配合 autocomplete 提示输入来实现
	 */
	public List<Project> getAllProject(String columns) {
		return dao.find("select " + columns + " from project where report < ? order by createAt asc", Project.REPORT_BLOCK_NUM);
	}
}
