package cn.umafan.lib.android.ui.main.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import cn.umafan.lib.android.R
import cn.umafan.lib.android.model.MyApplication
import cn.umafan.lib.android.model.db.Tag
import cn.umafan.lib.android.util.PinyinUtil
import com.google.android.material.textview.MaterialTextView
import java.util.*


/**
 * 用于搜索展示tag的适配器
 */
class TagSuggestionAdapter(
    private val tags: List<Tag>
) : BaseAdapter(), Filterable {

    private var mArrayFilter: ArrayFilter? = null
    private var filterTags: List<Tag> = tags

    override fun getFilter(): Filter {
        if (mArrayFilter == null) {
            mArrayFilter = ArrayFilter(tags, this)
        }
        return mArrayFilter!!
    }

    override fun getItem(p0: Int): Any {
        return filterTags[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return filterTags.size
    }

    override fun getView(positon: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder: ViewHolder?
        var mConvertView: View? = convertView
        if (null == mConvertView) {
            viewHolder = ViewHolder()
            mConvertView = LayoutInflater.from(MyApplication.context)
                .inflate(R.layout.item_tag_suggestion, null)
            viewHolder.tagName = mConvertView.findViewById(R.id.item_tag_suggestion_name)
            mConvertView.tag = viewHolder
        } else {
            viewHolder = mConvertView.tag as ViewHolder
        }
        val tag = filterTags[positon]
        viewHolder.tagName?.text = tag.name

        return mConvertView!!
    }

    class ViewHolder(
        var tagName: MaterialTextView? = null
    )

    private class ArrayFilter(
        private val tags: List<Tag>,
        val adapter: TagSuggestionAdapter
    ) : Filter() {
        var mFilterTags: ArrayList<Tag>? = null
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()
            if (mFilterTags == null) {
                mFilterTags = ArrayList(tags)
            }
            //如果没有过滤条件则不过滤
            if (constraint == null || constraint.isBlank()) {
                results.values = mFilterTags
                results.count = mFilterTags!!.size
            } else {
                val retList: MutableList<Tag> = ArrayList()
                //过滤条件
                val str = constraint.toString()
                //循环变量数据源，如果有属性满足过滤条件，则添加到result中
                for (tag in mFilterTags!!) {
                    val tagName = tag.name
                    if (PinyinUtil.getPinyin(tagName, "").lowercase(Locale.getDefault()).contains(
                            PinyinUtil.getPinyin(str, "").lowercase(Locale.getDefault())
                        )
                    ) {
                        val chars: CharArray = str.toCharArray()
                        var count = 0
                        for (i in chars.indices) {
                            // 判断是否为汉字字符
                            if (chars[i].toString().matches(Regex("[\\u4E00-\\u9FA5]+"))) {
                                count++
                            }
                        }
                        if (count > 0) {
                            if (tagName.contains(str)) {
                                retList.add(tag)
                            }
                        } else retList.add(tag)
                    }
                }
                results.values = retList
                results.count = retList.size
            }
            return results
        }

        //在这里返回过滤结果
        override fun publishResults(
            constraint: CharSequence?,
            results: FilterResults
        ) {
            adapter.filterTags = (results.values as List<*>).filterIsInstance<Tag>()
            if (results.count > 0) {
                adapter.notifyDataSetChanged()
            } else {
                adapter.notifyDataSetInvalidated()
            }
        }
    }
}