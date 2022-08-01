package cn.umafan.lib.android.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.umafan.lib.android.model.SearchBean

class MainViewModel : ViewModel() {

    var searchParams = MutableLiveData(SearchBean())

}