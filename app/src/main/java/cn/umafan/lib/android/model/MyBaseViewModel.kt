package cn.umafan.lib.android.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.umafan.lib.android.util.network.UpdateUtil
import cn.umafan.lib.android.util.network.model.UpdateBean
import com.liangguo.androidkit.app.ToastUtil
import kotlinx.coroutines.launch


class MyBaseViewModel : ViewModel() {
    val updateInfo = MutableLiveData<UpdateBean>()

    fun getUpdate(activity: MyBaseActivity, initiative: Boolean) {
        viewModelScope.launch {
            UpdateUtil.getUpdate().apply {
                if (initiative) activity.shapeLoadingDialog?.dialog?.hide()
                if (null != this) {
                    this.show =
                        this.currentVersion > MyApplication.getVersion().code ||
                                this.currentVersionName > MyApplication.getVersion().name
                    this.initiative = initiative
                    updateInfo.value = this
                } else {
                    ToastUtil.error("获取更新失败，请检查网络！")
                }
            }
        }
    }
}