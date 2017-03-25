
$(document).ready(function() {
	setCurrentAdminMenu();
});

/**
 * 设置当前后台管理菜单
 */
function setCurrentAdminMenu() {
	var url = location.pathname, navMenus = $(".jf-my-menu-box li");
	if (!url.indexOf('/admin/account')) {
		navMenus.eq(0).addClass("jf-my-menu-current");
	} else if (!url.indexOf('/admin/project')) {
		navMenus.eq(1).addClass("jf-my-menu-current");
	} else if (!url.indexOf('/admin/share')) {
		navMenus.eq(2).addClass("jf-my-menu-current");
	} else if (!url.indexOf('/admin/feedback')) {
		navMenus.eq(3).addClass("jf-my-menu-current");
	} else if (!url.indexOf('/admin/doc')) {
		navMenus.eq(4).addClass("jf-my-menu-current");
	}
}

/**
 * 确认对话框层，点击确定才真正操作
 * @param msg 对话框的提示文字
 * @param operationUrl 点击确认后请求到的目标 url
 */
function confirmAjaxAction(msg, operationUrl) {
	layer.confirm(msg, {
		icon: 0
		, title:''                                    // 设置为空串时，title消失，并自动切换关闭按钮样式，比较好的体验
		, shade: 0.4
		, offset: "139px"
	}, function(index) {                            // 只有点确定后才会回调该方法
		// location.href = operationUrl;     // 操作是一个 GET 链接请求，并非 ajax
		// 替换上面的 location.href 操作，改造成 ajax 请求。后端用 renderJson 更方便，不需要知道 redirect 到哪里
		ajaxAction(operationUrl);
		layer.close(index);                           // 需要调用 layer.close(index) 才能关闭对话框
	});
}

/**
 * ajax 做通用的操作，不传递表单数据，仅传id值的那种
 */
function ajaxAction(url) {
	$.ajax(url, {
		type: "GET"
		, cache: false
		, dataType: "json"
		// , data: {	}
		, beforeSend: function() {}
		, error: function(ret) {alert(ret);}
		, success: function(ret) {
			if (ret.isOk) {
				showAjaxActionMsg(0, ret.msg);
			} else {
				showAjaxActionMsg(6, ret.msg);
			}
		}
	});
}

function showAjaxActionMsg(shift, msg) {
	layer.msg(msg, {
			shift: shift
			, shade: 0.4
			, time: 0
			, offset: "140px"
			, closeBtn: 1
			, shadeClose: true
			,maxWidth: "1000"
		}, function () {
			if (shift != 6) {
				location.reload();
			}
		}
	);
}

/**
 * 使用 fancybox 发送 ajax 请求 url，将获取到的内容显示出来
 * @param url 通过该 url 获取弹出层 html 片段
 */
function showFancyBox(url) {
	$.fancybox.open({
		type: "ajax"
		, href: url
		, padding: 10        // 该值直接写在 style中，所以用 warpCss 属性搞不定
		, openSpeed: 1   // open毫秒数，默认值是 250，可选: "slow", "normal", "fast" 或数字
		, closeSpeed: 1
		, wrapCSS: "my-fancy-box"   // 自定义 fancybox 部分样式，会在添加在wrap中的 class属中
		, helpers: {
			overlay: {
				closeClick: true         // 为 false 则点击遮罩层不关闭对话框
				, locked: false             // 为 false 则在弹出框后仍然可以滚屏
				, css: {}    // 添加到遮罩层 div style属性之中的样式
			}
		}
		, afterShow: function () {       // content 显示完之后回调

		}
		, beforeClose: function () {       // 关闭前回调，return false可以终止关闭
			return true;
		}
	});
}

