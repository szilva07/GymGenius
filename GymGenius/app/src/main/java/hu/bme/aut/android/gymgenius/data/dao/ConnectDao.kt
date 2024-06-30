package hu.bme.aut.android.gymgenius.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import hu.bme.aut.android.gymgenius.data.table.Connect

@Dao
interface ConnectDao {
    @Query("SELECT * FROM Connect")
    fun getAll(): List<Connect>

    @Insert
    fun insert(exercisePerformance: Connect): Long

    @Update
    fun update(exercisePerformance: Connect)

    @Delete
    fun deleteItem(exercisePerformance: Connect)
}