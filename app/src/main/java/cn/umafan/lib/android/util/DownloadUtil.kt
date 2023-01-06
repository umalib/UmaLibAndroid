package cn.umafan.lib.android.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import cn.umafan.lib.android.R
import cn.umafan.lib.android.model.MyApplication
import cn.umafan.lib.android.model.MyBaseActivity
import cn.umafan.lib.android.ui.main.MainActivity
import cn.umafan.lib.android.ui.setting.SettingActivity
import cn.umafan.lib.android.util.network.UpdateUtil
import cn.umafan.lib.android.util.network.model.UpdateBean
import com.angcyo.dsladapter.internal.throttleClick
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.liangguo.androidkit.app.ToastUtil
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.DownloadBlock
import io.karn.notify.Notify
import io.karn.notify.NotifyCreator
import io.karn.notify.internal.utils.Action
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.annotations.NotNull
import java.util.Random


object DownloadUtil {
    private const val BASE_URL = "https://umalib.github.io/UmaLibDesktop/" //20230106.zip

    private lateinit var fetch: Fetch

    private val context = MyApplication.context

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private var progressNotification: Int? = null

    private fun getLocalFilePath(fileName: String): String {
        Log.d("downloadF", "getLocalFilePath: ${context.getDatabasePath(fileName).path}")
        return context.getDatabasePath(fileName).path
    }

    fun getLatestDataBase(activity: MyBaseActivity) {
        activity.shapeLoadingDialog?.dialog?.show()
        var updateInfo: UpdateBean? = null
        // 获取更新信息并下载最新的数据库
        GlobalScope.launch {
            UpdateUtil.getUpdate().apply {
                if (null != this) {
                    updateInfo = this
                    download("${updateInfo!!.currentDb}.zip", activity)
//                    download("20230106.zip", activity)
                } else {
                    ToastUtil.error("获取更新失败，请检查网络！")
                }
            }
        }
    }

    private fun download(name: String, activity: MyBaseActivity) {

        val fetchConfiguration: FetchConfiguration = FetchConfiguration.Builder(context)
            .setDownloadConcurrentLimit(1)
            .build()
        fetch = Fetch.Impl.getInstance(fetchConfiguration)

        val request = Request(BASE_URL + name, getLocalFilePath(name))
        request.priority = Priority.HIGH
        request.networkType = NetworkType.ALL

        val fetchListener: FetchListener = object : FetchListener {

            override fun onStarted(
                download: Download,
                downloadBlocks: List<DownloadBlock>,
                totalBlocks: Int
            ) {
                ToastUtil.info("已开始在后台下载数据库")
                activity.runOnUiThread {
                    activity.shapeLoadingDialog?.dialog?.hide()
                }
                showProgress(total = download.total / 1024 / 1024)
            }
            override fun onQueued(@NotNull download: Download, waitingOnNetwork: Boolean) {
                if (request.id == download.id) {
                    showProgress(total = download.total / 1024 / 1024)
                }
            }
            override fun onCompleted(@NotNull download: Download) {
                activity.runOnUiThread {
                    activity.shapeLoadingDialog?.dialog?.hide()
                }
                // 发送通知
                notificationManager.createNotificationChannel(NotificationChannel("high_priority_notifications", "下载通知", NotificationManager.IMPORTANCE_HIGH))
                val target = Intent(context, MainActivity::class.java)
                val bubbleIntent = PendingIntent.getActivity(context, 0, target, PendingIntent.FLAG_MUTABLE)
                val icon = IconCompat.createWithResource(context, cn.umafan.lib.android.R.drawable.ic_launcher)
                val bubbleData = NotificationCompat.BubbleMetadata.Builder(bubbleIntent,
                    icon)
                    .setDesiredHeight(600)
                    .build()
                notificationManager.notify(Random().nextInt(), NotificationCompat.Builder(context, "high_priority_notifications")
                    .setContentTitle("您有一条新消息")
                    .setContentText("点此应用数据库")
                    .setContentIntent(bubbleIntent)
                    .setBubbleMetadata(bubbleData)
                    .setSmallIcon(icon)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build())

                MaterialAlertDialogBuilder(
                    activity
                )
                    .setTitle(R.string.download_complete)
                    .setMessage(R.string.reload_database_prompt)
                    .setPositiveButton(R.string.confirm) { _, _ ->
                        /** TODO 重载数据库操作 */
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .create().show()
            }
            override fun onProgress(
                @NotNull download: Download,
                etaInMilliSeconds: Long,
                downloadedBytesPerSecond: Long
            ) {
                if (request.id == download.id) {
                    showProgress(
                        progress = download.progress,
                        speed = (download.downloadedBytesPerSecond / 1024),
                        total = (download.total / 1024 / 1024)
                    )
                }
            }

            override fun onWaitingNetwork(download: Download) {}
            override fun onDownloadBlockUpdated(download: Download, downloadBlock: DownloadBlock, totalBlocks: Int) {}
            override fun onError(download: Download, error: Error, throwable: Throwable?) {}
            override fun onAdded(download: Download) {}
            override fun onPaused(@NotNull download: Download) {}
            override fun onResumed(@NotNull download: Download) {}
            override fun onCancelled(@NotNull download: Download) {}
            override fun onRemoved(@NotNull download: Download) {}
            override fun onDeleted(@NotNull download: Download) {}
        }
        fetch.addListener(fetchListener)
        fetch.enqueue(request,
            { _: Request? -> }
        ) { _: Error? -> }
    }

    private fun showProgress(progress: Int = 0, speed: Long = 0, total: Long = 0) {
        if (progressNotification === null) {
            progressNotification = Notify
                .with(context)
                .asBigText {
                    title = "下载数据库$speed kB/s  共 $total MB"
                    expandedText = "正在下载数据库，请耐心等待"
                    bigText = "$speed kB/s  共 $total MB"
                }
                .progress {
                    showProgress = true
                    enablePercentage = true
                    progressPercent = progress
                }
                .show()
        } else {
            notificationManager.notify(
                progressNotification!!, Notify
                .with(context)
                .asBigText {
                    title = "下载数据库($speed kB/s  共 $total MB)  $progress%"
                    expandedText = "正在下载数据库，请耐心等待"
                    bigText = "$speed kB/s  共 $total MB"
                }
                .progress {
                    showProgress = true
                    enablePercentage = true
                    progressPercent = progress
                }.asBuilder().build())
        }
    }

}