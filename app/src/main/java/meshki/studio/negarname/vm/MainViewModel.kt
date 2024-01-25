package meshki.studio.negarname.vm

import android.content.Context
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import meshki.studio.negarname.data.storage.StorageApi
import meshki.studio.negarname.data.storage.StorageConstants
import meshki.studio.negarname.util.getCurrentLocale
import timber.log.Timber
import java.lang.ref.WeakReference

class MainViewModel(
    private val dataStore: StorageApi,
    ctx: Context) : ViewModel() {
    private val _ctx = WeakReference(ctx)
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
        viewModelScope.launch {
            getTheme().collectLatest {
                _theme.value = it
                _isReady.value = true
                _locale.value = getCurrentLocale(ctx)
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
        meshki.studio.negarname.util.setLocale(_ctx.get()!!, tag)
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
