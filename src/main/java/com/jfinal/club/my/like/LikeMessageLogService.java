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

package com.jfinal.club.my.like;

import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.club.common.account.AccountService;
import com.jfinal.club.common.model.Account;
import com.jfinal.club.my.message.MessageService;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 点赞消息业务
 * 当某位用户第一次对某个资源点赞成功后要向资源所有者发送系统消息
 * 发完消息以后需要向 like_message_log 中写入记录
 * 由于点赞可以是一个反复操作，为了避免多次发送系统消息引入此表
 */
public class LikeMessageLogService {

    public static final LikeMessageLogService me = new LikeMessageLogService();

    // 用于标识各种 refType 类型
    public static final int REF_TYPE_PROJECT = 1;
    // public static final int REF_TYPE_PROJECT_REPLY = 2;  // 暂时不用
    public static final int REF_TYPE_SHARE = 3;
    // public static final int REF_TYPE_SHARE_REPLY = 4;    // 暂时不用
    public static final int REF_TYPE_FEEDBACK = 5;
    // public static final int REF_TYPE_FEEDBACK_REPLY = 6; // 暂时不用

    @SuppressWarnings("serial")
	private static Map<String, Integer> map = new HashMap<String, Integer>(){{
        put("project", REF_TYPE_PROJECT);
        // put("project_reply", REF_TYPE_PROJECT_REPLY);    // 暂时不用
        put("share", REF_TYPE_SHARE);
        // put("share_reply", REF_TYPE_SHARE_REPLY);        // 暂时不用
        put("feedback", REF_TYPE_FEEDBACK);
        // put("feedback_reply", REF_TYPE_FEEDBACK_REPLY);  // 暂时不用
    }};

    public void sendSystemMessage(int myId, int userId, String refType, int refId) {
        try {
            doSendSystemMessage(myId, userId, refType, refId);
        } catch (Exception e) {
            LogKit.error(e.getMessage(), e);
        }
    }

    // TODO 考虑在单独的线程中调用，或者做成异步任务调度形式，提升性能
    private void doSendSystemMessage(int myId, int userId, String tableName, int refId) {
        String sql = "select accountId from like_message_log where accountId=? and refType=? and refId=?";
        Integer refType = getRefTypeValue(tableName);
        // 当 like_message_log 没有对应的记录时，才去发私信，否则证明已经发过私信
        if (Db.queryInt(sql, myId, refType, refId) == null) {
            Record r = new Record()
                    .set("accountId", myId)
                    .set("refType", refType)
                    .set("refId", refId)
                    .set("createAt", new Date());
            Db.save("like_message_log", r);

            saveSystemMessage(myId, userId, tableName, refType, refId);
        }
    }

    private void saveSystemMessage(int myId, int userId, String tableName, int refType, int refId) {
        Record ref = Db.findFirst("select id, title, likeCount from " + tableName + " where id=? limit 1", refId);
        // 被引用的资源存在时才去发私信，资源可能随时会被删除
        if (ref == null) {
            return ;
        }

        Account my = AccountService.me.getById(myId);
        String msg = "@" + my.getNickName() + " 刚刚赞了你的";
        if (refType == REF_TYPE_PROJECT) {
            msg = msg + "项目：<a href='/project/" + ref.getInt("id") +"' target='_blank'>" + ref.getStr("title");
        } else if (refType == REF_TYPE_SHARE) {
            msg = msg + "分享：<a href='/share/" + ref.getInt("id") +"' target='_blank'>" + ref.getStr("title");
        } else if (refType == REF_TYPE_FEEDBACK) {
            msg = msg + "反馈：<a href='/feedback/" + ref.getInt("id") +"' target='_blank'>" + ref.getStr("title");
        } else {
            throw new RuntimeException("refType 不正确，请告知管理员");
        }
        msg = msg + "</a>，目前被赞次数为：" + ref.getInt("likeCount");
        MessageService.me.sendSystemMessage(myId, userId, msg);
    }

    private Integer getRefTypeValue(String tableName) {
        Integer refType = map.get(tableName);
        if (refType == null) {
            throw new IllegalArgumentException("tableName 不正确");
        }
        return refType;
    }
}
