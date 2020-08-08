
/**
 * It performs the HTTP request.
 * 
 * @param {String} url 
 * @param {String} type 
 * @param {Object} body can be "undefined"
 * @param {Function} callBack 
 */
export const doHttpRequest = function(url, type, body, callBack, extra){
    extra = extra == undefined ? {} : extra;
    //MarcoUtils.preventClick(extra.preventClick == undefined ? true : extra.preventClick);
    fetch(url, {
        method: type,
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: body != undefined ? JSON.stringify(body) : body
    }).then((resp) => {
        if (resp.ok) {
            return resp.json();
        }
        throw new Error("Network response was not ok");
    }).then((jsonResp) => {
        //MarcoUtils.preventClick(false);
        if(jsonResp.errors){
            jsonResp.errors.forEach((error, index) => {
                //MarcoUtils.showNotification({ title: error.title, message: error.message, type: error.type})        
            });
        }
        callBack(jsonResp);
    }).catch((error) => {
        //MarcoUtils.preventClick(false);
        //MarcoUtils.showNotification({ title: "Oops", message: error.message, close: false })
    });
}