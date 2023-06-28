package cn.umafan.lib.android.ui.recommend.model

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
import org.json.JSONObject

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
                val jumpAdapter = DslAdapter()
                jumpButtonRecyclerView.adapter = jumpAdapter

                var jumpMap: JSONObject? = null
                // 判断是否需要多重跳转，需要则显示多重跳转按钮
                if (recInfo.others.isNotEmpty() && recInfo.others != "{}") {
                    driverDown2.visibility = View.VISIBLE
                    jumpButtonRecyclerView.visibility = View.VISIBLE

                    jumpMap = JSONObject(recInfo.others)
                    try {
                        jumpMap.getJSONArray("join")
                        viewModel.notShowJumpButtonList[itemPosition] = false
                    } catch (e: Exception) {
                        viewModel.notShowJumpButtonList[itemPosition] = true
                        jumpButton.visibility = View.INVISIBLE
                    }
                }
                recName.text =
                if (viewModel.notShowJumpButtonList[itemPosition]) {
                    "${recInfo.title} 等"
                } else {
                    recInfo.title
                }

                // 判断是否展开
                with(viewModel) {
                    val multiJump = notShowJumpButtonList[itemPosition].let { notShow ->
                        if (!notShow) {
                            View.GONE
                        } else {
                            View.VISIBLE
                        }
                    }
                    driverDown2.visibility = multiJump
                    jumpButtonRecyclerView.visibility = multiJump

                    recyclerView.visibility =
                        if (collapsedList[itemPosition]) View.GONE else View.VISIBLE
                    foldButton.setIconResource(if (collapsedList[itemPosition]) R.drawable.baseline_menu_open_24 else R.drawable.baseline_menu_24)
                    foldButton.text =
                        MyApplication.context.getString(if (collapsedList[itemPosition]) R.string.recommend_unfold_comment else R.string.recommend_fold_comment)
                }


                val searchBean = SearchBean()
                if (recInfo.classType == 0) {
                    searchBean.creator = recInfo.title
                } else if (recInfo.classType == 1 || recInfo.classType == 2) {
                    if (jumpMap != null) {
                        val keysIterable = jumpMap.keys()
                        val keys = mutableListOf<String>()
                        var isJoin = false
                        keysIterable.forEach { key ->
                            keys.add(key)
                            if ("join" == key) {
                                isJoin = true
                            }
                        }

                        if (isJoin) {
                            searchBean.tags = mutableSetOf()
                            // 获取join的数据
                            val joinArray = jumpMap.getJSONArray("join")
                            for (i in 0 until joinArray.length()) {
                                val tag = Tag(
                                    joinArray.getLong(i),
                                    "",
                                    0,
                                    "",
                                    ""
                                )
                                searchBean.tags.add(tag)
                            }
                            searchBean.tags.add(
                                Tag(
                                    recInfo.refId,
                                    "",
                                    0,
                                    "",
                                    ""
                                )
                            )
                        } else {
                            jumpAdapter.changeDataItems { adapterItems ->
                                adapterItems.clear()
                                val intSelfSearchBean = SearchBean()
                                intSelfSearchBean.tags = mutableSetOf(
                                    Tag(
                                        recInfo.refId,
                                        recInfo.title,
                                        0,
                                        "",
                                        ""
                                    )
                                )
                                adapterItems.add(
                                    RecJumpItem(
                                        recInfo.title,
                                        intSelfSearchBean,
                                        activity
                                    )
                                )
                                keys.forEach { key ->
                                    val tag = Tag(
                                        key.toLong(),
                                        jumpMap.getString(key),
                                        0,
                                        "",
                                        ""
                                    )
                                    // 防止共用一个searchBean导致数据相同
                                    val intSearchBean = SearchBean()
                                    intSearchBean.tags = mutableSetOf(tag)
                                    adapterItems.add(RecJumpItem(tag.name, intSearchBean, activity))
                                }
                            }
                            searchBean.tags =
                                mutableSetOf(
                                    Tag(
                                        recInfo.refId,
                                        recInfo.title,
                                        0,
                                        "",
                                        ""
                                    )
                                )
                        }
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
                    if (jumpMap != null) {
                        val keysIterable = jumpMap.keys()
                        val keys = mutableListOf<String>()
                        var isJoin = false
                        keysIterable.forEach { key ->
                            keys.add(key)
                            if ("join" == key) {
                                isJoin = true
                            }
                        }

                        if (isJoin) {
                            // TODO join 貌似没有单篇作品的join，暂且不做处理
                        } else {
                            jumpAdapter.changeDataItems { adapterItems ->
                                adapterItems.clear()
                                // 单篇作品需要将recInfo本身也加入跳转列表
                                val intSelfSearchBean = SearchBean()
                                val selfName = recInfo.title
                                intSelfSearchBean.keyword = selfName
                                adapterItems.add(RecJumpItem(selfName, intSelfSearchBean, activity))
                                keys.forEach { key ->
                                    // 防止共用一个searchBean导致数据相同
                                    val intSearchBean = SearchBean()
                                    val name = jumpMap.getString(key)
                                    intSearchBean.keyword = name
                                    adapterItems.add(RecJumpItem(name, intSearchBean, activity))
                                }
                            }
                        }
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
                    foldButton.text =
                        MyApplication.context.getString(if (isCollapsed) R.string.recommend_unfold_comment else R.string.recommend_fold_comment)
                }

                invalidateAll()
            }
        }
    }
}