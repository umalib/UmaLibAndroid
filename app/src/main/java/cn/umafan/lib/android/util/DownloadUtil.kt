package cn.umafan.lib.android.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import cn.umafan.lib.android.R
import cn.umafan.lib.android.model.MyApplication
import cn.umafan.lib.android.model.MyBaseActivity
import cn.umafan.lib.android.ui.main.DatabaseCopyThread
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
import kotlin.math.log


object DownloadUtil {
    private const val TAG = "downloadF"

    private const val BASE_URL = "https://umalib.github.io/UmaLibAndroid/"

    private lateinit var fetch: Fetch
    private lateinit var fetchListener: FetchListener

    private val context = MyApplication.context
    private var isDownloading = false

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private var progressNotification: Int? = null

    private fun getLocalFilePath(fileName: String): String {
        Log.d("downloadF", "getLocalFilePath: ${context.getDatabasePath(fileName).path}")
        return context.getDatabasePath(fileName).path
    }

    // ????????????db??????
    fun getLatestDataBase(activity: MyBaseActivity) {
        activity.shapeLoadingDialog?.dialog?.show()
        var updateInfo: UpdateBean? = null
        // ?????????????????????????????????????????????
        GlobalScope.launch {
            UpdateUtil.getUpdate().apply {
                if (null != this) {
                    updateInfo = this
                    // ?????????????????????db
                    download("${updateInfo!!.currentDb}.zip", activity)
                } else {
                    ToastUtil.error("???????????????????????????????????????")
                }
            }
        }
    }

    private fun download(name: String, activity: MyBaseActivity) {
        if (isDownloading) return
        val fetchConfiguration: FetchConfiguration = FetchConfiguration.Builder(context)
            .setDownloadConcurrentLimit(1)
            .build()
        fetch = Fetch.Impl.getInstance(fetchConfiguration)

        val request = Request(BASE_URL + name, getLocalFilePath(name))
        request.priority = Priority.HIGH
        request.networkType = NetworkType.ALL

        fetchListener = object : FetchListener {

            override fun onStarted(
                download: Download,
                downloadBlocks: List<DownloadBlock>,
                totalBlocks: Int
            ) {
                isDownloading = true
                ToastUtil.info("?????????????????????????????????")
                activity.runOnUiThread {
                    activity.shapeLoadingDialog?.dialog?.hide()
                }
                showProgress(total = download.total / 1024 / 1024)
            }
            override fun onQueued(@NotNull download: Download, waitingOnNetwork: Boolean) {
                Log.d(TAG, "onQueued: $download")
            }
            override fun onCompleted(@NotNull download: Download) {
                Log.d(TAG, "onCompleted: ")
                isDownloading = false
                activity.runOnUiThread {
                    activity.shapeLoadingDialog?.dialog?.hide()
                }
                // ????????????
                notificationManager.createNotificationChannel(NotificationChannel("high_priority_notifications", "????????????", NotificationManager.IMPORTANCE_HIGH))
                val icon = IconCompat.createWithResource(context, cn.umafan.lib.android.R.drawable.ic_launcher)

                notificationManager.notify(Random().nextInt(), NotificationCompat.Builder(context, "high_priority_notifications")
                    .setContentTitle("????????????????????????")
                    .setSmallIcon(icon)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build())

                MaterialAlertDialogBuilder(
                    activity
                )
                    .setTitle(R.string.download_complete)
                    .setMessage(R.string.reload_database_prompt)
                    .setPositiveButton(R.string.confirm) { _, _ ->
                        // ?????????????????????
                        context.getDatabasePath("main.db").delete()
                        context.getDatabasePath("version").delete()
                        DatabaseCopyThread.clearDb()
                        // ????????????????????????
                        DatabaseCopyThread.addHandler(object: Handler(Looper.getMainLooper()) {
                            override fun handleMessage(msg: Message) {
                                ToastUtil.success("???????????????????????????????????????????????????")
                            }
                        }, name)
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .create()
                    .show()
                fetch.removeListener(fetchListener)
            }
            override fun onProgress(
                @NotNull download: Download,
                etaInMilliSeconds: Long,
                downloadedBytesPerSecond: Long
            ) {
                Log.d(TAG, "onProgress: $download")
                if (request.id == download.id) {
                    showProgress(
                        progress = download.progress,
                        speed = (download.downloadedBytesPerSecond / 1024),
                        total = (download.total / 1024 / 1024)
                    )
                }
            }

            override fun onWaitingNetwork(download: Download) {
                Log.d(TAG, "onWaitingNetwork: ")
            }
            override fun onDownloadBlockUpdated(download: Download, downloadBlock: DownloadBlock, totalBlocks: Int) {
                Log.d(TAG, "onDownloadBlockUpdated: ")
            }
            override fun onError(download: Download, error: Error, throwable: Throwable?) {
                ToastUtil.error("???????????????$error")
                activity.runOnUiThread {
                    activity.shapeLoadingDialog?.dialog?.hide()
                }
            }
            override fun onAdded(download: Download) {
                Log.d(TAG, "onAdded: ")
            }
            override fun onPaused(@NotNull download: Download) {
                Log.d(TAG, "onPaused: ")
            }
            override fun onResumed(@NotNull download: Download) {
                Log.d(TAG, "onResumed: ")
            }
            override fun onCancelled(@NotNull download: Download) {
                Log.d(TAG, "onCancelled: ")
            }
            override fun onRemoved(@NotNull download: Download) {
                Log.d(TAG, "onRemoved: ")
            }
            override fun onDeleted(@NotNull download: Download) {
                Log.d(TAG, "onDeleted: ")
            }
        }
        fetch.addListener(fetchListener)
        fetch.enqueue(request,
            { req: Request? ->
                Log.d("downloadF", "download: $req")
            }
        ) { error: Error? ->
            Log.d("downloadF", "error: $error")
        }
    }

    private fun showProgress(progress: Int = 0, speed: Long = 0, total: Long = 0) {
        if (progressNotification === null) {
            progressNotification = Notify
                .with(context)
                .asBigText {
                    title = "???????????????$speed kB/s  ??? $total MB"
                    expandedText = "???????????????????????????????????????"
                    bigText = "$speed kB/s  ??? $total MB"
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
                    title = "???????????????($speed kB/s  ??? $total MB)  $progress%"
                    expandedText = "???????????????????????????????????????"
                    bigText = "$speed kB/s  ??? $total MB"
                }
                .progress {
                    showProgress = true
                    enablePercentage = true
                    progressPercent = progress
                }.asBuilder().build())
        }
    }

}