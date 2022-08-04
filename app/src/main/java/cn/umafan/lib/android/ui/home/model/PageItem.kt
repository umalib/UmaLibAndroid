package cn.umafan.lib.android.ui.home.model

import androidx.databinding.DataBindingUtil
import cn.umafan.lib.android.R
import cn.umafan.lib.android.databinding.ItemPageButtonBinding
import cn.umafan.lib.android.ui.home.HomeViewModel
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.DslViewHolder

class PageItem(
    private val page: Int,
    private val mViewModel: HomeViewModel
) : DslAdapterItem() {
    override var itemLayoutId = R.layout.item_page_button

    init {
        itemData = page
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
        itemHolder.view(R.id.item_page_button)?.let {
            DataBindingUtil.bind<ItemPageButtonBinding>(it)?.apply {
                //先初始化使其不被选中,再通过数组储存的状态恢复选中
                itemPageButtonBox.isChecked = false
                itemPageButtonBox.isChecked = mViewModel.checkedList[page - 1]
                if (mViewModel.checkedList[page - 1]) {
                    mViewModel.checkedButton.value = itemPageButtonBox
                }
                pageNumText.text = page.toString()
                itemPageButtonBox.setOnClickListener {
                    itemPageButtonBox.isChecked = true
                    with(mViewModel) {
                        checkedButton.value?.isChecked = false
                        checkedButton.value = itemPageButtonBox
                        //清除数组再重新记录状态
                        checkedList.replaceAll { false }
                        checkedList[page - 1] = true

                        selectedPage.value = page
                    }

                }
                invalidateAll()
            }
        }
    }

    override fun onItemViewRecycled(itemHolder: DslViewHolder, itemPosition: Int) {
        itemHolder.view(R.id.item_page_button)?.let {
            DataBindingUtil.bind<ItemPageButtonBinding>(it)?.apply {
                itemPageButtonBox.setOnCheckedChangeListener(null)
            }
        }
        super.onItemViewRecycled(itemHolder, itemPosition)
    }
}