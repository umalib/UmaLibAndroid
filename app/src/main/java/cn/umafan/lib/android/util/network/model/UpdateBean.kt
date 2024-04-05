package cn.umafan.lib.android.util.network.model

data class UpdateBean(
    val button: UpdateButtonBean,
    val currentVersionName: String = "",
    val currentVersion: Int = 0,
    val info: UpdateInfoBean,
    var show: Int = 0,
    var initiative: Boolean = false,
    var currentDb: Int = 0,
    var downloadUrl: String = ""
)