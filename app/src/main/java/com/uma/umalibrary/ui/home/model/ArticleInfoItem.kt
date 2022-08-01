package com.uma.umalibrary.ui.home.model

import android.util.Log
import androidx.databinding.DataBindingUtil
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.DslViewHolder
import com.google.android.material.snackbar.Snackbar
import com.uma.umalibrary.R
import com.uma.umalibrary.databinding.ItemArticleCardBinding
import com.uma.umalibrary.logic.model.ArticleBean
import java.text.SimpleDateFormat
import java.util.*


/**
 * @ClassName ArticleInfoItem
 * @author ForeverDdB 835236331@qq.com
 * @Description
 * @createTime 2022年 07月31日 23:19
 **/
class ArticleInfoItem(
    val articleInfo: ArticleBean
) : DslAdapterItem() {

    override var itemLayoutId = R.layout.item_article_card

    init {
        itemData = articleInfo
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
        itemHolder.view(R.id.item_article_card)?.let {
            DataBindingUtil.bind<ItemArticleCardBinding>(it)?.apply {
                val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINA)
                articleName.text = articleInfo.name
                articleNote.text = articleInfo.note
                articleAuthor.text = articleInfo.author
                articleTranslator.text = "译者： ${articleInfo.translator}"
                articleUploadTime.text = articleInfo.uploadTime?.let { date ->
                    formatter.format(date * 1000)
                }
                itemArticleCardBox.setOnClickListener { view ->
                    Snackbar.make(view, "点击了id为${articleInfo.id}的卡片", Snackbar.LENGTH_SHORT).setAction("Action", null).show()
                }
//                val calendar = CalendarUtil.getCalendarByTimeMills(gradeDetail.startTime.toLong())
//                textDate.text = calendar.toNearDayString()?.let { nearDay ->
//                    nearDay + " " + calendar.dayOfWeek
//                } ?: CalendarUtil.toCompleteDateString(calendar)
                invalidateAll()
            }
        }
    }

}