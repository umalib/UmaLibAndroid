package cn.umafan.lib.android.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.umafan.lib.android.beans.Tag
import cn.umafan.lib.android.model.MyApplication
import cn.umafan.lib.android.model.SearchBean
import cn.umafan.lib.android.util.network.UpdateUtil
import cn.umafan.lib.android.util.network.model.UpdateBean
import com.liangguo.androidkit.app.ToastUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    var searchParams = MutableLiveData(SearchBean())
    var selectedTags = MutableStateFlow(mutableSetOf<Tag>())
    var selectedExceptTags = MutableStateFlow(mutableSetOf<Tag>())

    val updateInfo = MutableLiveData<UpdateBean>()

    fun getUpdate(initiative: Boolean) {
        viewModelScope.launch {
            UpdateUtil.getUpdate().apply {
                if (null != this) {
                    this.show = this.currentVersion > MyApplication.getVersion().code || this.currentVersionName > MyApplication.getVersion().name
                    this.initiative = initiative
                    updateInfo.value = this
                } else {
                    ToastUtil.error("获取更新失败，请检查网络！")
                }
            }
        }
    }
}