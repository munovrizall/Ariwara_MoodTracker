package com.artonov.ariwara.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Diary::class],
    version = 2
)
abstract class DiaryDB : RoomDatabase() {

    abstract fun diaryDao(): DiaryDao

    companion object {

        @Volatile
        private var instance: DiaryDB? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            DiaryDB::class.java,
            "diary.db"
        )
            .fallbackToDestructiveMigration()
            .build()

    }
}