package cn.umafan.lib.android.ui.recommend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.umafan.lib.android.ui.main.MainActivity
import cn.umafan.lib.android.ui.recommend.model.RecInfo
import cn.umafan.lib.android.ui.recommend.model.RecTabItem
import com.angcyo.dsladapter.DslAdapter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RecommendViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    private val recData = MutableStateFlow(listOf<RecInfo>())

    val currentPage = MutableLiveData(1)

    val type = MutableLiveData(0)

    val recDataAdapter = DslAdapter()

    var notShowJumpButtonList = mutableListOf<Boolean>()

    var collapsedList = mutableListOf<Boolean>()

    lateinit var activity: MainActivity

    init {
        viewModelScope.launch {
            recData.collect {
                recDataAdapter.changeDataItems { adapterItems ->
                    adapterItems.clear()
                    it.forEach {
                        adapterItems.add(RecTabItem(it, activity, this@RecommendViewModel))
                    }
                }
            }
        }
    }

    /**
     * 异步加载推荐数据
     */
    fun loadRecs(list: List<RecInfo>?) {
        viewModelScope.launch {
            var data = list
            if (null == data) {
                data = mutableListOf()
            }
            recData.emit(data)
        }
    }
}