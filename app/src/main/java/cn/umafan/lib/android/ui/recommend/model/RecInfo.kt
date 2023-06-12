package cn.umafan.lib.android.ui.recommend.model

import cn.umafan.lib.android.model.db.Rec

class RecInfo(
    var data: MutableList<Rec>
): Rec() {
    // 传入Rec数据, 生成RecInfo
    companion object {
        fun fromRec(rec: Rec, data: MutableList<Rec>): RecInfo {
            val recInfo = RecInfo(data)

            recInfo.id = rec.id
            recInfo.name = rec.name
            recInfo.type = rec.type
            recInfo.r = rec.r
            recInfo.reason = rec.reason
            recInfo.title = rec.title
            recInfo.refId = rec.refId
            recInfo.others = rec.others

            return recInfo
        }
    }
}