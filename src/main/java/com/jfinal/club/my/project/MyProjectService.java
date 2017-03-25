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

package com.jfinal.club.my.project;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.club.common.model.Project;
import com.jfinal.club.index.IndexService;
import com.jfinal.club.my.favorite.FavoriteService;
import com.jfinal.club.my.like.LikeService;
import com.jfinal.club.my.newsfeed.NewsFeedService;
import com.jfinal.club.my.newsfeed.ReferMeKit;
import com.jfinal.club.project.ProjectService;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * 我的项目
 */
public class MyProjectService {

	public static final MyProjectService me = new MyProjectService();
	private final Project dao = new Project().dao();

	public List<Project> findAll(int accountId) {
		return dao.find("select * from project where accountId=? order by createAt desc", accountId);
	}

	public Project findById(int accountId, int projectId) {
		return dao.findFirst("select * from project where accountId=? and id=?", accountId, projectId);
	}

	public Page<Project> paginate(int accountId, int pageNumber, int pageSize) {
		return dao.paginate(pageNumber, pageSize, "select * ", "from project where accountId=? order by createAt", accountId);
	}

	/**
	 * 用于创建项目，判断当前项目名称是否已经存在
	 */
	public boolean isProjectNameExists(String projectName) {
		projectName = projectName.toLowerCase().trim();
		return Db.queryInt("select id from project where lower(name) = ? limit 1", projectName) != null;
	}

	/**
	 * 用于更新项目，判断当前项目名称是否已经存在
	 * 排除当前项目 id：如果项目名称没有被修改，那么项目名称必定已经存在
	 */
	public boolean isProjectNameExists(int exceptProjectId, String projectName) {
		projectName = projectName.toLowerCase().trim();
		return Db.queryInt("select id from project where id != ?  and lower(name) = ? limit 1", exceptProjectId, projectName) != null;
	}

	public boolean isProjectIdExists(Integer projectId) {
		return Db.queryInt("select id from project where id = ? limit 1", projectId) != null;
	}

	public void save(int accountId, Project project) {
		project.setAccountId(accountId);
		project.setCreateAt(new Date());
		project.setClickCount(0);
		List<Integer> referAccounts = ReferMeKit.buildAtMeLink(project);
		project.save();

		// 添加创建项目动态消息
		NewsFeedService.me.createProjectNewsFeed(accountId, project, referAccounts);

		ProjectService.me.clearHotProjectCache();     // 清缓存，以后改成更好的方式
		IndexService.me.clearCache();
	}

	public void update(int accountId, Project project) {
		if (Db.queryInt("select accountId from project where id=? limit 1", project.getId()) != accountId) {
			throw new RuntimeException("个人空间只能操作属于自己的项目");
		}
		project.update();

		ProjectService.me.clearHotProjectCache();     // 清缓存，以后改成更好的方式
		IndexService.me.clearCache();
	}

	/**
	 * 删除 project 之前，先删除 news_feed，NewsFeedService 会自动删除相应的 refer_me
	 * 注意：未来如果支持 project_reply 功能时，需要最先删除 project_project 相关的 news_feed、再删 project_reply，然后下面的代码
	 */
	public void delete(final int accountId, final int projectId) {
		// id 值小于等于 4 的项目，不允许删除，以免误删，损失过大
		// if (projectId <= 4) {
		// 	return ;
		// }

		Db.tx(new IAtom() {
			public boolean run() throws SQLException {
				// 先删除 news_feed
				NewsFeedService.me.deleteByProjectId(projectId);

                // 再删除收藏、点赞数据
                FavoriteService.me.deleteByProjectDeleted(projectId);
                LikeService.me.deleteByProjectDeleted(projectId);

				// 再删除 project
				return Db.update("delete from project where accountId=? and id=?", accountId, projectId) > 0;
			}
		});

		IndexService.me.clearCache();
	}
}

