package cn.umafan.lib.android.model

data class ArticleBean(
    val id: Int,
    val name: String?,
    val note: String?,
    val content: String?,
    val author: String?,
    val translator: String?,
    val source: String?,
    val uploadTime: Long?
)
