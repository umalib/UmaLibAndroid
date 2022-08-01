package com.uma.umalibrary.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.angcyo.dsladapter.DslAdapter
import com.uma.umalibrary.logic.model.ArticleBean
import com.uma.umalibrary.logic.model.SearchBean
import com.uma.umalibrary.ui.home.model.ArticleInfoItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    var searchParams = MutableLiveData(SearchBean())

}