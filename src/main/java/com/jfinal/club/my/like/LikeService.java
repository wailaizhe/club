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

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.club.common.account.AccountService;
import com.jfinal.kit.Ret;
import com.jfinal.club.common.model.Account;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * 个人空间、用户空间的粉丝数量旁边添加一个赞数量： 关注(19)  粉丝(999)  赞(9999)
 *
 * 对于 reply list item 中的点赞按钮颜色可以淡些，鼠标放上去颜色深点
 *
 * 点赞功能，每篇 article、reply 都可以被人点赞
 * project、project_like、share、share_like ...
 *
 */
public class LikeService {

    public static final LikeService me = new LikeService();

    final String REF_TYPE_PROJECT = "project";
    final String REF_TYPE_SHARE = "share";
    final String REF_TYPE_FEEDBACK = "feedback";

    // 用户前端传入非法参数引发安全问题
    @SuppressWarnings("serial")
	private final Set<String> permissionTables = new HashSet<String>(){{
        add(REF_TYPE_PROJECT);
        add(REF_TYPE_SHARE);
        add(REF_TYPE_FEEDBACK);
    }};

    private void check(String refType) {
        if ( !permissionTables.contains(refType) ) {
            throw new IllegalArgumentException("refType 不正确");
        }
    }

    /**
     * 点赞
     * @param myId 点赞的用户 id，即当前登录用户
     * @param refType 被点赞的表名
     * @param refId 被点赞的表名中的相应的 id 值
     * @param isAdd true 为点赞，false 为取消点赞，null 需要判断是否已被点赞
     */
    public Ret like(int myId, String refType, int refId, Boolean isAdd) {
        check(refType);
        if (isAdd != null) {
            if (isAdd) {
                return save(myId, refType, refId);
            } else {
                return delete(myId, refType, refId);
            }
        } else {
            return like(myId, refType, refId);
        }
    }

    private Ret like(int myId, String refType, int refId) {
        if (isLike(myId, refType, refId)) {
            return delete(myId, refType, refId);
        } else {
            return save(myId, refType, refId);
        }
    }

    // 获取被点赞资源的创建者
    private Integer getUserIdOfRef(String refType, int refId) {
        return Db.queryInt("select accountId from " + refType + " where id=? limit 1", refId);
    }

    /**
     * 点赞
     */
    private Ret save(final int myId, final String refType, final int refId) {
        final Integer userId = getUserIdOfRef(refType, refId);
        if (userId == null) {
            return Ret.fail("msg", "未找到资源，可能已经被删除");
        }
        if (myId == userId) {
            return Ret.fail("msg", "不能给自己点赞");
        }
        // 如果已经点过赞，则直接退出
        if (isLike(myId, refType, refId)) {
            return Ret.fail("msg", "已经点赞，请刷新页面");
        }
        boolean isOk = Db.tx(new IAtom() {
            public boolean run() throws SQLException {
                int n = Db.update("insert into " + refType + "_like(accountId, refId, createAt) value(?, ?, now())", myId, refId);
                if (n > 0) {
                    n = Db.update("update " + refType + " set likeCount=likeCount+1 where id=? limit 1", refId);
                    if (n > 0) {
                        AccountService.me.addLikeCount(userId);
                    }
                }

                return n > 0;
            }
        });
        if (isOk) {
            // 向被赞的人发送私信，鼓励创造更多资源
            LikeMessageLogService.me.sendSystemMessage(myId, userId, refType, refId);
        }
        return isOk ? Ret.ok() : Ret.fail("msg", "点赞失败");
    }

    /**
     * 取消点赞
     */
    private Ret delete(final int myId, final String refType, final int refId) {
        boolean isOk = Db.tx(new IAtom() {
            public boolean run() throws SQLException {
                int n = Db.update("delete from " + refType + "_like where accountId=? and refId=? limit 1", myId, refId);
                if (n > 0) {
                    n = Db.update("update " + refType + " set likeCount=likeCount-1 where id=? and likeCount>0 limit 1", refId);
                    Integer userId = getUserIdOfRef(refType, refId);
                    if (userId != null) {
                        AccountService.me.minusLikeCount(userId);
                    }
                }
                return n > 0;
            }
        });
        return isOk ? Ret.ok() : Ret.fail("msg", "取消点赞失败");
    }

    /**
     * 对 refType + refId 指向的资源，是否已点赞
     */
    public boolean isLike(int accountId, String refType, int refId) {
        String sql = "select accountId from " + refType + "_like where accountId=? and refId=? limit 1";
        return Db.queryInt(sql, accountId, refId) != null;
    }

    /**
     * 设置 article detail 页面的点赞状态
     */
    @SuppressWarnings("rawtypes")
	public void setLikeStatus(Account loginAccount, String refType, Model refObj, Ret ret) {
        if (loginAccount != null) {
            boolean isLike = isLike(loginAccount.getId(), refType, refObj.getInt("id"));
            ret.set("isLikeActive", isLike ? "active" : "");
            ret.set("isLikeAdd", isLike ? "false" : "true");
        } else {
            ret.set("isLikeActive", "");
            ret.set("isLikeAdd", "true");
        }
        int likeCount = refObj.getInt("likeCount");
        ret.set("likeCount", likeCount > 0 ? likeCount : "");
    }

    /**
     * 删除被引用的资源时，要删除相关的点赞记录
     */
    private void deleteByRefDeleted(String refType, int refId) {
        Db.update("delete from " + refType + "_like where refId=?", refId);
    }

    /**
     * 删除 project 时，要删除相关的点赞记录
     */
    public void deleteByProjectDeleted(int refId) {
        deleteByRefDeleted(this.REF_TYPE_PROJECT, refId);
    }

    /**
     * 删除 share 时，要删除相关的点赞记录
     */
    public void deleteByShareDeleted(int refId) {
        deleteByRefDeleted(this.REF_TYPE_SHARE, refId);
    }

    /**
     * 删除 feedback 时，要删除相关的点赞记录
     */
    public void deleteByFeedbackDeleted(int refId) {
        deleteByRefDeleted(this.REF_TYPE_FEEDBACK, refId);
    }
}

