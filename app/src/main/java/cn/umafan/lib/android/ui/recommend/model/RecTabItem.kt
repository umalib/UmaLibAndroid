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
                if (null != recInfo.others && recInfo.others.isNotEmpty() && "{}" != recInfo.others) {
                    driverDown2.visibility = View.VISIBLE
                    jumpButtonRecyclerView.visibility = View.VISIBLE

                    jumpMap = JSONObject(recInfo.others)
                    try {
                        jumpMap.getJSONArray("join")
                        viewModel.notShowJumpButtonList[itemPosition] = false
                    } catch (e: Exception) {
                        viewModel.notShowJumpButtonList[itemPosition] = true
                    }
                }
                recName.text =
                    if (viewModel.notShowJumpButtonList[itemPosition]) {
                        "${recInfo.title} 等"
                    } else {
                        recInfo.title
                    }

                // 因为安卓的recyclerView的item在离开屏幕后会被回收，所以需要在这里缓存是否展开的状态
                // 便于重渲染的时候不会出现展开的item被折叠或者折叠的item被展开的情况
                // collapsedList和notShowJumpButtonList都是同样的缓存目的
                with(viewModel) {
                    if (collapsedList[itemPosition]) {
                        recyclerView.visibility = View.GONE
                        driverDown2.visibility = View.GONE
                        jumpButtonRecyclerView.visibility = View.GONE
                    } else {
                        recyclerView.visibility = View.VISIBLE
                        if (viewModel.notShowJumpButtonList[itemPosition]) {
                            driverDown2.visibility = View.VISIBLE
                            jumpButtonRecyclerView.visibility = View.VISIBLE
                        }
                    }
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
                                // 存在join则为join类型单独处理
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
                            // join类型还需要加上自身的tag
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
                        searchBean.tags = mutableSetOf(
                            Tag(
                                recInfo.refId,
                                recInfo.title,
                                0,
                                "",
                                ""
                            )
                        )
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
                            searchBean.keyword = recInfo.title
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
                    val isJumpShown =
                        if (!isCollapsed && viewModel.notShowJumpButtonList[itemPosition]) View.VISIBLE else View.GONE
                    jumpButtonRecyclerView.visibility = isJumpShown
                    driverDown2.visibility = isJumpShown
                    foldButton.setIconResource(if (isCollapsed) R.drawable.baseline_menu_open_24 else R.drawable.baseline_menu_24)
                    foldButton.text =
                        MyApplication.context.getString(if (isCollapsed) R.string.recommend_unfold_comment else R.string.recommend_fold_comment)
                }

                invalidateAll()
            }
        }
    }
}