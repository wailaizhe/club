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

package com.jfinal.club.my.feedback;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import com.jfinal.club.common.kit.SensitiveWordsKit;
import com.jfinal.club.my.project.MyProjectService;

/**
 * 我的项目表单提交校验
 */
public class MyFeedbackValidator extends Validator {

	protected void validate(Controller c) {
		setShortCircuit(true);

		checkSensitiveWords(c.getPara("feedback.title"), "反馈标题 title 包含敏感词");
		checkSensitiveWords(c.getPara("feedback.content"), "反馈内容 content 名包含敏感词");

		validateString("feedback.title", 3, 100, "msg", "标题长度要求在3到100个字符");

		validateInteger("feedback.projectId", "msg", "请选择关联项目");
		if ( ! MyProjectService.me.isProjectIdExists(c.getParaToInt("feedback.projectId")) ) {
			addError("msg", "关联项目不存在，请联系管理员");
		}

		validateString("feedback.content", 19, 65536, "msg", "正文内容太少啦，多写点哈");
	}

	private void checkSensitiveWords(String value, String msg) {
		if (SensitiveWordsKit.checkSensitiveWord(value) != null) {
			addError("msg", msg);
		}
	}

	protected void handleError(Controller c) {
		c.renderJson();
	}
}
