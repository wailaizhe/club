
$(document).ready(function() {
	setCurrentNavMenu();
});

/**
 *  采用问号挂参的方式，为 a 链接追加 returnUrl 参数
 */
function appendReturnUrl(target) {
	var returnUrl;
	var currentUrl = location.pathname;
	if (currentUrl.indexOf("/login") != 0 && currentUrl.indexOf("/reg") != 0) {
		returnUrl = "?returnUrl=" + currentUrl;
		var link = $(target);
		link.attr("href", link.attr("href") + returnUrl);
	}
	//else {
	//	if (location.search) {
	//		returnUrl =  location.search;
	//	} else {
	//		return ;
	//	}
	//}
	//var link = $(target);
	//link.attr("href", link.attr("href") + returnUrl);
}

/**
 * 退出登录
 */
function logout() {
	if (confirm('确定要退出登录？')) {
		location.href = '/logout';
	} 
}

/**
 * 设置当前导航菜单
 */
function setCurrentNavMenu() {
	var url = location.pathname, navMenus = $(".jf-nav-menu-box a");
	if (url == '/') {
		navMenus.eq(0).addClass("jf-nav-menu-current");
	} else if (!url.indexOf('/project')) {
		navMenus.eq(1).addClass("jf-nav-menu-current");
	} else if (!url.indexOf('/share')) {
		navMenus.eq(2).addClass("jf-nav-menu-current");
	} else if (!url.indexOf('/feedback')) {
		navMenus.eq(3).addClass("jf-nav-menu-current");
	} else if (!url.indexOf('/doc')) {
		navMenus.eq(4).addClass("jf-nav-menu-current");
	}
}

/**
 * 设置当前我的空间菜单
 */
function setCurrentMyMenu() {
	var url = location.pathname, navMenus = $(".jf-my-menu li");
	if (url == '/my' || url == '/my/hot' || url == '/my/referMe') {
		navMenus.eq(0).addClass("jf-my-menu-current");
	} else if (!url.indexOf('/my/share')) {
		navMenus.eq(1).addClass("jf-my-menu-current");
	} else if (!url.indexOf('/my/feedback')) {
		navMenus.eq(2).addClass("jf-my-menu-current");
	} else if (!url.indexOf('/my/project')) {
		navMenus.eq(3).addClass("jf-my-menu-current");
	} else if (!url.indexOf('/my/message')) {
		navMenus.eq(4).addClass("jf-my-menu-current");
	} else if (!url.indexOf('/my/favorite')) {
		navMenus.eq(5).addClass("jf-my-menu-current");
	} else if (!url.indexOf('/my/setting/info')) {
		navMenus.eq(6).addClass("jf-my-menu-current");
	} else if (!url.indexOf('/my/setting/password')) {
		navMenus.eq(7).addClass("jf-my-menu-current");
	}
	// else if (!url.indexOf('/my')) {
	// 	navMenus.eq(0).addClass("jf-my-menu-current");
	// }
}

/**
 * 设置某用户空间菜单，url: "/user/id"
 */
function setCurrentUserMenu() {
	var url = location.pathname, navMenus = $(".jf-my-menu li");
	if (!url.indexOf('/user/share')) {
		navMenus.eq(1).addClass("jf-my-menu-current");
	} else if (!url.indexOf('/user/feedback')) {
		navMenus.eq(2).addClass("jf-my-menu-current");
	} else if (!url.indexOf('/user/project')) {
		navMenus.eq(3).addClass("jf-my-menu-current");
	} else if (!url.indexOf('/user')) {
		navMenus.eq(0).addClass("jf-my-menu-current");
	}
}

/**
 *  textarea 不要设置 margin 值，否则 IE 下的 scrollHeight 会包含该值，用外部嵌套div来布局
 * @param ele 必须是 textarea，并且在外部需要将 overflow 设置为 hidden
 * @param minHeight 最小高度值
 */
function autoHeight(ele, minHeight) {
	minHeight = minHeight || 16;
	// ele.style.height = minHeight + "px";
	if (ele.style.height) {
		ele.style.height = (parseInt(ele.style.height) - minHeight ) + "px";
	}
	ele.style.height = ele.scrollHeight + "px";

	// 返回了: 29  30 30，后两个始终比前一个大一个 px，经测试前都就是少了一个px的border-bottom
	// alert(ele.clientHeight + " : " +ele.scrollHeight + " : " + ele.offsetHeight);
	// 或许这个 currHeight 留着有点用
	// ele.currHeight = ele.style.height;
}

// 来自 git.oschina.net 项目首页，只支持自动增高，不支持减高
// textarea 自动调整高度，绑定 onkeyup="textAreaAdjustHeight(this);"
// git.oschina.net 的 issue 回复实现减高功能，但找不到代码
function textareaAdjustHeightOsc(textarea) {
	var adjustedHeight = textarea.clientHeight;
	adjustedHeight = Math.max(textarea.scrollHeight, adjustedHeight);
	if (adjustedHeight > textarea.clientHeight) {
		textarea.style.height = adjustedHeight + 'px';
	}
}

// share、feedback 详情页回复功能
function reply(url, articleId, map) {
	if (map.isLoading) {
		return ;
	}

	$.ajax(url, {
		type: "POST"
		, cache: false
		, dataType: "json"
		, data: {
			articleId: articleId,
			replyContent: $('#replyContent').val()
		}
		, beforeSend: function() {
			map.isLoading = true;
			map.submit_btn.hide();
			map.submit_loading.show();
		}
		, success: function(ret) {
			if (ret.isOk) {
				var replyContent = $('#replyContent');
				replyContent.val("");
				// 数据清空后，高度重置一下，注意高度与 css 文件中保持一致
				replyContent.css({height:"30px"});
				// 插入刚刚回复的内容 replyItem
				// TODO 考虑用 news feed 模块的定位方案来改进一下，更优雅
				$(".jf-reply-list > li:last-child").before(ret.replyItem);
			} else {
				showReplyErrorMsg(ret.msg);
			}
		}
		, complete: function() {
			map.submit_loading.hide();
			map.submit_btn.show();
			map.isLoading = false;
		}
	});
}

// share、feedback 详情页回复链接的 at 功能
function atAndReply(nickName) {
	var replyContent = $('#replyContent');
	var content = replyContent.val() + "@" + nickName + " ";
	replyContent.val(content);
}

/**
 * share、feedback 详情页回复错误信息提示框，需要引入 layer.js
 *  news feed 模块的 replyNewsFeed(...) 也用到此方法，在演化时注意
  */
function showReplyErrorMsg(msg) {
	layer.msg(msg, {
			shift: 6
			, shade: 0.4
			, time: 2000
			// , offset: "140px"
			, closeBtn: 1
			, shadeClose: true
			,maxWidth: "1000"
		}, function () {}
	);
}

/**
 * ajax GET 请求封装，提供了一些默认参数
 */
function ajaxGet(url, options) {
	var defaultOptions = {
		type: "GET"
		, cache: false      // 经测试设置为 false 时，ajax 请求会自动追加一个参数 "&_=nnnnnnnnnnn"
		, dataType: "json"  // "json" "text" "html" "jsonp"，如果设置为"html"，其中的script会被执行
		// , data: {}
		// , timeout: 9000     // 毫秒
		// , beforeSend: function(XHR) {}
		, success: function(ret){
			if (ret.isOk) {
				alert(ret.msg ? ret.msg : "操作成功");
			} else {
				alert("操作失败：" + (ret.msg ? ret.msg : "请告知管理员！"));
			}
		}
		, error: function(XHR, msg) {
			showReplyErrorMsg(msg); // 默认调用
		}
		// , complete: function(XHR, msg){} // 请求成功与失败都调用
	};
	// 用户自定义参数覆盖掉默认参数
	for(var o in options) {
		defaultOptions[o] = options[o];
	}

	$.ajax(url, defaultOptions);
}

/**
 * 确认对话框层，点击确定才真正操作
 * @param msg 对话框的提示文字
 * @param actionUrl 点击确认后请求到的目标 url
 * @param options jquery $.ajax(...) 方法的 options 参数
 */
function confirmAjaxGet(msg, actionUrl, options) {
	layer.confirm(msg, {
		icon: 0
		, title:''                                      // 设置为空串时，title消失，并自动切换关闭按钮样式，比较好的体验
		, shade: 0.4
		, offset: "139px"
	}, function(index) {                                // 只有点确定后才会回调该方法
		// location.href = operationUrl;                // 操作是一个 GET 链接请求，并非 ajax
		// 替换上面的 location.href 操作，改造成 ajax 请求。后端用 renderJson 更方便，不需要知道 redirect 到哪里
		ajaxGet(actionUrl, options);
		layer.close(index);                             // 需要调用 layer.close(index) 才能关闭对话框
	});
}

// share、feedback 详情页 reply 删除功能
function deleteReply(deleteBtn, url) {
	confirmAjaxGet("删除后无法恢复，确定要删除？", url, {
		success: function(ret) {
			if (ret.isOk) {
				$(deleteBtn).parents(".jf-reply-list li").remove();
			}
		}
	});
}

// 添加好友功能，用于关注/粉丝列表页面
function addFriend(btn, friendId) {
	layer.msg("正在加载，请稍后！", {icon: 16, offset: '100px'});
	ajaxGet("/friend/add?friendId=" + friendId, {
		success: function(ret) {
			if (ret.isOk) {
				setFriendBtn($(btn), friendId, ret.friendRelation);
			} else {
				showReplyErrorMsg(ret.msg);
			}
		}
		, complete: function(XHR, msg){
			layer.closeAll();
		}
	});
}
// 删除好友功能，用于关注/粉丝列表页面
function deleteFriend(btn, friendId) {
	confirmAjaxGet("取消关注后，此人的动态消息将不会出现在你的首页，确定要操作？", "/friend/delete?friendId=" + friendId, {
		success: function(ret) {
			if (ret.isOk) {
				setFriendBtn($(btn), friendId, ret.friendRelation);
			} else {
				showReplyErrorMsg(ret.msg);
			}
		}
	});
}

/**
 * 用于关注/粉丝列表页面
 * friendRelation 含义
 * 0： accountId 与 friendId 无任何关系
 * 1： accountId 关注了 friendId
 * 2： friendId 关注了 accountId
 * 3： accountId 与 friendId 互相关注
 */
function setFriendBtn(btn, friendId, friendRelation) {
	if (friendRelation == 0) {
		btn.attr("onclick", "addFriend(this," + friendId + ");");
		btn.text("+关注");
	} else if (friendRelation == 1) {
		btn.attr("onclick", "deleteFriend(this," + friendId + ");");
		btn.text("取消关注");
	} else if (friendRelation == 2) {
		btn.attr("onclick", "addFriend(this," + friendId + ");");
		btn.text("+关注");
	} else if (friendRelation == 3) {
		btn.attr("onclick", "deleteFriend(this," + friendId + ");");
		btn.text("取消互粉");
	}
}

/**
 * 用于个人空间用户头像下方的关注/取消关注功能
 */
function handleFriend(thisBtn, isAdd, friendId) {
	var layerIndex = layer.msg("正在加载，请稍后！", {icon: 16, offset: '100px'});
	var url = isAdd ? "/friend/add?friendId=" + friendId : "/friend/delete?friendId=" + friendId;
	ajaxGet(url, {
		success: function(ret) {
			if (ret.isOk) {
                var parent = $(thisBtn).parent();
                var link;
				if (ret.friendRelation == 0) {
                    link = "未关注<a href='javascript:void(0);' onclick='handleFriend(this, true, " + friendId + ");'>关注</a>";
				} else if (ret.friendRelation == 1) {
                    link = "已关注<a href='javascript:void(0);' onclick='handleFriend(this, false, " + friendId + ");'>取消</a>";
				} else if (ret.friendRelation == 2) {
                    link = "粉丝<a href='javascript:void(0);' onclick='handleFriend(this, true, " + friendId + ");'>+关注</a>";
				} else {
                    link = "互相关注<a href='javascript:void(0);' onclick='handleFriend(this, false, " + friendId + ");'>取消</a>";
				}
                parent.html(link);
			} else {
				showReplyErrorMsg(ret.msg);
			}
		}
		, complete: function(XHR, msg){
			layer.close(layerIndex);
		}
	});
}

/**
 * 点赞
 */
function doLike(refType, refId, isAdd, options) {
    var url = "/like?refType=" + refType + "&refId=" + refId;
    if (isAdd != null) {
        url = url + "&isAdd=" + isAdd;
    }
    ajaxGet(url, options);
}

/**
 * 点赞
 */
function like(refType, refId, map) {
    if (map.isLoading) {
        return ;
    } else {
        map.isLoading = true;
    }

    doLike(refType, refId, map.isAdd, {
        success: function(ret){
            if (ret.isOk) {
                var btn = map.btn;
                var next = btn.next();
                var num = next.text();
                num = parseInt(num);
                if (isNaN(num)) {
                    num = 0;
                }
                if (map.isAdd) {
                    num = num + 1;
                    btn.addClass("active");
                    map.isAdd = false;
                } else {
                    num = num - 1;
                    btn.removeClass("active");
                    map.isAdd = true;
                }
                if (num == 0) {
                    num = "";
                }
                next.text(num);
            } else {
                showReplyErrorMsg(ret.msg);
            }
	        map.isLoading = false;  // 重置 isLoading，允许点击时提交请求
        }
    });
}

/**
 * 收藏
 */
function doFavorite(refType, refId, isAdd, options) {
    var url = "/favorite?refType=" + refType + "&refId=" + refId;
    if (isAdd != null) {
        url = url + "&isAdd=" + isAdd;
    }
    ajaxGet(url, options);
}

/**
 * 收藏
 */
function favorite(refType, refId, map) {
    if (map.isLoading) {
        return ;
    } else {
        map.isLoading = true;
    }

    doFavorite(refType, refId, map.isAdd, {
        success: function(ret){
            if (ret.isOk) {
                var btn = map.btn;
                var next = btn.next();
                var num = next.text();
                num = parseInt(num);
                if (isNaN(num)) {
                    num = 0;
                }
                if (map.isAdd) {
                    num = num + 1;
                    btn.addClass("active");
                    map.isAdd = false;
                } else {
                    num = num - 1;
                    btn.removeClass("active");
                    map.isAdd = true;
                }
                if (num == 0) {
                    num = "";
                }
                next.text(num);
            } else {
                showReplyErrorMsg(ret.msg);
            }
	        map.isLoading = false;  // 重置 isLoading，允许点击时提交请求
        }
    });
}
