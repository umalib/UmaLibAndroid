package cn.umafan.lib.android.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.umafan.lib.android.beans.Tag
import cn.umafan.lib.android.model.SearchBean
import kotlinx.coroutines.flow.MutableStateFlow

class MainViewModel : ViewModel() {
    var searchParams = MutableLiveData(SearchBean())
    var selectedTags = MutableStateFlow(mutableSetOf<Tag>())
    var selectedExceptTags = MutableStateFlow(mutableSetOf<Tag>())
}