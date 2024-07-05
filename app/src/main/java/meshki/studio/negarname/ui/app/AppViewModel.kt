package meshki.studio.negarname.ui.app

import android.content.Context
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import meshki.studio.negarname.data.storage.Storage
import meshki.studio.negarname.data.storage.StorageConstants
import meshki.studio.negarname.ui.util.extensions.getCurrentLocale
import meshki.studio.negarname.ui.util.extensions.setLocale
import java.lang.ref.WeakReference

class AppViewModel(private val dataStore: Storage, context: Context) : ViewModel() {
    private val ctx = WeakReference(context)
    private val _isReady = mutableStateOf(false)
    private val _locale = mutableStateOf("en")
    private val _bottomBarVisible = mutableStateOf(true)
    private val _theme = mutableStateOf("system")
    val theme get() = _theme.value
    val isReady get() = _isReady.value
    val isRtl get() = _locale.value == "fa"
    val locale get() = _locale.value
    val isBottomBarVisible: State<Boolean> = _bottomBarVisible
    val drawerState = DrawerState(DrawerValue.Closed)

    init {
        _locale.value = context.getCurrentLocale()
        viewModelScope.launch {
            getTheme().collectLatest {
                _theme.value = it
                _isReady.value = true
            }
        }
    }

    fun setTheme(themeName: String) {
        viewModelScope.launch {
            dataStore.putPreference(StorageConstants.THEME, themeName)
        }
    }

    suspend fun getTheme(): Flow<String> {
        return dataStore.getPreference(StorageConstants.THEME, "system")
    }

    fun setLocale(tag: String) {
        _locale.value = tag
        ctx.get()!!.setLocale(tag)
    }

    fun setBottomBarVisible(value: Boolean) {
        _bottomBarVisible.value = value
    }

    fun getLocaleName(): String = when(locale) {
        "fa" -> "پارسی \uD83C\uDDEE\uD83C\uDDF7"
        "en" -> "English \uD83C\uDDFA\uD83C\uDDF8"
        "fr" -> "Français \uD83C\uDDEB\uD83C\uDDF7"
        else -> ""
    }
}
