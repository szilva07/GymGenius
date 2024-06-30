package hu.bme.aut.android.gymgenius.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import hu.bme.aut.android.gymgenius.data.table.ExercisePerformance

@Dao
interface ExercisePerformanceDao {
    @Query("SELECT * FROM ExercisePerformance")
    fun getAll(): List<ExercisePerformance>

    @Insert
    fun insert(exercisePerformance: ExercisePerformance): Long

    @Update
    fun update(exercisePerformance: ExercisePerformance)

    @Delete
    fun deleteItem(exercisePerformance: ExercisePerformance)
}