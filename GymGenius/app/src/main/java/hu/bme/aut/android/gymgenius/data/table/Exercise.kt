package hu.bme.aut.android.gymgenius.data.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise")
data class Exercise(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo(name = "nev") var name: String,
    @ColumnInfo(name = "set") var set: Int,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "rest") var rest: Int
){
    override fun toString(): String {
        return name
    }
}