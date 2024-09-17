package com.zxc.idata

import com.zxc.idata.data.model.CellData
import org.junit.Test

class CellSortingTest {
    @Test
    fun testSortingCells() {
        val sortedColumnId = 1L
        val sortedOrder = "DESC"
        val cellItems = listOf(
            CellData(1, 1, 1, 1, "A", 1, 1),
            CellData(2, 1, 1, 2, "B", 2, 2),
            CellData(3, 1, 1, 3, "C", 3, 3),
            CellData(4, 1, 1, 4, "D", 4, 4),
            CellData(5, 1, 1, 5, "E", 5, 5),
            CellData(6, 1, 2, 1, "E", 6, 6),
            CellData(7, 1, 2, 2, "D", 7, 7),
            CellData(8, 1, 2, 3, "C", 8, 8),
            CellData(9, 1, 2, 4, "B", 9, 9),
            CellData(10, 1, 2, 5, "A", 10, 10),
        )
        cellItems.groupBy {
            it.rowId
        }
            .toMap()
            .toList()
            .let {
                if (sortedOrder == "ASC") {
                    it.sortedBy { (_, value) ->
                        value.first { it.columnId == sortedColumnId }.value
                    }
                } else {
                    it.sortedByDescending { (_, value) ->
                        value.first { it.columnId == sortedColumnId }.value
                    }
                }
            }.toMap().map {
                println(it)
            }
    }
}
