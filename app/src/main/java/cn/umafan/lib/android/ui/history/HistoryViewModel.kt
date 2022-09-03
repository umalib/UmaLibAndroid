package cn.umafan.lib.android.ui.history

import androidx.lifecycle.viewModelScope
import cn.umafan.lib.android.model.PageSelectorViewModel
import cn.umafan.lib.android.model.db.ArtInfo
import cn.umafan.lib.android.ui.home.model.ArticleInfoItem
import com.angcyo.dsladapter.DslAdapter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel : PageSelectorViewModel() {

    private val articleData = MutableStateFlow(listOf<ArtInfo>())

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

    fun loadArticles(data: MutableList<ArtInfo>) {
        viewModelScope.launch {
            articleData.emit(data)
        }
    }

}