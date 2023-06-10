package cn.umafan.lib.android.ui.recommend.model

import androidx.databinding.DataBindingUtil
import cn.umafan.lib.android.R
import cn.umafan.lib.android.databinding.ItemArticleCardBinding
import cn.umafan.lib.android.model.db.ArtInfo
import cn.umafan.lib.android.ui.reader.ReaderActivity
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.DslViewHolder
import com.google.android.material.snackbar.Snackbar
import com.liangguo.androidkit.app.startNewActivity
import java.text.SimpleDateFormat
import java.util.*

class RecTabItem(
    private val articleInfo: ArtInfo
) : DslAdapterItem() {
    override var itemLayoutId = R.layout.item_article_card
    private val timeStampFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)

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
                articleName.text = articleInfo.name
                articleNote.text = articleInfo.note
                if (articleInfo.translator.isNotEmpty()) {
                    articleAuthor.text = articleInfo.author
                    articleTranslator.text = String.format("译者：%s", articleInfo.translator)
                } else {
                    articleAuthor.text = ""
                    articleTranslator.text = articleInfo.author
                }

                articleUploadTime.text = articleInfo.uploadTime.let { date ->
                    timeStampFormatter.format(date.toLong() * 1000)
                }
                articleTags.text =
                    articleInfo.taggedList.map { tagged -> tagged.tag }
                        .sortedWith { a, b ->
                            if (a.type == b.type)
                                a.name.compareTo(b.name)
                            else
                                b.type.compareTo(a.type)
                        }.joinToString(" | ") { tag -> tag.name }
                itemArticleCardBox.setOnClickListener {
                    ReaderActivity::class.startNewActivity {
                        putExtra("id", articleInfo.id.toInt())
                    }
                }
                itemArticleCardBox.setOnLongClickListener { view ->
                    Snackbar.make(
                        view,
                        "[ID${articleInfo.id}]${articleInfo.name}",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    false
                }
                invalidateAll()
            }
        }
    }
}