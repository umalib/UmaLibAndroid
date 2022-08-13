package cn.umafan.lib.android.ui.setting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import cn.umafan.lib.android.R
import cn.umafan.lib.android.databinding.ActivitySettingBinding
import cn.umafan.lib.android.util.SettingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.liangguo.androidkit.app.ToastUtil
import java.io.FileNotFoundException


class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding

    private val restartDialog by lazy {
        MaterialAlertDialogBuilder(this)
            .setMessage(R.string.restart_info)
            .setPositiveButton(R.string.restart_now) { _,_ ->
                val intent = packageManager.getLaunchIntentForPackage(packageName)
                intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
            .setNegativeButton(R.string.later, null)
            .create()
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
                startActivityForResult(intent,1)
            }
            settingChangeIndexBg.setOnLongClickListener {
                if (SettingUtil.clearImageBackground(SettingUtil.INDEX_BG)) restartDialog.show()
                false
            }
            settingChangeBarBg.setOnClickListener {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "image/*"
                startActivityForResult(intent,2)
            }
            settingChangeBarBg.setOnLongClickListener {
                if (SettingUtil.clearImageBackground(SettingUtil.APP_BAR_BG)) restartDialog.show()
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



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            1 -> {
                if (resultCode == Activity.RESULT_OK && data!=null){
                    try {
                        if (SettingUtil.saveImageBackground(SettingUtil.INDEX_BG, data.data!!)) restartDialog.show()
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                        ToastUtil.error(getString(R.string.set_fail))
                    }
                }
            }
            2 -> {
                if (resultCode == Activity.RESULT_OK && data!=null){
                    try {
                        if (SettingUtil.saveImageBackground(SettingUtil.APP_BAR_BG, data.data!!)) restartDialog.show()
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                        ToastUtil.error(getString(R.string.set_fail))
                    }
                }
            }
        }
    }
}