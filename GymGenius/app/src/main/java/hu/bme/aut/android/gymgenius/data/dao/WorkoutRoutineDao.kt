package hu.bme.aut.android.gymgenius.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import hu.bme.aut.android.gymgenius.data.table.WorkoutRoutine

@Dao
interface WorkoutRoutineDao {
    @Query("SELECT * FROM WorkoutRoutine")
    fun getAll(): List<WorkoutRoutine>

    @Insert
    fun insert(exercisePerformance: WorkoutRoutine): Long

    @Update
    fun update(exercisePerformance: WorkoutRoutine)

    @Delete
    fun deleteItem(exercisePerformance: WorkoutRoutine)
}