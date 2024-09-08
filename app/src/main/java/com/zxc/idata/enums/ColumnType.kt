package com.zxc.idata.enums

import com.zxc.idata.R

enum class ColumnType(val i18nId: Int, val iconId: Int) {
    TEXT(R.string.text, R.drawable.stringtext),
    NUMBER(R.string.number, R.drawable.number6),
    DATE(R.string.date, R.drawable.calendar),
    DATETIME(R.string.datetime, R.drawable.calendar),
    TIME(R.string.time, R.drawable.time),
    DURATION(R.string.duration, R.drawable.hourglass),
    CHECKBOX(R.string.checkbox, R.drawable.checkboxchecked),
    SINGLE_SELECT(R.string.single_select, R.drawable.radiobuttonchecked),
    MULTIPLE_SELECT(R.string.multiple_select, R.drawable.taggroup),
    COUNT(R.string.count, R.drawable.progressbar),
    RATING(R.string.rating, R.drawable.star),
    IMAGE(R.string.image, R.drawable.image),
}