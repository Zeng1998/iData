package com.zxc.idata.enums

enum class DateTimeFormat(
    val displayName: String,
) {
    LOW_YYYY_BAR_MM_BAR_LOW_DD_SPACE_HH_LOW_COLON_LOW_MM_COLON_LOW_SS("yyyy-MM-dd HH:mm:ss"),
    MM_BACKSLASH_LOW_DD_BACKSLASH_LOW_YYYY_SPACE_HH_COLON_LOW_MM_COLON_LOW_SS_SPACE_LOW_A("MM/dd/yyyy HH:mm:ss a"),
}