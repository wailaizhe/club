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

package com.jfinal.club.common.kit;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import java.util.ArrayList;
import java.util.List;

/**
 * 敏感词检测
 */
public class SensitiveWordsKit {

	private static final List<String> sensitiveWords = build();

	private static List<String> build() {
		ArrayList<String> ret = new ArrayList<String>();
		List<Record> list = Db.find("select * from sensitive_words");
		for (Record r : list) {
			ret.add(r.getStr("word"));
		}
		return ret;
	}

	/**
	 * 在 MyProjectValidator 中checkSensitiveWords(c.getPara("project.content"),....) 这行调用
	 * 在第一次调用时传入 null 时 target 为 String[1]对象，而里面的内容为 null，第二次调用 target 则为 null
	 */
	public static String checkSensitiveWord(String... target) {
		if (target != null) {
			for (String s : target) {
				if (s != null) {
					for (String sensitiveWord : sensitiveWords) {
						if (s.indexOf(sensitiveWord) >= 0) {
							return sensitiveWord;
						}
					}
				}
			}
		}
		return null;
	}
}
