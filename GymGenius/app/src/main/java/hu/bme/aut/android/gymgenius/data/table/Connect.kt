package hu.bme.aut.android.gymgenius.data.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "connect",
    foreignKeys = [ForeignKey(
        entity = Exercise::class,
        childColumns = ["exerciseId"],
        parentColumns = ["id"]
    ),
    ForeignKey(
        entity = WorkoutDay::class,
        childColumns = ["workoutDayId"],
        parentColumns = ["id"]
    )])
data class Connect(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo(name = "exerciseId") var exerciseId: Long? = null,
    @ColumnInfo(name = "workoutDayId") var workoutDayId: Long? = null,
    @ColumnInfo(name = "exerciseNumber") var exerciseNumber: Int? = null
){
}