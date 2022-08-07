package cn.umafan.lib.android.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.card.MaterialCardView


open class PageSelectorViewModel: ViewModel() {
    var checkedList = mutableListOf<Boolean>()

    val checkedButton = MutableLiveData<MaterialCardView>()

    val selectedPage = MutableLiveData(1)
}