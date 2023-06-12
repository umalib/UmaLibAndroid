package cn.umafan.lib.android.ui.recommend.model

import androidx.databinding.DataBindingUtil
import cn.umafan.lib.android.R
import cn.umafan.lib.android.databinding.ItemArticleCardBinding
import cn.umafan.lib.android.databinding.ItemRecCardBinding
import cn.umafan.lib.android.model.db.ArtInfo
import cn.umafan.lib.android.model.db.Rec
import cn.umafan.lib.android.model.db.Tagged
import cn.umafan.lib.android.ui.reader.ReaderActivity
import com.angcyo.dsladapter.DslAdapter
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.DslViewHolder
import com.google.android.material.snackbar.Snackbar
import com.liangguo.androidkit.app.startNewActivity
import java.text.SimpleDateFormat
import java.util.*

class RecTabItem(
    private val recInfo: RecInfo,
    private val t: Boolean
) : DslAdapterItem() {
    override var itemLayoutId = R.layout.item_rec_card
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
                articleName.text = recInfo.name
//                articleAuthor.visibility = android.view.View.GONE
                if (t) {
                    val adapter = DslAdapter()

                    adapter.changeDataItems { adapterItems ->
                        adapterItems.clear()
                        adapterItems.add(RecTabItem(recInfo, false))
                        adapterItems.add(RecTabItem(recInfo, false))
                        adapterItems.add(RecTabItem(recInfo, false))

                    }
                    recyclerView.adapter = adapter

                }
                invalidateAll()
            }
        }
    }
}