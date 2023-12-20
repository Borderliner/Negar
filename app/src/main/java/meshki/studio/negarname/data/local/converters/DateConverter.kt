package meshki.studio.negarname.data.local.converters

import androidx.room.TypeConverter
import java.util.Date

class DateConverter {
    @TypeConverter
    fun toDate(dateLong: Long?): Date? {
        return when (dateLong) {
            null -> null
            else -> Date(dateLong)
        }
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return when (date) {
            null -> null
            else -> date.time
        }
    }
}
