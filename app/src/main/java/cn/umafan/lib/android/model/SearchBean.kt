package cn.umafan.lib.android.model

import java.io.Serializable

data class SearchBean(
    var keyword: String? = "",
    val tags: List<Int> = listOf(),
    val author: String? = "",
    val translator: String? = "",
    val exceptedTags: List<Int> = listOf()
) : Serializable
