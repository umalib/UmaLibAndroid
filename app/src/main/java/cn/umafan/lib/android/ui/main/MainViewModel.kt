package cn.umafan.lib.android.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.umafan.lib.android.model.db.Tag
import cn.umafan.lib.android.model.MyApplication
import cn.umafan.lib.android.model.MyBaseActivity
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
}