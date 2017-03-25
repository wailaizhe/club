/**
 * "/user/id" 用户空间关注/粉丝列表动态显示加关注等动作，个人空间 "/my" 并未使用此 js
 */
$(document).ready(function() {
    $(".friends div").hover(
        function() {
            var span = $(this).find("span");
            if (span.data("key") != 1) {
                ajaxGetFriendRelation(span, span.attr("id"));
            } else {
                span.css({visibility: "visible"});
            }
        },
        function() {
            $(this).find("span").css({visibility: "hidden"});
        }
    );
});

function ajaxGetFriendRelation(btn, friendId) {
    ajaxGet("/friend/getFriendRelation?friendId=" + friendId, {
        success: function(ret) {
            if (ret.isOk) {
                setFriendBtn(btn, friendId, ret.friendRelation);
                btn.data("key", 1);
                btn.css({visibility: "visible"});
            } else {
                showReplyErrorMsg(ret.msg);
            }
        }
        , complete: function(XHR, msg){
            layer.closeAll();
        }
    });
}
