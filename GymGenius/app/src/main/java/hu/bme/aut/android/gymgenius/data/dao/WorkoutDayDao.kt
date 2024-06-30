package hu.bme.aut.android.gymgenius.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import hu.bme.aut.android.gymgenius.data.table.WorkoutDay

@Dao
interface WorkoutDayDao {
    @Query("SELECT * FROM WorkoutDay")
    fun getAll(): List<WorkoutDay>

    @Insert
    fun insert(exercisePerformance: WorkoutDay): Long

    @Update
    fun update(exercisePerformance: WorkoutDay)

    @Delete
    fun deleteItem(exercisePerformance: WorkoutDay)
}