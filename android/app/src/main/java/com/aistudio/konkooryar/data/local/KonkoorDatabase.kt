package com.aistudio.konkooryar.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [SessionEntity::class, ExamEntity::class, MistakeEntity::class, DailyPlanEntity::class],
    version = 1,
    exportSchema = false
)
abstract class KonkoorDatabase : RoomDatabase() {
    abstract fun konkoorDao(): KonkoorDao

    companion object {
        @Volatile
        private var INSTANCE: KonkoorDatabase? = null

        fun getDatabase(context: Context): KonkoorDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KonkoorDatabase::class.java,
                    "konkoor_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
