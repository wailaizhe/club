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

package com.jfinal.club.common.model;

import com.jfinal.club.common.model.base.BaseRemind;

/**
 * 提醒，用于在右上角弹出层显示各类提醒
 * accountId   int(11)    主键
 * referMe      int(11)    @提到我 的消息条数
 * message     int(11)    私信 消息条数，暂时不用
 */
@SuppressWarnings("serial")
public class Remind extends BaseRemind<Remind> {
    // 提醒类型
	public static final int TYPE_REFER_ME = 0;      // 非数据库字段，仅用于在业务层判断所操作的类型
    public static final int TYPE_MESSAGE = 1;       // 非数据库字段，仅用于在业务层判断所操作的类型
    public static final int TYPE_FANS = 2;          // 非数据库字段，仅用于在业务层判断所操作的类型
}
