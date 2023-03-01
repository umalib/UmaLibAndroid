package cn.umafan.lib.android.model

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModelProvider
import cn.umafan.lib.android.R
import cn.umafan.lib.android.util.DownloadUtil
import cn.umafan.lib.android.util.SettingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.mingle.widget.ShapeLoadingDialog


open class MyBaseActivity : AppCompatActivity() {
    var shapeLoadingDialog: ShapeLoadingDialog? = null

    private var _baseViewModel: MyBaseViewModel? = null
    val baseViewModel get() = _baseViewModel!!

    /**
     * 数据库操作进度dialog
     */
    private var mDataBaseLoadingProgressView: View? = null
    private var mDataBaseLoadingProgressIndicator: LinearProgressIndicator? = null
    private var mDataBaseLoadingProgressNum: AppCompatTextView? = null
    private val mDataBaseLoadingProgressDialog by lazy {
        mDataBaseLoadingProgressView =
            LayoutInflater.from(this).inflate(R.layout.dialog_loading_database, null)
        with(mDataBaseLoadingProgressView) {
            mDataBaseLoadingProgressIndicator = this?.findViewById(R.id.progress_indicator)
            mDataBaseLoadingProgressNum = this?.findViewById(R.id.progress_num)
        }

        MaterialAlertDialogBuilder(
            this,
            com.google.android.material.R.style.MaterialAlertDialog_Material3
        )
            .setTitle(getString(R.string.prepare_database))
            .setView(mDataBaseLoadingProgressView)
            .setCancelable(false)
            .create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(SettingUtil.getTheme())
        super.onCreate(savedInstanceState)

        _baseViewModel =
            ViewModelProvider(this)[MyBaseViewModel::class.java]

        shapeLoadingDialog = ShapeLoadingDialog(this)
        shapeLoadingDialog?.dialog?.setCanceledOnTouchOutside(false)

        baseViewModel.updateInfo.observe(this@MyBaseActivity) {
            var message = getString(R.string.no_update)
            var buttonText = getString(R.string.confirm)
            val buttonAction: DialogInterface.OnClickListener?
            if (2 == it.show) {
                message = it.info.message
                buttonText = it.button.text
                buttonAction = DialogInterface.OnClickListener { _, _ ->
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(it.button.url)
                    startActivity(intent)
                }
                MaterialAlertDialogBuilder(
                    this@MyBaseActivity,
                    com.google.android.material.R.style.MaterialAlertDialog_Material3
                )
                    .setTitle("新应用版本${it.currentVersionName}")
                    .setMessage(message)
                    .setPositiveButton(buttonText, buttonAction)
                    .create().show()
            } else if (1 == it.show) {
                message = "版本 ${MyBaseViewModel.getDbVersion()} < ${it.currentDb}"
                buttonText = it.button.text
                buttonAction = DialogInterface.OnClickListener { _, _ ->
                    DownloadUtil.getLatestDataBase(this@MyBaseActivity)
                }
                MaterialAlertDialogBuilder(
                    this@MyBaseActivity,
                    com.google.android.material.R.style.MaterialAlertDialog_Material3
                )
                    .setTitle("新数据库版本${it.currentDb}")
                    .setMessage(message)
                    .setPositiveButton(buttonText, buttonAction)
                    .create().show()
            } else if (it.initiative) {
                MaterialAlertDialogBuilder(
                    this@MyBaseActivity,
                    com.google.android.material.R.style.MaterialAlertDialog_Material3
                ).setTitle(it.info.title)
                    .setMessage(message)
                    .setPositiveButton(buttonText, null)
                    .create().show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        shapeLoadingDialog?.dismiss()
    }

    /**
     * 用于展示数据库加载进度条
     */
    @SuppressLint("SetTextI18n")
    fun dataBaseLoadingDialog(progress: Double) {
        if (progress < 100.0) {
            mDataBaseLoadingProgressIndicator?.progress = progress.toInt()
            mDataBaseLoadingProgressNum?.text = "${String.format("%.2f", progress)}%"
            mDataBaseLoadingProgressDialog.show()
        } else mDataBaseLoadingProgressDialog.hide()
    }
}