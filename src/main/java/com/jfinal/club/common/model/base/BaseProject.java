package com.jfinal.club.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseProject<M extends BaseProject<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setAccountId(java.lang.Integer accountId) {
		set("accountId", accountId);
	}

	public java.lang.Integer getAccountId() {
		return get("accountId");
	}

	public void setName(java.lang.String name) {
		set("name", name);
	}

	public java.lang.String getName() {
		return get("name");
	}

	public void setTitle(java.lang.String title) {
		set("title", title);
	}

	public java.lang.String getTitle() {
		return get("title");
	}

	public void setContent(java.lang.String content) {
		set("content", content);
	}

	public java.lang.String getContent() {
		return get("content");
	}

	public void setCreateAt(java.util.Date createAt) {
		set("createAt", createAt);
	}

	public java.util.Date getCreateAt() {
		return get("createAt");
	}

	public void setClickCount(java.lang.Integer clickCount) {
		set("clickCount", clickCount);
	}

	public java.lang.Integer getClickCount() {
		return get("clickCount");
	}

	public void setReport(java.lang.Integer report) {
		set("report", report);
	}

	public java.lang.Integer getReport() {
		return get("report");
	}

	public void setLikeCount(java.lang.Integer likeCount) {
		set("likeCount", likeCount);
	}

	public java.lang.Integer getLikeCount() {
		return get("likeCount");
	}

	public void setFavoriteCount(java.lang.Integer favoriteCount) {
		set("favoriteCount", favoriteCount);
	}

	public java.lang.Integer getFavoriteCount() {
		return get("favoriteCount");
	}

}
