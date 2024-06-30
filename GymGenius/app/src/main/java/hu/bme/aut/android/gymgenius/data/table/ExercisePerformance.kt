package hu.bme.aut.android.gymgenius.data.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.time.LocalDate


@Entity(tableName = "exercisePerformance",
    foreignKeys = [ForeignKey(
        entity = Exercise::class,
        childColumns = ["exerciseId"],
        parentColumns = ["id"]
)])
data class ExercisePerformance(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo(name = "exerciseId") var exerciseId: Long? = null,
    @ColumnInfo(name = "date") var date: LocalDate,
    @ColumnInfo(name = "set1RepCount") var set1RepCount: Int,
    @ColumnInfo(name = "set2RepCount") var set2RepCount: Int,
    @ColumnInfo(name = "set3RepCount") var set3RepCount: Int,
    @ColumnInfo(name = "set4RepCount") var set4RepCount: Int,
    @ColumnInfo(name = "set5RepCount") var set5RepCount: Int,
    @ColumnInfo(name = "set6RepCount") var set6RepCount: Int,
    @ColumnInfo(name = "set7RepCount") var set7RepCount: Int,
    @ColumnInfo(name = "set1Weight") var set1Weight: Double,
    @ColumnInfo(name = "set2Weight") var set2Weight: Double,
    @ColumnInfo(name = "set3Weight") var set3Weight: Double,
    @ColumnInfo(name = "set4Weight") var set4Weight: Double,
    @ColumnInfo(name = "set5Weight") var set5Weight: Double,
    @ColumnInfo(name = "set6Weight") var set6Weight: Double,
    @ColumnInfo(name = "set7Weight") var set7Weight: Double
) {
    class LocalDateConverter {
        companion object {
            @JvmStatic
            @TypeConverter
            open fun toDate(dateString: String?): LocalDate? {
                return if (dateString == null) {
                    null
                } else {
                    LocalDate.parse(dateString)
                }
            }

            @JvmStatic
            @TypeConverter
            fun toDateString(date: LocalDate?): String? {
                return date?.toString()
            }
        }
    }
}