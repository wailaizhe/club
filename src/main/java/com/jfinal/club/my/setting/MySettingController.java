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

import com.jfinal.aop.Before;
import com.jfinal.upload.UploadFile;
import com.jfinal.club.common.controller.BaseController;
import com.jfinal.club.common.interceptor.FrontAuthInterceptor;
import com.jfinal.kit.Ret;
import com.jfinal.club.common.model.Account;
import com.jfinal.club.login.LoginService;
import com.jfinal.club.my.friend.FriendInterceptor;
import com.jfinal.club.my.like.LikeInterceptor;

/**
 * 我的设置
 */
@Before({FrontAuthInterceptor.class, FriendInterceptor.class, LikeInterceptor.class})
public class MySettingController extends BaseController {

	public static final MySettingService srv = MySettingService.me;

	public void info() {
		render("info.html");
	}

	/**
	 * 上传用户图片，为裁切头像做准备
	 */
	public void uploadAvatar() {
		UploadFile uf = null;
		try {
			uf = getFile("avatar", srv.getAvatarTempDir(), srv.getAvatarMaxSize());
			if (uf == null) {
				renderJson(Ret.fail("msg", "请先选择上传文件"));
				return;
			}
		} catch (Exception e) {
			// 经测试，暂时拿不到这个异常，需要改进 jfinal 才可以拿得到
			if (e instanceof com.oreilly.servlet.multipart.ExceededSizeException) {
				renderJson(Ret.fail("msg", "文件大小超出范围"));
			} else {
				if (uf != null) {
					// 只有出现异常时才能删除，不能在 finally 中删，因为后面需要用到上传文件
					uf.getFile().delete();
				}
				renderJson(Ret.fail("msg", e.getMessage()));
			}
			return ;
		}

		Ret ret = srv.uploadAvatar(123456, uf);
		if (ret.isOk()) {   // 上传成功则将文件 url 径暂存起来，供下个环节进行裁切
			setSessionAttr("avatarUrl", ret.get("avatarUrl"));
		}
		renderJson(ret);
	}

	/**
	 * 保存 jcrop 裁切区域为用户头像
	 */
	public void saveAvatar() {
		String avatarUrl = getSessionAttr("avatarUrl");
		int x = getParaToInt("x");
		int y = getParaToInt("y");
		int width = getParaToInt("width");
		int height = getParaToInt("height");
		Ret ret = srv.saveAvatar(getLoginAccount(), avatarUrl, x, y, width, height);
		renderJson(ret);
	}

	public void password() {
		render("password.html");
	}

	public void updatePassword() {
		Account loginAccount = getAttr(LoginService.loginAccountCacheName);
		Ret ret = srv.updatePassword(loginAccount.getId(), getPara("oldPassword"), getPara("newPassword"));
		renderJson(ret);
	}

	/**
	 * TODO 加一个通用设置功能，或者通知设置，可以设置，是否接收邮件通知、微信通知
	 * 菜单名可能叫：设置通知
	 */
	public void notice() {

	}
}
