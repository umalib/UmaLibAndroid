/**
* 统一管理js调用安卓方法
* @param method 方法名
* @param params 参数 数组格式
*/
var callAndroidMethod = function(method, params){
    window.jsInterface.invokeMethod(method, [JSON.stringify(params)]);//json对象转成字符串，再转成字符串数组
}