package cn.umafan.lib.android.util.network

import cn.umafan.lib.android.util.network.service.UpdateService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

typealias MyHeaderMap = MutableMap<String, String>

fun getHeaderMap(): MyHeaderMap = mutableMapOf(
    Pair("Content-type", "application/json")
)

object UpdateUtil {
    private val updateService by lazy {
        ServiceCreator.create<UpdateService>()
    }

    suspend fun getUpdate() = withContext(Dispatchers.IO){
        updateService.getUpdate(getHeaderMap())
    }
}