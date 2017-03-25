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

package com.jfinal.club.my.setting;

import com.jfinal.kit.HashKit;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.upload.UploadFile;
import com.jfinal.club.common.account.AccountService;
import com.jfinal.club.common.kit.ImageKit;
import com.jfinal.kit.Ret;
import com.jfinal.club.common.model.Account;
import com.jfinal.club.index.IndexService;
import com.jfinal.club.login.LoginService;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * MySettingService
 */
public class MySettingService {

	public static final MySettingService me = new MySettingService();
	private final Account accountDao = new Account().dao();

	// 经测试对同一张图片裁切后的图片 jpg为3.28KB，而 png 为 33.7KB，大了近 10 倍
	public static final String extName = ".jpg";

	/**
	 * 上传图像到临时目录，发回路径供 jcrop 裁切
	 */
	public Ret uploadAvatar(int accountId, UploadFile uf) {
		if (uf == null) {
			return Ret.fail("msg", "上传文件UploadFile对象不能为null");
		}

		try {
			if (ImageKit.notImageExtName(uf.getFileName())) {
				return Ret.fail("msg", "文件类型不正确，只支持图片类型：gif、jpg、jpeg、png、bmp");
			}

			String avatarUrl = "/upload" + getAvatarTempDir() + accountId + "_" + System.currentTimeMillis() + extName;
			String saveFile = PathKit.getWebRootPath() + avatarUrl;
			ImageKit.zoom(500, uf.getFile(), saveFile);
			return Ret.ok("avatarUrl", avatarUrl);
		}
		catch (Exception e) {
			return Ret.fail("msg", e.getMessage());
		} finally {
			uf.getFile().delete();
		}
	}

	public Ret saveAvatar(Account loginAccount, String avatarUrl, int x, int y, int width, int height) {
		int accountId = loginAccount.getId();
		// 暂时用的 webRootPath，以后要改成 baseUploadPath，并从一个合理的地方得到
		String webRootPath = PathKit.getWebRootPath() ;
		String avatarFileName = webRootPath + avatarUrl;

		try {
			// 相对路径 + 文件名：用于保存到 account.avatar 字段
			String[] relativePathFileName = new String[1];
			// 绝对路径 + 文件名：用于保存到文件系统
			String[] absolutePathFileName = new String[1];
			buildPathAndFileName(accountId, webRootPath, relativePathFileName, absolutePathFileName);

			BufferedImage bi = ImageKit.crop(avatarFileName, x, y, width, height);
			bi = ImageKit.resize(bi, 200, 200);     // 将 size 变为 200 X 200，resize 不会变改分辨率
			deleteOldAvatarIfExists(absolutePathFileName[0]);
			ImageKit.save(bi, absolutePathFileName[0]);

			AccountService.me.updateAccountAvatar(accountId, relativePathFileName[0]);
			LoginService.me.reloadLoginAccount(loginAccount);
			IndexService.me.clearCache();   // 首页的用户图片需要更新
			return Ret.ok("msg", "头像更新成功，部分浏览器需要按 CTRL + F5 强制刷新看效果");
		} catch (Exception e) {
			return Ret.fail("msg", "头像更新失败：" + e.getMessage());
		} finally {
			new File(avatarFileName).delete();	 // 删除用于裁切的源文件
		}
	}

	/**
	 * 目前该方法为空实现
	 * 如果在 linux 上跑稳了，此方法可以删除，不必去实现，如果出现 bug，
	 * 则尝试实现该方法，即当用户图像存在时再次上传保存，则先删除老的，
	 * 以免覆盖老文件时在 linux 之上出 bug
	 */
	private void deleteOldAvatarIfExists(String oldAvatar) {

	}

	// 用户上传图像最多只允许 1M大小
	public int getAvatarMaxSize() {
		return 1024 * 1024;
	}

	/**
	 * 上传文件，以及上传后立即缩放后的文件暂存目录
	 */
	public String getAvatarTempDir() {
		return "/avatar/temp/";
	}

	/**
	 * 1：生成保存于 account.avatar 字段的：相对路径 + 文件名，存放于 relativePathFileName[0]
	 * 2：生成保存于文件系统的：绝对路径 + 文件名，存放于 absolutePathFileName[0]
	 *
	 * 3：用户头像保存于 baseUploadPath 之下的 /avatar/ 之下
	 * 4：account.avatar 只存放相对于 baseUploadPath + "/avatar/" 之后的路径和文件名
	 *    例如：/upload/avatar/0/123.jpg 只存放 "0/123.jpg" 这部分到 account.avatar 字段之中
	 *
	 * 5："/avatar/" 之下生成的子录为 accountId 对 5000取整，例如 accountId 为 123 时，123 / 5000 = 0，生成目录为 "0"
	 * 6：avatar 文件名为：accountId + ".jpg"
	 */
	private void buildPathAndFileName(int accountId, String webRootPath, String[] relativePathFileName, String[] absolutePathFileName) {
		String relativePath = (accountId / 5000) + "/";
		String fileName = accountId + extName;
		relativePathFileName[0] = relativePath + fileName;

		String absolutePath = webRootPath + "/upload/avatar/" + relativePath;   // webRootPath 将来要根据 baseUploadPath 调整，改代码，暂时选先这样用着，着急上线
		File temp = new File(absolutePath);
		if (!temp.exists()) {
			temp.mkdirs();  // 如果目录不存在则创建
		}
		absolutePathFileName[0] = absolutePath + fileName;
	}

	/**
	 * 修改密码
	 */
	public Ret updatePassword(int accountId, String oldPassword, String newPassword) {
		if (StrKit.isBlank(oldPassword)) {
			return Ret.fail("msg", "原密码不能为空");
		}
		if (StrKit.isBlank(newPassword)) {
			return Ret.fail("msg", "新密码不能为空");
		}
		if (newPassword.length() < 6) {
			return Ret.fail("msg", "新密码长度不能小于 6");
		}

		Account account = accountDao.findById(accountId);
		String salt = account.getSalt();
		String hashedPass = HashKit.sha256(salt + oldPassword);
		if ( ! hashedPass.equals(account.getPassword())) {
			return Ret.fail("msg", "原密码不正确，请重新输入");
		}

		salt = HashKit.generateSaltForSha256();
		newPassword = HashKit.sha256(salt + newPassword);
		int result = Db.update("update account set password=?, salt=? where id=? limit 1", newPassword, salt, accountId);
		if (result > 0) {
			return Ret.ok("msg", "密码更新成功");
		} else {
			return Ret.fail("msg", "未找到账号，请联系管理员");
		}
	}
}



