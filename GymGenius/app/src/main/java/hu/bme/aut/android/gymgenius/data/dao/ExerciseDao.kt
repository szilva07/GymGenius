package hu.bme.aut.android.gymgenius.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import hu.bme.aut.android.gymgenius.data.table.Exercise

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM Exercise")
    fun getAll(): List<Exercise>

    @Insert
    fun insert(exercisePerformance: Exercise): Long

    @Update
    fun update(exercisePerformance: Exercise)

    @Delete
    fun deleteItem(exercisePerformance: Exercise)
}