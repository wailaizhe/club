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

package com.jfinal.club.common.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

/**
 * SEO 搜索引擎优化基础拦截器
 */
public abstract class BaseSeoInterceptor implements Interceptor {

	public static final String SEO_TITLE = "seoTitle";
	public static final String SEO_KEYWORDS = "seoKeywords";
	public static final String SEO_DESCR = "seoDescr";

	protected void setSeoTitle(Controller c, String seoTitle) {
		c.setAttr(SEO_TITLE, seoTitle);
	}

	protected void setSeoKeywords(Controller c, String seoKeywords) {
		c.setAttr(SEO_KEYWORDS, seoKeywords);
	}

	protected void setSeoDescr(Controller c, String seoDescr) {
		c.setAttr(SEO_DESCR, seoDescr);
	}

	public final void intercept(Invocation inv) {
		inv.invoke();

		Controller c = inv.getController();
		String method = inv.getMethodName();
		if (method.equals("index")) {
			if (c.getPara() == null) {
				indexSeo(c);
			} else {
				detailSeo(c);
			}
		} else if (method.equals("detail")) {
			detailSeo(c);
		} else {
			othersSeo(c, method);
		}
	}

	// 对 index() action 进行 seo
	public abstract void indexSeo(Controller c) ;

	// 对 detail() action 进行 seo
	public abstract void detailSeo(Controller c) ;

	// 对其它方法进行 seo，只需在该方法的实现类中判断一下 method 参数名
	// 并调用前面的 seoTitle、setSeoKeywords、setSeoDescr 三个工具方法即可完成 seo
	public abstract void othersSeo(Controller c, String method);
}



