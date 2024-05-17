package meshki.studio.negarname.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import meshki.studio.negarname.ui.todos.TodoEntity

class TodoConverter {
    private val gson = Gson()
    private val type = object : TypeToken<TodoEntity>() {}.type

    @TypeConverter
    fun todoToString(todoEntity: TodoEntity): String? {
        return gson.toJson(todoEntity, type)
    }

    @TypeConverter
    fun stringToTodo(str: String?): TodoEntity? {
        return gson.fromJson(str, type)
    }
}
