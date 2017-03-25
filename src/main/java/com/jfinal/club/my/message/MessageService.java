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

package com.jfinal.club.my.message;

import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.club.common.account.AccountService;
import com.jfinal.kit.Ret;
import com.jfinal.club.common.model.Message;
import com.jfinal.club.my.newsfeed.ReferMeKit;
import com.jfinal.club.my.newsfeed.RemindService;
import java.sql.SQLException;
import java.util.Date;

/**
 * 我的消息业务
 */
public class MessageService {

    public static final MessageService me = new MessageService();
    final Message dao = new Message().dao();
    final int pageSize = 15;

    /**
     * 所有私信分页
     */
    public Page<Message> paginate(int pageNum, int accountId) {
        String select = "select m.*, t.msgCount ";
        String from = "from (select max(id) as maxId, count(id) as msgCount from message where user=? group by friend " +
                                    ") as t inner join message m where t.maxId=m.id order by m.id desc";
        Page<Message> messagePage = dao.paginate(pageNum, pageSize, select, from, accountId);
        AccountService.me.join("friend", messagePage.getList(), "nickName", "avatar");
        return messagePage;
    }

    /**
     * 与某个用户的私信分页
     */
    public Page<Message> paginate(int pageNum, int accountId, int friendId) {
        String select = "select *";
        String from = "from message where user=? and friend=? order by id desc";
        Page<Message> messagePage = dao.paginate(pageNum, pageSize, select, from, accountId, friendId);
        AccountService.me.join("sender", messagePage.getList(), "nickName", "avatar");
        return messagePage;
    }

    /**
     * 发送普通私信
     */
    public Ret send(int sender, int receiver, String content) {
        return send(sender, receiver, Message.TYPE_NORMAL, content);
    }

    /**
     * 发送系统私信
     */
    public Ret sendSystemMessage(int sender, int receiver, String content) {
        return send(sender, receiver, Message.TYPE_SYSTEM, content);
    }

    /**
     * 发送私信
     */
    private Ret send(final int sender, final int receiver, final int type, final String content) {
        if (sender == receiver) {
            return Ret.fail("msg", "不能给自己发送私信");
        }
        if (type < Message.TYPE_NORMAL || type > Message.TYPE_SYSTEM) {
            throw new IllegalArgumentException("type 值不正确");
        }
        final Ret ret = Ret.create();
        final Message m1 = new Message();
        boolean isOk = Db.tx(new IAtom() {
            public boolean run() throws SQLException {
                m1.setUser(receiver);
                m1.setFriend(sender);
                m1.setSender(sender);
                m1.setReceiver(receiver);
                m1.setType(type);
                m1.setContent(content);
                m1.setCreateAt(new Date());
                ReferMeKit.buildAtMeLink(m1);   // 转换 @提到我
                RemindService.me.createRemindOfMessage(receiver);   // 向收信人发一个提醒

                // 系统消息，只保留收信人的信息，不创建发件人的信息
                if (type == Message.TYPE_SYSTEM) {
                	ret.set("message", m1);
                    return m1.save();
                }

                // 如果是"非系统消息" 同时为发信人保存一条信息，例如：点赞功能发送的是系统消息，但发信人是点赞的人
                Message m2 = new Message();
                m2.setUser(sender);
                m2.setFriend(receiver);
                m2.setSender(sender);
                m2.setReceiver(receiver);
                m2.setType(type);
                m2.setContent(content);
                m2.setCreateAt(new Date());
                ReferMeKit.buildAtMeLink(m2);   // 转换 @提到我
                ret.set("message", m2);
                return m1.save() && m2.save();
            }
        });
        if (isOk) {
        	return ret.setOk();
        } else {
            String msg = "消息发送失败，请告知管理员";
            LogKit.error(msg);
            return Ret.fail("msg", msg);
        }
    }

    /**
     * 删除某一条私信
     * message.user 字段表示 message 记录的主人，message 只有主人才可以删除
     */
    public Ret deleteByMessageId(int accountId, int messageId) {
        Db.update("delete from message where user=? and id=?", accountId, messageId);
        return Ret.ok();
    }

    /**
     * 删除某一个用户的所有私信往来
     * message.user 字段表示 message 记录的主人，message 只有主人才可以删除
     */
    public Ret deleteByFriendId(int accountId, int friendId) {
        Db.update("delete from message where user=? and friend=?", accountId, friendId);
        return Ret.ok();
    }
}
