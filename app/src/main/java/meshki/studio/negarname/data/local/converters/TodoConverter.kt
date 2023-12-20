package meshki.studio.negarname.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import meshki.studio.negarname.entity.Todo

class TodoConverter {
    private val gson = Gson()
    private val type = object : TypeToken<Todo>() {}.type

    @TypeConverter
    fun todoToString(todo: Todo): String? {
        return gson.toJson(todo, type)
    }

    @TypeConverter
    fun stringToTodo(str: String?): Todo? {
        return gson.fromJson(str, type)
    }
}
