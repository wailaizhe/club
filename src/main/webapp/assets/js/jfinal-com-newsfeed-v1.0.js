/**
 * 设置个人空间最新动态 newsfeed 中的 tab
 */
function setCurrentNewsFeedTab() {
	var url = location.pathname, navMenus = $(".newsfeed-tabs li a");
	if (url == '/my') {
		navMenus.eq(0).addClass("current");
	} else if (!url.indexOf('/my/hot')) {
		navMenus.eq(1).addClass("current");
    } else if (!url.indexOf('/my/referMe')) {
		navMenus.eq(2).addClass("current");
	}
}

/**
 * 在 news feed reply item 中点击 "回复" 链接后，生成的 loading 效果 html 片段
 * js 动态拼接，缩减页面大小
  */
function getNewsFeedReplyListLoading() {
	return "<div class='newsfeed-loading'>" +
		"<img src='/assets/img/loading-2.gif'>" +
		"<span>&nbsp;&nbsp;正在加载，请稍候...&nbsp;&nbsp;&nbsp;</span>" +
		"</div>";
}

/**
 *  打开动态消息回复列表
 *  TODO 未来还可以考虑预埋一个 container div 来容纳 loading 与 reply list
 *   结合 $("#container_newsFeedId").append(html) 与
 *          $("#container_newsFeedId").empty() 实现
 *          还可以在第一次 append 过后，用 hide() 与 show() 实现类似缓存的效果
 */
function showNewsFeedReplyList(thisReplyLinkBtn, newsFeedId, atUser) {
	var replyLinkBtn = $(thisReplyLinkBtn);
	if (replyLinkBtn.text() == "回复") {
		doShowNewsFeedReplyList(replyLinkBtn, newsFeedId, atUser)
	} else {
		// 由于 ajax 是异步操作，所以在此也要对界面进行操作，
		// 同时在ajax 相关方法的success回调中也要有同样的操作
		replyLinkBtn.text("回复");
		// 删除可能存在的 loading 与 news feed reply list
		replyLinkBtn.parent().nextAll().remove();
	}
}
// ajax 获取 newsFeedReplyList，并插入到合理的地点
function doShowNewsFeedReplyList(replyLinkBtn, newsFeedId, atUser) {
	var position = replyLinkBtn.parent();
	$.ajax("/my/showReplyList", {
		type: "POST"
		, cache: false
		// 这里必须要用 html，因为后返回的是html，无返回解析成 json，造成异常，进而无响应
		, dataType: "html"
		, data: {
			newsFeedId: newsFeedId
		}
		, beforeSend: function() {
			replyLinkBtn.text("关闭");
			var loadingHtml = getNewsFeedReplyListLoading();
			position.after(loadingHtml);
		}
		, success: function(ret) {
            // 对于未登录用户，后端返回 renderHtml("notLogin")，则直接跳去登录页面
            if (ret == "notLogin") {
                location.href = "/login?returnUrl=" + location.pathname;
                return ;
            }

			// 只在按钮关闭时操作，用户在返回数据前可能已经点击了关闭，此时再插入列表会乱掉
			if (replyLinkBtn.text() == "关闭") {   // 经测试这个判断必须要，否则按钮显示"回复"时，列表被插进来了
				position.nextAll().remove();   // 移除 loading 以及可能存在的列表
				position.after(ret);                // 添加列表

				var textarea = position.next().find("textarea").focus();    // 添加 focus() 避免 IE 有时将光标停在最前方
				textarea.val("@" + atUser + " ").focus();   // textarea 添加 at 并设置焦点

				// 为动态添加的 "回复" 按钮以及 textare 添加事件
				bindSubmitEvent(newsFeedId, position, textarea, atUser);
			}
		}
		, error: function(XHR, errorInfo, exception) {
			var loading = position.next();
			loading.empty();
			loading.append("<p>数据加载失败，请重试！</p>");
		}
		, complete: function() {
		}
	});
}

/**
 *  为动态添加的 "回复" 按钮以及 textarea 添加事件，"回复"  的 click 与 textarea 的 ctrl + enter 事件
 */
function bindSubmitEvent(newsFeedId, position, textarea, atUser) {
	var newsFeedReplyListBox = position.next();
	var submitBtn = newsFeedReplyListBox.find("span.newsfeed-reply-submit");
	var submitLoading = newsFeedReplyListBox.find("img.newsfeed-reply-loading");

	var map = {
		isLoading: false
		, submit_btn: submitBtn
		, submit_loading: submitLoading
		, newsFeedId: newsFeedId
		, textarea: textarea
		, atUser: atUser
	};

	submitBtn.bind("click", function() {
		replyNewsFeed("/my/saveNewsFeedReply", map);
	});

	textarea.bind("keydown", function(e) {
		if ((e.ctrlKey || e.metaKey) && e.keyCode==13) {    // ctrl + 回车发送消息
			replyNewsFeed("/my/saveNewsFeedReply", map);   // $("#submit_btn").trigger("click");   // 简写 $("#submit_btn").click();
		}
	});
}
// news feed 回复功能
function replyNewsFeed(url, map) {
	if (map.isLoading) {
		return ;
	}

	$.ajax(url, {
		type: "POST"
		, cache: false
		, dataType: "json"
		, data: {
			newsFeedId: map.newsFeedId,
			replyContent: map.textarea.val()
		}
		, beforeSend: function() {
			if (map.textarea.val() == "@" + map.atUser + " ") {
				showReplyErrorMsg("请先输入要发送的内容！");
				return false;
			}

			map.isLoading = true;
			map.submit_btn.hide();
			map.submit_loading.show();
		}
		, success: function(ret) {
			if (ret.isOk) {
				map.textarea.focus();                                       // 添加 focus() 避免 IE 有时将光标停在最前方
				map.textarea.val("@" + map.atUser + " ").focus();    // 回复完成后，textarea 添加 at 并设置焦点
				map.textarea.css({height:"35px"});                    // 数据清空后，高度重置一下，注意高度与 css 文件中保持一致
				map.textarea.parent().parent().after(ret.replyItem);                     // 插入刚刚回复的内容 replyItem
			} else {
				showReplyErrorMsg(ret.msg);                   // 调用 jfinal-com-v1.0.js 的方法，与 share reply 中共用的功能
			}
		}
		, complete: function() {
			map.submit_loading.hide();
			map.submit_btn.show();
			map.isLoading = false;
		}
	});
}
