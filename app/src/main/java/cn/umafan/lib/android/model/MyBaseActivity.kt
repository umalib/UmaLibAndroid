package cn.umafan.lib.android.model

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import cn.umafan.lib.android.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.mingle.widget.ShapeLoadingDialog


open class MyBaseActivity : AppCompatActivity() {

    var shapeLoadingDialog: ShapeLoadingDialog? = null

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
        super.onCreate(savedInstanceState)
        shapeLoadingDialog = ShapeLoadingDialog(this)
        shapeLoadingDialog?.dialog?.setCanceledOnTouchOutside(false)
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