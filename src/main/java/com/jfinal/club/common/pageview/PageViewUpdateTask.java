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
import com.jfinal.plugin.cron4j.ITask;
import java.util.Date;

/**
 * 定时更新 project_page_view、share_page_view、feedback_page_view
 *
 * 目前暂定为每小时的 0 分这个时间点更新
 * cron 表达式为：0 * * * *
 */
public class PageViewUpdateTask implements ITask {

	public void run() {
		doUpdate();
	}

	public void stop() {
		doUpdate();
	}

	private void doUpdate() {
		PageViewService.me.updateToDataBase();

		// 每次调度启动时，向 task_run_log 写日志，用于检查调度的时间是否与预期的一致，避免出现 bug 却不知道
		Record taskRunLog = new Record().set("taskName", "PageViewUpdateTask").set("createAt", new Date());
		Db.save("task_run_log", taskRunLog);
	}
}
