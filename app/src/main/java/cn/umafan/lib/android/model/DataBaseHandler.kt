package cn.umafan.lib.android.model

import android.os.Handler
import android.os.Looper
import android.os.Message
import cn.umafan.lib.android.ui.main.MainActivity

/**
 * 自定义handler模板，每个数据库的操作都通过此handler构建
 */
class DataBaseHandler(
    private val activity: MainActivity,
    private val unit: (Message) -> Unit
): Handler(Looper.getMainLooper()) {
    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        when (msg.what) {
            // 若数据库在加载中，则展示进度条
            MyApplication.DATABASE_LOADING -> {
                activity.dataBaseLoadingDialog(msg.obj as Double)
            }
            MyApplication.DATABASE_LOADED -> {
                activity.dataBaseLoadingDialog(100.0)
                unit(msg)
            }
        }
    }
}