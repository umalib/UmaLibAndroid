package cn.umafan.lib.android.util.network.service

import cn.umafan.lib.android.util.network.MyHeaderMap
import cn.umafan.lib.android.util.network.model.UpdateBean
import retrofit2.http.GET
import retrofit2.http.HeaderMap


interface UpdateService {

    @GET("/UmaLibAndroid/update-info.json")
    suspend fun getUpdate(
        @HeaderMap headerMap: MyHeaderMap
    ): UpdateBean
}