package cn.umafan.lib.android.model

import java.io.Serializable

data class SearchBean(
    var keyword: String? = "",
    var tags: List<Int> = listOf(),
    var creator: String? = "",
    var exceptedTags: List<Int> = listOf()
) : Serializable
