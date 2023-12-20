package meshki.studio.negarname.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import meshki.studio.negarname.entity.Note

class NoteConverter {
    private val gson = Gson()
    private val type = object : TypeToken<Note>() {}.type

    @TypeConverter
    fun noteToString(note: Note): String? {
        return gson.toJson(note, type)
    }

    @TypeConverter
    fun stringToNote(str: String?): Note? {
        return gson.fromJson(str, type)
    }
}
