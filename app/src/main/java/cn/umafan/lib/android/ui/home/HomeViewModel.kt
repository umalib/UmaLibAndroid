package cn.umafan.lib.android.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.umafan.lib.android.model.ArticleBean
import cn.umafan.lib.android.model.SearchBean
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

    private val articleData = MutableStateFlow(listOf<ArticleBean>())

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

    fun loadArticles(page: Int?, params: SearchBean?) {
        viewModelScope.launch {
            var data = mutableListOf<ArticleBean>()
            for (i in 0 until 10) {
                data.add(
                    ArticleBean(
                        id = i,
                        name = "西野花与東商變革(${params?.keyword}) 斯芬克斯 $page",
                        note = "很久很久以前，在特雷森學園的交通要道上出現了一隻怪物 據說那被稱為斯芬克斯的怪物會向路過的人提出謎語，回答不出來的話就會被吃掉 有一天，出現了一名挑戰這個怪物的勇者，她的名字叫東商變革 如果是魔法師",
                        author = "匿名",
                        translator = "鬼道",
                        source = "外部",
                        uploadTime = 1659290486,
                        content = "ffff"
                    )
                )
            }
            articleData.emit(data)
        }
    }

}