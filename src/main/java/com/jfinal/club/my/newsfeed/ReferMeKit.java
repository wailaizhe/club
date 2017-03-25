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

package com.jfinal.club.my.newsfeed;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.club.common.account.AccountService;
import com.jfinal.club.common.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @提到我工具类
 */
public class ReferMeKit {

	static final Pattern p = Pattern.compile("@([^@\\s:,;：，；　<&]{1,})([\\s:,;：，；　<&]{0,1})");

	/**
	 * 匹配 @xxx 生成链接，将生成过链接的账号 id 存放在 referAccounts，以供后续生成 remind 记录
	 */
	public static String buildAtMeLink(String content, List<Integer> referAccounts) {
		if (StrKit.isBlank(content)) {
			return content;
		}

		StringBuilder ret = new StringBuilder();
		Matcher matcher = p.matcher(content);
		int pointer = 0;
		while (matcher.find()) {
			ret.append(content.substring(pointer, matcher.start()));
			String nickName = matcher.group(1);

			Account account = AccountService.me.getByNickName(nickName, "id");
			if (account != null) {
				ret.append("<a href=\"/user/").append(account.getId())
						.append("\" target=\"_blank\" class=\"at-me\">")
						.append("@").append(nickName).append("</a>");
				ret.append(matcher.group(2));

				if ( !referAccounts.contains(account.getId()) ) {
					referAccounts.add(account.getId());
				}
			} else {
				ret.append(matcher.group());
			}

			pointer = matcher.end();
		}
		ret.append(content.substring(pointer));
		return ret.toString();
	}

	/**
	 * 将 model 中的 attrName 属性内容创建 at me 链接
	 */
	public static List<Integer> buildAtMeLink(Model model, String attrName) {
		List<Integer> referAccounts = new ArrayList<Integer>();
		String content = model.getStr(attrName);
		if (StrKit.notBlank(content)) {
			content = buildAtMeLink(content, referAccounts);
			model.set(attrName, content);
		}
		return referAccounts;
	}

	/**
	 * 将 model 中的 content 属性内容创建 at me 链接
	 */
	public static List<Integer> buildAtMeLink(Model model) {
		return buildAtMeLink(model, "content");
	}
}
