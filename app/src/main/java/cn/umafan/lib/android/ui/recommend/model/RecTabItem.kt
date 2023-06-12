package cn.umafan.lib.android.ui.recommend.model

import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import cn.umafan.lib.android.R
import cn.umafan.lib.android.databinding.ItemRecCardBinding
import cn.umafan.lib.android.model.MyApplication
import cn.umafan.lib.android.model.SearchBean
import cn.umafan.lib.android.model.db.Tag
import cn.umafan.lib.android.ui.main.MainActivity
import cn.umafan.lib.android.ui.recommend.RecommendViewModel
import com.angcyo.dsladapter.DslAdapter
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.DslViewHolder

class RecTabItem(
    private val recInfo: RecInfo,
    private val activity: MainActivity,
    private val viewModel: RecommendViewModel
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

                // 判断是否需要多重跳转，需要则不显示单独按钮
                if (recInfo.others.isNotEmpty() && recInfo.others != "{}") {
                    viewModel.notShowJumpButtonList[itemPosition] = true
                    jumpButton.visibility = View.GONE
                }

                // 判断是否展开
                with(viewModel) {
                    jumpButton.visibility = notShowJumpButtonList[itemPosition].let { notShow ->
                        if (notShow) {
                            View.GONE
                        } else {
                            View.VISIBLE
                        }
                    }
                    recyclerView.visibility = if (collapsedList[itemPosition]) View.GONE else View.VISIBLE
                    foldButton.setIconResource(if (collapsedList[itemPosition]) R.drawable.baseline_menu_open_24 else R.drawable.baseline_menu_24)
                    foldButton.text = MyApplication.context.getString(if (collapsedList[itemPosition]) R.string.recommend_unfold_comment else R.string.recommend_fold_comment)
                }


                val searchBean = SearchBean()
                if (recInfo.classType == 0) {
                    searchBean.creator = recInfo.title
                } else if (recInfo.classType == 1 || recInfo.classType == 2) {
                    if (recInfo.others.isNotEmpty() && recInfo.others != "{}") {
                        // TODO 处理others
                    } else {
                        val tag = Tag(
                            recInfo.refId,
                            recInfo.title,
                            0,
                            "",
                            ""
                        )
                        searchBean.tags = mutableSetOf(tag)
                    }
                } else if (recInfo.classType == 3) {
                    if (recInfo.others.isNotEmpty() && recInfo.others != "{}") {
                        // TODO 处理others
                    } else {
                        searchBean.keyword = recInfo.title
                    }
                }

                // 点击跳转按钮
                jumpButton.setOnClickListener {
                    activity.searchByOption(searchBean)
                }

                // 展开/折叠
                itemRecCardBox.setOnClickListener {
                    isCollapsed = !isCollapsed
                    viewModel.collapsedList[itemPosition] = isCollapsed
                    recyclerView.visibility = if (isCollapsed) View.GONE else View.VISIBLE
                    foldButton.setIconResource(if (isCollapsed) R.drawable.baseline_menu_open_24 else R.drawable.baseline_menu_24)
                    foldButton.text = MyApplication.context.getString(if (isCollapsed) R.string.recommend_unfold_comment else R.string.recommend_fold_comment)
                }

                invalidateAll()
            }
        }
    }
}