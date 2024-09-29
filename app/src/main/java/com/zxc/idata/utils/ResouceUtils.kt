package com.zxc.idata.utils

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import com.zxc.idata.enums.Language
import java.util.Locale


class StringResourceUtils(private val context: Context, private val language: Language) {
    fun getString(id: Int): String {
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(if (language.locale.language == "follow") Locale.getDefault() else language.locale)
        val localizedContext = context.createConfigurationContext(configuration)
        return localizedContext.resources.getString(id)
    }

}