package com.artonov.ariwara.database

import androidx.room.*

@Dao
interface DiaryDao {

    @Insert
    suspend fun addDiary(diary: Diary)

    @Update
    suspend fun updateDiary(diary: Diary)

    @Delete
    suspend fun deleteDiary(diary: Diary)

    @Query("SELECT * FROM diary ORDER BY date DESC")
    suspend fun getDiaries(): List<Diary>

    @Query("SELECT * FROM diary WHERE id=:diary_id")
    suspend fun getDiaryById(diary_id: Int): List<Diary>

    @Query("SELECT id, note, date, mood, COUNT(*) as count FROM diary GROUP BY mood")
    fun getMoodCounts(): List<Diary>

}