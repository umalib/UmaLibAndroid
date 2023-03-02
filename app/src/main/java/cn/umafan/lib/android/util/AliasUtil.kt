package cn.umafan.lib.android.util

object AliasUtil {
    private val aliasMap = mutableMapOf<String, String>()

    init {
        aliasMap["大拓太阳神"] = "大德太阳"
        aliasMap["小小蚕茧"] = "小巧圆茧\\Little Cocon"
        aliasMap["长山我驹"] = "长山之马"
        aliasMap["艾尼斯风神"] = "艾尼风神"
        aliasMap["东海帝王"] = "东海帝皇"
        aliasMap["东瀛佐敦"] = "岛川乔登"
        aliasMap["目白赖恩"] = "目白莱恩\\目白雷恩"
        aliasMap["圣王光环"] = "帝王光辉\\帝皇光辉"
        aliasMap["成田白仁"] = "成田拜仁"
        aliasMap["成田路"] = "成田速王"
        aliasMap["光辉致意"] = "Light Hello"
        aliasMap["名将怒涛"] = "名将户仁"
        aliasMap["名竞应援"] = "齐叫好"
        aliasMap["安身立命"] = "幸存者"
        aliasMap["好歌剧"] = "竹正歌剧王"
        aliasMap["里见女尊"] = "里见蕾娜斯"
        aliasMap["谷水琴蕾"] = "谷野美酒"
        aliasMap["快乐米可"] = "快乐温顺\\Happy Mik"
        aliasMap["纯洁光辉"] = "纯水之辉\\碧水鉴心"
        aliasMap["青云天空"] = "星云天空"
        aliasMap["苦味糖衣"] = "微苦糖渍\\Bitter Glasse"
        aliasMap["春乌拉拉"] = "春乌菈菈"
        aliasMap["待兼诗歌剧"] = "待兼唐怀瑟"
        aliasMap["夏威夷王"] = "龟波王"
        aliasMap["菱亚马逊"] = "亚马逊"
        aliasMap["萌机伶"] = "机伶梦"
        aliasMap["曼城茶座"] = "曼哈顿咖啡"
        aliasMap["第一红宝石"] = "第一红宝"
        aliasMap["超级小海湾"] = "超级小溪\\超级溪流"
        aliasMap["鲁铎象征"] = "鲁道夫象征"
        aliasMap["樱花进王"] = "樱花蓦进王"
        aliasMap["摩耶重炮"] = "重炮"
    }

    fun getAlias(name: String): String {
        if (aliasMap.containsKey(name)) {
            return "$name\\${aliasMap[name]}"
        }
        return name
    }
}