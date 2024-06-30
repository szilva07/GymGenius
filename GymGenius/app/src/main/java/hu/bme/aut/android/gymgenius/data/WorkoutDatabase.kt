package hu.bme.aut.android.gymgenius.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import hu.bme.aut.android.gymgenius.data.table.Connect
import hu.bme.aut.android.gymgenius.data.dao.ConnectDao
import hu.bme.aut.android.gymgenius.data.table.Exercise
import hu.bme.aut.android.gymgenius.data.dao.ExerciseDao
import hu.bme.aut.android.gymgenius.data.table.ExercisePerformance
import hu.bme.aut.android.gymgenius.data.dao.ExercisePerformanceDao
import hu.bme.aut.android.gymgenius.data.table.WorkoutDay
import hu.bme.aut.android.gymgenius.data.dao.WorkoutDayDao
import hu.bme.aut.android.gymgenius.data.table.WorkoutRoutine
import hu.bme.aut.android.gymgenius.data.dao.WorkoutRoutineDao

@Database(entities = [ExercisePerformance::class, Exercise::class, Connect::class, WorkoutDay::class, WorkoutRoutine::class], version = 2)
@TypeConverters(value = [WorkoutRoutine.Split::class, ExercisePerformance.LocalDateConverter::class])
abstract class WorkoutDatabase : RoomDatabase() {
    abstract fun exercisePerformanceDao(): ExercisePerformanceDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun connectDao(): ConnectDao
    abstract fun workoutDayDao(): WorkoutDayDao
    abstract fun workoutRoutineDao(): WorkoutRoutineDao

    companion object {
        fun getDatabase(applicationContext: Context): WorkoutDatabase {
            return Room.databaseBuilder(
                applicationContext,
                WorkoutDatabase::class.java,
                "workout4"
            ).build();
        }
    }
}