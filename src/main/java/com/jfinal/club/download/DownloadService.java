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

package com.jfinal.club.download;

import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.kit.Ret;
import com.jfinal.club.common.model.Account;
import com.jfinal.club.common.model.Download;
import java.util.Date;
import java.util.List;

/**
 * 下载业务
 */
public class DownloadService {
	public static DownloadService me = new DownloadService();
	private final Download dao = new Download().dao();

	/**
	 * 用于在首页显示的下载列表
	 */
	public List<Download> getDownloadList() {
		return dao.findByCache("download", "downloadList", "select * from download where isShow = 1");
	}

	public void clearCache() {
		CacheKit.remove("download", "downloadList");
	}

	public Ret download(Account loginAccount, String fileName, String ip) {
		Download download = dao.findFirst("select * from download where fileName = ?", fileName);
		if (download != null) {
			try {
				processDownloadCount(loginAccount, download, ip);
			} catch (Exception e) {
				LogKit.error(e.getMessage(), e);
			}
			return Ret.ok("fullFileName", download.getPath() + download.getFileName());
		} else {
			return Ret.fail("msg", "文件未找到：" + fileName);
		}
	}

	/**
	 * 每个 ip 每天对于每个文件只统计一次下载量
	 */
	private void processDownloadCount(Account loginAccount, Download download, String ip) {
		String sql = "select ip from download_log where ip=? and downloadDate = ? and fileName = ? limit 1";
		String todayDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date());
		if (Db.query(sql, ip, todayDate, download.getFileName()).size() == 0) {
			Db.update("update download set downloadCount = downloadCount + 1 where id = ?", download.getId());
			Record downloadLog = new Record().set("ip", ip).set("fileName", download.getFileName()).set("downloadDate", new Date());
			downloadLog.set("accountId", loginAccount.getId());
			Db.save("download_log", downloadLog);
		}
	}
}
