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

package com.jfinal.club.common.account;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.club.common.model.Account;
import java.util.List;

/**
 * 账户业务
 */
@SuppressWarnings("rawtypes")
public class AccountService {

	public static final AccountService me = new AccountService();
	private final Account dao = new Account().dao();
	private final String allAccountsCacheName = "allAccounts";

	public void updateAccountAvatar(int accountId, String relativePathFileName) {
		Db.update("update account set avatar=? where id=? limit 1", relativePathFileName, accountId);
        clearCache(accountId);
	}

	/**
	 * 通过nickName获取Account对象，仅返回指定的字段，多字段用逗号分隔
	 */
	public Account getByNickName(String nickName, String columns) {
		if (StrKit.isBlank(nickName)) {
			return null;
		}
		return dao.findFirst("select " + columns +" from account where nickName=?  and status=?  limit 1", nickName, Account.STATUS_OK);
	}

	/**
	 * 通过 id 获取Account对象，只能获取到未被 block 的 account
	 */
	public Account getUsefulById(int accountId) {
		// return dao.findFirst("select " + columns +" from account where id=? and status=? limit 1", accountId, Account.STATUS_OK);
        Account account = getById(accountId);
        return account.isStatusOk() ? account : null;
	}

    /**
     * 优先从缓存中获取 account 对象，可获取到被 block 的 account
     */
    public Account getById(int accountId) {
        // 优先从缓存中取，未命中缓存则从数据库取
        Account account = CacheKit.get(allAccountsCacheName, accountId);
        if (account == null) {
            // 考虑到可能需要 join 状态不合法的用户，先放开 status 的判断
            // account = dao.findFirst("select * from account where id=? and status=? limit 1", accountId, Account.STATUS_OK);
            account = dao.findFirst("select * from account where id=? limit 1", accountId);
            if (account != null) {
                account.removeSensitiveInfo();
                CacheKit.put(allAccountsCacheName, accountId, account);
            }
        }
        return account;
    }

	public void joinNickNameAndAvatar(List<? extends Model> modelList) {
		join("accountId", modelList, "nickName", "avatar");
	}

	public void joinNickNameAndAvatar(Model model) {
		join("accountId", model, "nickName", "avatar");
	}

	public void join(String joinOnField, List<? extends Model> modelList, String... joinAttrs) {
		if (modelList != null) {
			for (Model m : modelList) {
				join(joinOnField, m, joinAttrs);
			}
		}
	}

	/**
	 * 在 Project、Share、Feedback、NewsFeed 等模块，需要关联查询获取 Account 对象的 nickName、avatar 时使用此方法
	 * 避免使用关联查询，优化性能，在使用中更关键的地方在于缓存的清除
	 * @param joinOnField join 使用的字段名，account 这端使用 id
	 * @param model 需要 join 的 model
	 * @param joinAttrs 需要 join 到 model 中的的属性名称
	 */
	public void join(String joinOnField, Model model, String... joinAttrs) {
		Integer accountId = model.getInt(joinOnField);
		if (accountId == null) {
			throw new RuntimeException("Model 中的 \"" + joinOnField  + "\" 属性值不能为 null");
		}

        Account account = getById(accountId);

        // join 真正开始的地方，前面是准备工作
		if (account != null) {
			for (String attr : joinAttrs) {
				model.put(attr, account.get(attr));
			}
		} else {
			throw new RuntimeException("未找到 account 或者 account 状态不合法，account 的 id 值为：" + accountId + " 可能是数据库数据不一致");
		}
	}

    /**
     * 更新 likeCount 字段
     * TODO 未来做成延迟更新模式
     */
    private void updateLikeCount(int accountId, boolean isAdd) {
        String sql = isAdd ?
                "update account set likeCount=likeCount+1 where id=? limit 1" :
                "update account set likeCount=likeCount-1 where id=? and likeCount > 0 limit 1";
        int n = Db.update(sql, accountId);
        if (n > 0) {
            // 直接更新缓存中的 likeCount 值
            Account account = CacheKit.get(allAccountsCacheName, accountId);
            if (account != null) {
                account.setLikeCount(account.getLikeCount() + (isAdd ? 1 : -1));
            }
        }
    }

    /**
     * likeCount 增加 1
     */
    public void addLikeCount(int accountId) {
        updateLikeCount(accountId, true);
    }

    /**
     * likeCount 减去 1
     */
    public void minusLikeCount(int accountId) {
        updateLikeCount(accountId, false);
    }

	/**
	 * 根据 accountId 值移除缓存
	 */
	public void clearCache(int accountId) {
		CacheKit.remove(allAccountsCacheName, accountId);
	}
}



