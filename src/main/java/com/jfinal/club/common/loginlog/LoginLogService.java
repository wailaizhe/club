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

package com.jfinal.club.common.loginlog;

/**
 * 用户登录时做日志，便于统计活跃用户
 * 用缓存缓冲一下，不要每次都写库
 *
 * 例如缓存设置为
 * map {accountId, date, times}
 *
 * 用户的 session 过期时间设置为 30 分钟，如果过期就会触发登录操作
 * 另外注意，登录操作可能是利用 cookie 值自动实现的，但是这个自动实现也
 * 算做是一次登录，相当于只要  ehcache 中没有用户 session，建立这个 session就算做是一次登录
 *
 * 集群部署时需要考虑 ehcache 同步问题
 */
public class LoginLogService {

	public static final LoginLogService me = new LoginLogService();

}
