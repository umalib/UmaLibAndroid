package cn.umafan.lib.android.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.umafan.lib.android.beans.ArtInfo
import cn.umafan.lib.android.model.PageSelectorViewModel
import cn.umafan.lib.android.ui.home.model.ArticleInfoItem
import com.angcyo.dsladapter.DslAdapter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : PageSelectorViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    private val articleData = MutableStateFlow(listOf<ArtInfo>())

    val currentPage = MutableLiveData(1)

    val articleDataAdapter = DslAdapter()

    init {
        viewModelScope.launch {
            articleData.collect {
                articleDataAdapter.changeDataItems { adapterItems ->
                    adapterItems.clear()
                    it.forEach {
                        adapterItems.add(ArticleInfoItem(it))
                    }
                }
            }
        }
    }

    /**
     * 异步加载文章
     */
    fun loadArticles(list: List<ArtInfo>?) {
        viewModelScope.launch {
            var data = list
            if (null == data) {
                data = mutableListOf()
            }
            articleData.emit(data)
        }
    }
}