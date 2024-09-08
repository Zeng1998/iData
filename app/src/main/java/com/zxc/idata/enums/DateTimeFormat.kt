package com.zxc.idata.enums

enum class DateTimeFormat(
    val displayName: String,
) {
    yyyy_bar_MM_bar_dd_space_HH_colon_mm_colon_ss("yyyy-MM-dd HH:mm:ss"),
    MM_backslash_dd_backslash_yyyy_space_HH_colon_mm_colon_ss_space_a("MM/dd/yyyy HH:mm:ss a"),
}