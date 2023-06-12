package cn.umafan.lib.android.ui.recommend.model

import android.app.Application
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import cn.umafan.lib.android.R
import cn.umafan.lib.android.databinding.ItemRecCardBinding
import cn.umafan.lib.android.model.MyApplication
import com.angcyo.dsladapter.DslAdapter
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.DslViewHolder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RecTabItem(
    private val recInfo: RecInfo
) : DslAdapterItem() {
    override var itemLayoutId = R.layout.item_rec_card
    var isCollapsed = true

    init {
        itemData = recInfo
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
        itemHolder.view(R.id.item_rec_card)?.let {
            DataBindingUtil.bind<ItemRecCardBinding>(it)?.apply {
                recName.text = recInfo.title
                recType.text =
                    listOf("译者", "作者", "短篇合集", "长篇小说", "系列", "短篇作品")[recInfo.type]

                val adapter = DslAdapter()
                adapter.changeDataItems { adapterItems ->
                    adapterItems.clear()
                    recInfo.data.forEach { data ->
                        adapterItems.add(RecCommentItem(data))
                    }
                }

                recyclerView.adapter = adapter

                itemRecCardBox.setOnClickListener {
                    isCollapsed = !isCollapsed
                    recyclerView.visibility = if (isCollapsed) View.GONE else View.VISIBLE
                    foldButton.setIconResource(if (isCollapsed) R.drawable.baseline_menu_open_24 else R.drawable.baseline_menu_24)
                    foldButton.text = MyApplication.context.getString(if (isCollapsed) R.string.recommend_unfold_comment else R.string.recommend_fold_comment)
                }

                invalidateAll()
            }
        }
    }
}