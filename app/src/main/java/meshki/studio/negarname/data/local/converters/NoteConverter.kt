package meshki.studio.negarname.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import meshki.studio.negarname.ui.notes.entities.NoteEntity

class NoteConverter {
    private val gson = Gson()
    private val type = object : TypeToken<NoteEntity>() {}.type

    @TypeConverter
    fun noteToString(noteEntity: NoteEntity): String? {
        return gson.toJson(noteEntity, type)
    }

    @TypeConverter
    fun stringToNote(str: String?): NoteEntity? {
        return gson.fromJson(str, type)
    }
}
