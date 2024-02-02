package meshki.studio.negarname.data.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "meshki.studio.negarname.shared.preferences")

object StorageConstants {
    val FIRST_TIME = booleanPreferencesKey("FIRST_TIME")
    val THEME = stringPreferencesKey("THEME")
}


class PersistentStorage(
    context: Context
) : Storage {
    // dataSource access the DataStore file and does the manipulation based on our requirements.
    private val dataSource = context.dataStore

    /* This returns us a flow of data from DataStore.
    Basically as soon we update the value in Datastore,
    the values returned by it also changes. */
    override suspend fun <T> getPreference(key: Preferences.Key<T>, defaultValue: T):
            Flow<T> = dataSource.data.catch { exception ->
        if (exception is IOException){
            emit(emptyPreferences())
        }else{
            throw exception
        }
    }.map { preferences->
        val result = preferences[key]?: defaultValue
        result
    }

    /* This returns the last saved value of the key. If we change the value,
        it wont effect the values produced by this function */
    override suspend fun <T> getFirstPreference(key: Preferences.Key<T>, defaultValue: T) :
            T = dataSource.data.first()[key] ?: defaultValue

    // This Sets the value based on the value passed in value parameter.
    override suspend fun <T> putPreference(key: Preferences.Key<T>, value: T) {
        dataSource.edit {   preferences ->
            preferences[key] = value
        }
    }

    // This Function removes the Key Value pair from the datastore, hereby removing it completely.
    override suspend fun <T> removePreference(key: Preferences.Key<T>) {
        dataSource.edit { preferences ->
            preferences.remove(key)
        }
    }

    // This function clears the entire Preference Datastore.
    override suspend fun clearAllPreference() {
        dataSource.edit { preferences ->
            preferences.clear()
        }
    }



//    override fun insert(data: List<T>): Flow<Int> {
//        return flow {
//            val cachedDataClone = getAll().first().toMutableList()
//            cachedDataClone.addAll(data)
//            dataStore.edit {
//                val jsonString = gson.toJson(cachedDataClone, type)
//                it[preferenceKey] = jsonString
//                emit(OPERATION_SUCCESS)
//            }
//        }
//    }
//
//    override fun insert(data: T): Flow<Int> {
//        return insert(listOf(data))
//    }
//
//    override fun get(where: (T) -> Boolean): Flow<T> {
//        return getAll().map { cachedData ->
//            cachedData.first(where)
//        }
//    }
//
//    override fun clearAll(): Flow<Int> {
//        return flow {
//            dataStore.edit {
//                it.remove(preferenceKey)
//                emit(OPERATION_SUCCESS)
//            }
//        }
//    }
}
