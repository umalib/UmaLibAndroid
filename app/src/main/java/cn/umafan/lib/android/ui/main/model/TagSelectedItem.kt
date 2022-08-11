package cn.umafan.lib.android.ui.main.model

import androidx.databinding.DataBindingUtil
import androidx.lifecycle.viewModelScope
import cn.umafan.lib.android.R
import cn.umafan.lib.android.model.db.Tag
import cn.umafan.lib.android.databinding.ItemSelectedTagBinding
import cn.umafan.lib.android.ui.main.MainViewModel
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.DslViewHolder
import kotlinx.coroutines.launch


class TagSelectedItem(
    val tag: Tag,
    private val mViewModel: MainViewModel,
    private val flag: Boolean
) : DslAdapterItem() {
    override var itemLayoutId = R.layout.item_selected_tag

    init {
        itemData = tag
        thisAreContentsTheSame = { fromItem, newItem, _, _ ->
            fromItem?.itemData == newItem.itemData
        }
        thisAreItemsTheSame = { fromItem, newItem, _, _ ->
            fromItem?.itemData == newItem.itemData
        }
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)
        itemHolder.view(R.id.item_selected_tag)?.let {
            DataBindingUtil.bind<ItemSelectedTagBinding>(it)?.apply {
                itemSelectedTagName.text = tag.name
                itemSelectedTagCancel.setOnClickListener {
                    with(mViewModel) {
                        viewModelScope.launch {
                            if (flag) {
                                searchParams.value?.tags?.remove(tag)
                                val tmp = mutableSetOf<Tag>()
                                searchParams.value?.tags?.forEach { tag ->
                                    tmp.add(tag)
                                }
                                selectedTags.emit(tmp)
                            } else {
                                searchParams.value?.exceptedTags?.remove(tag)
                                val tmp = mutableSetOf<Tag>()
                                searchParams.value?.exceptedTags?.forEach { tag ->
                                    tmp.add(tag)
                                }
                                selectedExceptTags.emit(tmp)
                            }
                        }
                    }
                }
                invalidateAll()
            }
        }
    }
}