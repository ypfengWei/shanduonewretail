/*查询店铺的种类*/
function getClassify(lat, lon, back) {
    $.getJSON("/shanduonewretail/jseller/selectsellertype", {lat: lat, lon: lon}, function (result) {
        if (result.success) {
            back && back(result);
        }
    });
}

function getCommodityAllType(token, back) {
    $.getJSON("/shanduonewretail/jcommodity/selectcommodityalltype", {token: token}, function (result) {
        if (result.success) {
            back && back(result);
        }
    });
}

// 根据类别id查询该类别下的店铺
function getClassifyBySellerType(lat, lon, sellerType, cbOk) {
    $.ajax({
        sync: false,
        url: "/shanduonewretail/jseller/selectseller",
        dataType: "JSON",
        data: {lat: lat, lon: lon, sellerType: sellerType},
        success: function (result) {
            if (result.success) {
                cbOk && cbOk(result);
            }
        }
    });
}

/* 查询某个店铺的商品种类 */
function getStoreClassify(storeId, typeId, back) {
    $.getJSON("/shanduonewretail/jcommodity/selectcommoditytype", {id: storeId, typeId: typeId}, function (result) {
        if (result.success) {
            back && back(result.result);
        }
    });
}

// 根据类别id查询该商店某类别下的商品
function getGoods(storeId, categoryId, typeId, pageIndex, pageCount, cbOk, cbErr) {
    $.ajax({
        sync: false,
        url: "/shanduonewretail/jcommodity/selectcommodity",
        dataType: "JSON",
        data: {id: storeId, categoryId: categoryId, typeId: typeId, page: pageIndex, pageSize: pageCount},
        success: function (result) {
            console.log(result)
            if (result.success) {
                cbOk && cbOk(result.result);
            }
        }, error: () => {
            cbErr && cbErr();
        }
    });
}

// 根据类别id查询该商店某类别下的商品
function getGoodsAll(token, categoryId, page, pageSize, cbOk, cbErr) {
    $.ajax({
        sync: false,
        url: "/shanduonewretail/jcommodity/selectwarehousecommodity",
        dataType: "JSON",
        data: {token: token, categoryId: categoryId, page: page, pageSize: pageSize},
        success: function (result) {
            console.log(result)
            if (result.success) {
                cbOk && cbOk(result.result);
            }
        }, error: () => {
            cbErr && cbErr();
        }
    });
}

function addClassify() {
    var classifyName = $('#classifyName').val();
    if ($.trim(classifyName) === '') {
        toast('填写类别名');
        return;
    }
    $.ajax({
        url: "/yapingzh/addClassify.do",
        data: {"commodityClassification.commodityType": "" + classifyName + ""},
        type: "POST",
        dataType: "json",
        success: function (result) {
            if (result.success) {
                toast('添加成功');
                history.go(-1);
            } else {
                toast('类别重复');
            }
        }, error: function () {
            toast('未知错误');
        }
    });
}