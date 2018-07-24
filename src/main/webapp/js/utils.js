//获取指定参数的值
var openid = localStorage.getItem('openid');
var imagePath = '/yapingzh/picture/';

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
        url: "/shanduonewretail/jwechat/getopenid",
        data: {
            "code": code
        },
        type: "POST",
        dataType: "json",
        success: function (res) {
            if (res.success) {
                localStorage.setItem('openid', res.result);
            }
        }
    });
}
function getMyDate(str) {
    let oDate = new Date(str),
        oHour = oDate.getHours(),
        oMin = oDate.getMinutes();
    return getzf(oHour) + ':' + getzf(oMin);
}

//补0操作
function getzf(num) {
    if (parseInt(num) < 10) {
        num = '0' + num;
    }
    return num;
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