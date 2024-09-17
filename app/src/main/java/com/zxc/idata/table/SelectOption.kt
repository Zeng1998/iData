package com.zxc.idata.table

import androidx.compose.ui.graphics.Color
import java.util.UUID


data class SelectOption(val id: String, val text: String, val color: ULong) {
    companion object {
        fun new(): SelectOption {
            return SelectOption(UUID.randomUUID().toString(), "", Color.Black.value)
        }
    }
}

fun String.toSelectOptionListFromIds(columnOptions: List<SelectOption>): List<SelectOption> {
    if (this.isEmpty()) {
        return emptyList()
    }
    return this.split(";").mapNotNull { id ->
        columnOptions.find { it.id == id }
    }
}

fun String.toSelectOptionList(): List<SelectOption> {
    if (this.isEmpty()) {
        return emptyList()
    }
    return this.split(";").map {
        val split = it.split(",")
        SelectOption(split[0], split[1], split[2].toULong())
    }
}

fun List<SelectOption>.toIdsText(): String {
    return this.joinToString(";") { it.id }
}

fun List<SelectOption>.toText(): String {
    return this.joinToString(";") { "${it.id},${it.text},${it.color}" }
}