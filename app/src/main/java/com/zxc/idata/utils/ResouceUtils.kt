package com.zxc.idata.utils

import android.content.Context
import android.content.res.Configuration
import com.zxc.idata.enums.Language


class StringResourceUtils(private val context: Context, private val language: Language) {
    fun getString(id: Int): String {
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(language.locale)
        val localizedContext = context.createConfigurationContext(configuration)
        return localizedContext.resources.getString(id)
    }

}