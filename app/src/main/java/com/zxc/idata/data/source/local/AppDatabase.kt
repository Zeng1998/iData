package com.zxc.idata.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        LocalFileDescription::class,
        LocalColumnDescription::class,
        LocalCellData::class,
        LocalTableOrder::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun fileDescriptionDao(): FileDescriptionDao
    abstract fun columnDescriptionDao(): ColumnDescriptionDao
    abstract fun cellDataDao(): CellDataDao
    abstract fun tableOrderDao(): TableOrderDao
}