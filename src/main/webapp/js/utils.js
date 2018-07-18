//获取指定参数的值
var openid = localStorage.getItem('openid');

/**
 * @return {null}
 */
function GetQueryString(name) {
    let reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    let r = window.location.search.substr(1).match(reg);
    if (r != null)
        return (r[2]);
    return null;
}

function toast(msg) {
    layer.open({
        content: msg
        , skin: 'msg'
        , time: 2 //2秒后自动关闭
    });
}

function askforJumpPage(content, view, positiveButton, negativeButton) {
    layer.open({
        content: content
        , btn: [positiveButton, negativeButton]
        , yes: function (index) {
            location.href = view;
            layer.close(index);
        }
    });
}

function getOpenid(code) {
    $.ajax({
        url: "/yapingzh/getOpenid.do",
        data: {
            "code": code
        },
        type: "POST",
        dataType: "json",
        success: function (result) {
            if (result.success) {
                localStorage.setItem('openid', result.openid);
            }
        }
    });
}

function init_wx_js_sdk(pageUrl, cbOK) {
    $.getJSON("/shanduonewretail/jwechat/selectinitjssdk", {
        "pageUrl": pageUrl
    }, function (result) {
        if (result.success) {
            cbOK && cbOK(result.result);
        }
    }, function (res) {
        toast(res.errMsg);
    });
}

// base64编码开始
function encode64(input) {
    let keyStr = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';
    let output = '';
    let chr1, chr2, chr3 = '';
    let enc1, enc2, enc3, enc4 = '';
    let i = 0;
    do {
        chr1 = input.charCodeAt(i++);
        chr2 = input.charCodeAt(i++);
        chr3 = input.charCodeAt(i++);
        enc1 = chr1 >> 2;
        enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
        enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
        enc4 = chr3 & 63;
        if (isNaN(chr2)) {
            enc3 = enc4 = 64;
        } else if (isNaN(chr3)) {
            enc4 = 64;
        }
        output = output + keyStr.charAt(enc1) + keyStr.charAt(enc2)
            + keyStr.charAt(enc3) + keyStr.charAt(enc4);
        chr1 = chr2 = chr3 = '';
        enc1 = enc2 = enc3 = enc4 = '';
    } while (i < input.length);

    return output;
}// base64编码结束

//非空校验
function checkInput(input) {
    if (!input) {
        return false;
    }
    input = input.replace(/\s/g, '');
    return input.length !== 0;
}

/*验证手机号*/
function checkPhone(str) {
    let myreg = /^(((13[0-9])|(15[0-9])|16[678]|17[0135678]|(18[0-9]))+\d{8})$/;
    return checkInput(str) && myreg.test(str);
}

//时间戳转本地时间
function getLocalTime(nS) {
    let now = new Date(parseInt(nS) * 1000),
        y = now.getFullYear(),
        m = now.getMonth() + 1,
        d = now.getDate();
    return y + "-" + (m < 10 ? "0" + m : m) + "-" + (d < 10 ? "0" + d : d) + " " + now.toTimeString().substr(0, 8);
}

function formatMsgTime(timespan) {
    let dateTime = new Date(timespan);
    let year = dateTime.getFullYear();
    let month = dateTime.getMonth() + 1;
    let day = dateTime.getDate();
    let hour = dateTime.getHours();
    let minute = dateTime.getMinutes();
    let now = new Date();
    let now_new = Date.parse(now.toDateString());  //typescript转换写法

    let milliseconds;
    let timeSpanStr;

    milliseconds = now_new - timespan;

    if (milliseconds <= 1000 * 60) {
        timeSpanStr = '刚刚';
    }
    else if (1000 * 60 < milliseconds && milliseconds <= 1000 * 60 * 60) {
        timeSpanStr = Math.round((milliseconds / (1000 * 60))) + '分钟前';
    }
    else if (1000 * 60 * 60 < milliseconds && milliseconds <= 1000 * 60 * 60 * 24) {
        timeSpanStr = Math.round(milliseconds / (1000 * 60 * 60)) + '小时前';
    }
    else if (1000 * 60 * 60 * 24 < milliseconds && milliseconds <= 1000 * 60 * 60 * 24 * 15) {
        timeSpanStr = Math.round(milliseconds / (1000 * 60 * 60 * 24)) + '天前';
    }
    else if (milliseconds > 1000 * 60 * 60 * 24 * 15 && year === now.getFullYear()) {
        timeSpanStr = month + '-' + day + ' ' + hour + ':' + minute;
    } else {
        timeSpanStr = year + '-' + month + '-' + day + ' ' + hour + ':' + minute;
    }
    return timeSpanStr;
}