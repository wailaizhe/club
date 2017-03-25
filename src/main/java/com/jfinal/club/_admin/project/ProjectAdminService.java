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

package com.jfinal.club._admin.project;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.club.my.project.MyProjectService;

/**
 * project 管理业务
 * TODO ProjectAdminController 中的业务逻辑在将来挪到这里来
 */
public class ProjectAdminService {

	public static final ProjectAdminService me = new ProjectAdminService();

	/**
	 * 删除 project
	 */
	public void delete(int projectId) {
		Integer accountId = Db.queryInt("select accountId from project where id=? limit 1", projectId);
		if (accountId != null) {
			MyProjectService.me.delete(accountId, projectId);
		}
	}
}
