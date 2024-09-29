package com.zxc.idata.enums

import com.zxc.idata.R
import java.util.Locale

enum class Language(val i18nId: Int, val locale: Locale) {
    FOLLOW_SYSTEM(R.string.follow_system, Locale("follow")),
    EN_US(R.string.en_US, Locale.US),
    ZH_CN(R.string.zh_CN, Locale.CHINA),
}