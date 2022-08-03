package cn.umafan.lib.android.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.umafan.lib.android.beans.ArtInfo
import cn.umafan.lib.android.ui.home.model.ArticleInfoItem
import com.angcyo.dsladapter.DslAdapter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    private val articleData = MutableStateFlow(listOf<ArtInfo>())

    val currentPage = MutableLiveData(1)

    val selectedPage = MutableLiveData(1)

    val checkedButton = MutableLiveData<com.google.android.material.card.MaterialCardView>()

    var checkedList = mutableListOf<Boolean>()

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