package cn.umafan.lib.android.util.network.model

data class UpdateBean(
    val button: UpdateButtonBean,
    val currentVersionName: String = "",
    val currentVersion: Int = 0,
    val info: UpdateInfoBean,
    var show: Boolean = false,
    var initiative: Boolean = false
)