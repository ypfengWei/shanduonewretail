window.indexedDB = window.indexedDB || window.mozIndexedDB || window.webkitIndexedDB || window.msIndexedDB;
window.db = null;
const versionCode = 1;
const dbName = 'cart.db';

function init(cbOK) {
    if (!window.indexedDB) {
        console.log("你的浏览器不支持IndexedDB");
    } else {
        let request = window.indexedDB.open(dbName, versionCode);
        request.onerror = function (event) {
            console.log("打开DB失败", event);
        };
        request.onupgradeneeded = function (event) {
            window.db = event.target.result;
            if (!window.db.objectStoreNames.contains('cart')) {
                window.db.createObjectStore("cart", {keyPath: "commodityId"});
            }
        };
        request.onsuccess = function (event) {
            window.db = event.target.result;
            cbOK && cbOK();
        };
    }
}

/*购物车操作*/
function cartAction(actionMode, obj, cbOK, cbErr) {
    let objectStore = window.db.transaction(["cart"], "readwrite").objectStore("cart");
    let request = objectStore.get(obj.commodityId);
    request.onsuccess = function (event) {
        let cartItem = event.target.result;
        if ('del' === actionMode) {
            if (cartItem) {
                let count = cartItem.count;
                if (count > 0) {
                    count--;
                    cartItem.count = count;
                    objectStore.put(cartItem);
                }
            }
        } else {
            if (!cartItem) {
                objectStore.add(obj);
            } else {
                cartItem.count += 1;
                objectStore.put(cartItem);
            }
        }
        cbOK && cbOK();
    };
    request.onerror = function () {
        cbErr && cbErr();
    };
}

function delCommodity(commodityId) {
    window.db.transaction(["cart"], "readwrite").objectStore("cart").delete(commodityId);
}

//清空购物车
function emptyDB() {
    let transaction = window.db.transaction(["cart"], "readwrite");
    transaction.objectStore("cart").clear();
}

//获取购物车所有商品数据
function queryCartAll(cbOK) {
    let commodityArray = [];
    let transaction = window.db.transaction(["cart"], "readonly");
    let store = transaction.objectStore("cart");
    let cursor = store.openCursor();
    cursor.onsuccess = function (e) {
        let res = e.target.result;
        if (res) {
            commodityArray.push(res.value);
            res.continue();
        } else {
            cbOK && cbOK(commodityArray);
        }
    }
}

//检查购物车是否有数据
function checkCart(cbOK, cbErr) {
    let transaction = db.transaction(["cart"], "readwrite");
    let store = transaction.objectStore("cart");
    let cursor = store.openCursor();
    cursor.onsuccess = function (e) {
        let res = e.target.result;
        if (res) {
            cbOK && cbOK();
        } else {
            cbErr && cbErr();
        }
    };
}