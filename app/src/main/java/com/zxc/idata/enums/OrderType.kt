package com.zxc.idata.enums

import com.zxc.idata.R

enum class OrderType(val i18nId: Int) {
    ASC(R.string.ascending),
    DESC(R.string.descending),
    UNSPECIFIED(R.string.unspecified),
}