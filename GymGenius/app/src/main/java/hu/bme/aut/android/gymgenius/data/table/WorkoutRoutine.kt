package hu.bme.aut.android.gymgenius.data.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(tableName = "workoutRoutine")
data class WorkoutRoutine(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo(name = "nev") var name: String,
    @ColumnInfo(name = "split") var split: Split
){
    enum class Split{
        Body_Part,
        Upper_Lower_Body,
        Push_Pull_Legs,
        Full_Body,
        Arnold,
        Other;

        override fun toString(): String {
            return super.toString().replace('_', ' ')
        }

        companion object {
            @JvmStatic
            @TypeConverter
            fun getByOrdinal(ordinal: Int): Split? {
                var ret: Split? = null
                for (cat in values()) {
                    if (cat.ordinal == ordinal) {
                        ret = cat
                        break
                    }
                }
                return ret
            }

            @JvmStatic
            @TypeConverter
            fun toInt(category: Split): Int {
                return category.ordinal
            }
        }
    }
}