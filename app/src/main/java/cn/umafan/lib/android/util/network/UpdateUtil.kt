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

    suspend fun getUpdate() = catchError {
        updateService.getUpdate(getHeaderMap())
    }

    /**
     * 所有的网络请求都统一经过这个函数
     */
    suspend fun <T> catchError(block: suspend () -> T): T? {
        return try {
            val result = withContext(Dispatchers.IO) { block() }
            result
        } catch (e: Exception) {
            if (e is retrofit2.HttpException) {
                null
            } else {
                null
            }
        }
    }
}