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

package com.jfinal.club.document;

import com.jfinal.core.Controller;
import com.jfinal.club.common.interceptor.BaseSeoInterceptor;
import com.jfinal.club.common.model.Document;

/**
 * Document 搜索引擎优化
 */
public class DocumentSeo extends BaseSeoInterceptor {

	@Override
	public void indexSeo(Controller c) {

	}

	@Override
	public void detailSeo(Controller c) {

	}

	@Override
	public void othersSeo(Controller c, String method) {
		if (method.equals("doc")) {
			Document doc = c.getAttr("doc");
			if (doc != null) {
				setSeoTitle(c, "JFinal 文档、资料、学习、API，" + doc.getTitle());
				setSeoKeywords(c, "JFinal 文档, JFinal 教程, JFinal API, JFinal 入门, JFinal 资料, JFinal 学习");
				setSeoDescr(c, "JFinal 官方社区文档频道，提供最新、最优质、最权威、最全面的 JFinal 在线文档、资料、例子、API，是 JFinal 入门、提升、深入学习的最好资源，文档会经常更新，尽请关注 JFinal 社区动态");
			}
		}
	}
}
