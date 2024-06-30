package hu.bme.aut.android.gymgenius.data.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "workoutDay",
    foreignKeys = [ForeignKey(
        entity = WorkoutRoutine::class,
        childColumns = ["workoutRoutineId"],
        parentColumns = ["id"]
    )])
data class WorkoutDay(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo(name = "workoutRoutineId") var workoutRoutineId: Long? = null,
    @ColumnInfo(name = "nev") var name: String
){
}