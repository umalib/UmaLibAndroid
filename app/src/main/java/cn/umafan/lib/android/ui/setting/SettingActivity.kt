package cn.umafan.lib.android.ui.setting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import cn.umafan.lib.android.R
import cn.umafan.lib.android.databinding.ActivitySettingBinding
import cn.umafan.lib.android.model.MyBaseActivity
import cn.umafan.lib.android.util.SettingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.liangguo.androidkit.app.ToastUtil
import java.io.FileNotFoundException


class SettingActivity : MyBaseActivity() {
    private lateinit var binding: ActivitySettingBinding

    private val restartDialog by lazy {
        MaterialAlertDialogBuilder(this)
            .setMessage(R.string.restart_info)
            .setPositiveButton(R.string.restart_now) { _, _ ->
                val intent = packageManager.getLaunchIntentForPackage(packageName)
                intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
            .setNegativeButton(R.string.later, null)
            .create()
    }

    private val handler by lazy {
        Handler(Looper.getMainLooper()){ it ->
            shapeLoadingDialog?.dialog?.hide()
            when(it.what) {
                SettingUtil.SAVE_IMAGE_SUCCESS -> restartDialog.show()
                SettingUtil.SAVE_IMAGE_FAIL -> ToastUtil.error(getString(R.string.set_fail))
            }
            false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        with(binding) {
            settingChangeIndexBg.setOnClickListener {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "image/*"
                startActivityForResult(intent, 1)
            }
            settingChangeIndexBg.setOnLongClickListener {
                clearImageBackground(SettingUtil.INDEX_BG)
                false
            }
            settingChangeBarBg.setOnClickListener {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "image/*"
                startActivityForResult(intent, 2)
            }
            settingChangeBarBg.setOnLongClickListener {
                clearImageBackground(SettingUtil.APP_BAR_BG)
                false
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun clearImageBackground(type: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.reset)
            .setMessage(R.string.confirm_reset)
            .setPositiveButton(R.string.confirm) { _, _ ->
                if (SettingUtil.clearImageBackground(type)) restartDialog.show()
                else ToastUtil.error(getString(R.string.set_fail))
            }
            .setNegativeButton(R.string.cancel, null)
            .create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    try {
                        shapeLoadingDialog?.show()
                        if (!SettingUtil.saveImageBackground(
                                handler,
                                SettingUtil.INDEX_BG,
                                data.data!!
                            )
                        ) ToastUtil.error(getString(R.string.set_fail))
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                        ToastUtil.error(getString(R.string.set_fail))
                    }
                }
            }
            2 -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    try {
                        shapeLoadingDialog?.show()
                        if (!SettingUtil.saveImageBackground(
                                handler,
                                SettingUtil.APP_BAR_BG,
                                data.data!!
                            )
                        ) ToastUtil.error(getString(R.string.set_fail))
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                        ToastUtil.error(getString(R.string.set_fail))
                    }
                }
            }
        }
    }
}