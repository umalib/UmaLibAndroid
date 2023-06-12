package cn.umafan.lib.android.ui.recommend.model

import android.util.Log
import androidx.databinding.DataBindingUtil
import cn.umafan.lib.android.R
import cn.umafan.lib.android.databinding.ItemRecCommentBinding
import cn.umafan.lib.android.model.db.Rec
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.DslViewHolder


/**
 * @ClassName RecCommentItem
 * @author ForeverDdB 835236331@qq.com
 * @Description
 * @createTime 2023年 06月12日 15:34
 **/
class RecCommentItem(
    val recInfo: Rec
) : DslAdapterItem() {
    override var itemLayoutId = R.layout.item_rec_comment
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
        itemHolder.view(R.id.item_rec_comment)?.let {
            DataBindingUtil.bind<ItemRecCommentBinding>(it)?.apply {
                recReason.text = recInfo.reason
                recAuthor.text = "—— ${recInfo.name}"

                invalidateAll()
            }
        }
    }
}