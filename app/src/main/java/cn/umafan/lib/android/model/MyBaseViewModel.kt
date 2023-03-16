package cn.umafan.lib.android.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.umafan.lib.android.util.network.UpdateUtil
import cn.umafan.lib.android.util.network.model.UpdateBean
import com.liangguo.androidkit.app.ToastUtil
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.FileReader
import java.io.FileWriter


class MyBaseViewModel : ViewModel() {
    val updateInfo = MutableLiveData<UpdateBean>()

    companion object {
        private val verFile = MyApplication.context.getDatabasePath("version")
        private var dbVersion = 0

        fun getDbVersion(): Int {
            verFile.parentFile?.mkdirs()
            var flag = !verFile.exists()
            if (dbVersion == 0) {
                try {
                    val br = BufferedReader(FileReader(verFile))
                    dbVersion = Integer.parseInt(br.readLine())
                    br.close()
                } catch (e: Exception) {
                    Log.e("MyBaseViewModel", e.message!!)
                    flag = true
                }
            }
            if (flag) {
                setDbVersion(dbVersion)
            }
            return dbVersion
        }

        fun setDbVersion(version: Int) {
            dbVersion = version
            val bw = BufferedWriter(FileWriter(verFile))
            bw.write(version.toString())
            bw.close()
            Log.d("DBVersion", "set db version $dbVersion")
        }
    }

    fun getUpdate(activity: MyBaseActivity, initiative: Boolean) {
        viewModelScope.launch {
            UpdateUtil.getUpdate().apply {
                if (initiative) activity.shapeLoadingDialog?.dialog?.hide()
                if (null != this) {
                    Log.d("DBVersion", getDbVersion().toString())
                    this.show =
                        if (this.currentVersionName > MyApplication.getVersion().name) 2
                        else if (this.currentDb > getDbVersion()) 1
                        else 0
                    this.initiative = initiative
                    updateInfo.value = this
                } else {
                    ToastUtil.error("获取更新失败，请检查网络！")
                }
            }
        }
    }
}