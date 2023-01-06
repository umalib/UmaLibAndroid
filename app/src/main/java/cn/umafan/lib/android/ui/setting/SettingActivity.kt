package cn.umafan.lib.android.ui.setting

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import cn.umafan.lib.android.R
import cn.umafan.lib.android.databinding.ActivitySettingBinding
import cn.umafan.lib.android.model.MyBaseActivity
import cn.umafan.lib.android.ui.UpdateLogActivity
import cn.umafan.lib.android.util.DownloadUtil
import cn.umafan.lib.android.util.SettingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.liangguo.androidkit.app.ToastUtil
import com.liangguo.androidkit.app.startNewActivity
import java.io.BufferedWriter
import java.io.FileNotFoundException
import java.io.FileWriter
import kotlin.system.exitProcess


class SettingActivity : MyBaseActivity() {
    private lateinit var binding: ActivitySettingBinding

    private val restartDialog by lazy {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.restart)
            .setMessage(R.string.restart_info)
            .setPositiveButton(R.string.restart_now) { _, _ ->
                val intent = packageManager.getLaunchIntentForPackage(packageName)
                intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
            .setNegativeButton(R.string.later, null)
            .create()
    }

    private val reopenDialog by lazy {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.restart)
            .setMessage(R.string.restart_info)
            .setPositiveButton(R.string.restart_now) { _, _ ->
                finish()
                exitProcess(0)
            }
            .setNegativeButton(R.string.later, null)
            .create()
    }

    private val handler by lazy {
        Handler(Looper.getMainLooper()) {
            shapeLoadingDialog?.dialog?.hide()
            when (it.what) {
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
            settingChangeIndexBg.apply {
                setOnClickListener {
                    XXPermissions.with(this@SettingActivity)
                        .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                        .request(object : OnPermissionCallback {
                            override fun onGranted(permissions: List<String>, all: Boolean) {
                                if (all) {
                                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                                    intent.type = "image/*"
                                    startActivityForResult(intent, 1)
                                }
                            }

                            override fun onDenied(permissions: List<String>, never: Boolean) {
                                if (never) {
                                    ToastUtil.error("被永久拒绝授权，请手动授予读写手机储存权限")
                                    // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                    XXPermissions.startPermissionActivity(context, permissions)
                                } else {
                                    ToastUtil.error("获取读写手机储存权限失败")
                                }
                            }
                        })
                }
                setOnLongClickListener {
                    clearImageBackground(SettingUtil.INDEX_BG)
                    false
                }
            }

            settingChangeBarBg.apply {
                setOnClickListener {
                    XXPermissions.with(this@SettingActivity)
                        .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                        .request(object : OnPermissionCallback {
                            override fun onGranted(permissions: List<String>, all: Boolean) {
                                if (all) {
                                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                                    intent.type = "image/*"
                                    startActivityForResult(intent, 2)
                                }
                            }

                            override fun onDenied(permissions: List<String>, never: Boolean) {
                                if (never) {
                                    ToastUtil.error("被永久拒绝授权，请手动授予读写手机储存权限")
                                    // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                    XXPermissions.startPermissionActivity(context, permissions)
                                } else {
                                    ToastUtil.error("获取读写手机储存权限失败")
                                }
                            }
                        })
                }
                setOnLongClickListener {
                    clearImageBackground(SettingUtil.APP_BAR_BG)
                    false
                }
            }


            settingChangeTheme.setOnClickListener {
                val themes = arrayOf(
                    getString(R.string.theme_nga),
                    getString(R.string.theme_white),
                    getString(R.string.theme_teal),
                    getString(R.string.theme_mcqueen)
                )
                val themesId = arrayOf(
                    R.style.Theme_UmaLibrary_NGA,
                    R.style.Theme_UmaLibrary_WHITE,
                    R.style.Theme_UmaLibrary_TEAL,
                    R.style.Theme_UmaLibrary
                )
                val selectedId = themesId.indexOf(SettingUtil.getTheme())
                MaterialAlertDialogBuilder(this@SettingActivity)
                    .setTitle(R.string.change_theme)
                    .setSingleChoiceItems(themes, selectedId) { _, i ->
                        SettingUtil.saveTheme(themesId[i])
                        restartDialog.show()
                    }.create().show()
            }

            settingItemFeedback.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://bbs.nga.cn/read.php?tid=33041357")
                startActivity(intent)
            }
            settingItemGithub.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://github.com/umalib/UmaLibAndroid")
                startActivity(intent)
            }
            settingItemCheckUpdate.setOnClickListener {
                this@SettingActivity.shapeLoadingDialog?.dialog?.show()
                this@SettingActivity.baseViewModel.getUpdate(this@SettingActivity, true)
            }
            settingUpdateLog.setOnClickListener {
                UpdateLogActivity::class.startNewActivity()
            }
            settingGetOnlineDb.setOnClickListener {
                val activity = this@SettingActivity
                DownloadUtil.getLatestDataBase(activity)
            }
            settingReloadDatabase.setOnClickListener {
                try {
                    MaterialAlertDialogBuilder(
                        this@SettingActivity
                    ).setTitle(R.string.reload_database_title)
                        .setMessage(R.string.reload_database_prompt)
                        .setPositiveButton(R.string.confirm) { _, _ ->
                            val versionFile = this@SettingActivity.getDatabasePath("version")
                            val bw = BufferedWriter(FileWriter(versionFile))
                            bw.write("0.0.0")
                            bw.flush()
                            bw.close()
                            reopenDialog.show()
                        }
                        .setNegativeButton(R.string.cancel, null)
                        .show()
                } catch (e: Exception) {
                    e.printStackTrace()
                    ToastUtil.error("加载失败！")
                }
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