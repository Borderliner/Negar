package meshki.studio.negarname.ui.app

import android.content.Context
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import meshki.studio.negarname.data.repository.DatabaseRepository
import meshki.studio.negarname.data.storage.Storage
import meshki.studio.negarname.data.storage.StorageConstants
import meshki.studio.negarname.ui.util.extensions.getCurrentLocale
import meshki.studio.negarname.ui.util.extensions.setLocale
import java.lang.ref.WeakReference

class AppViewModel(private val dataStore: Storage, context: Context) : ViewModel() {
    private val ctx = WeakReference(context)
    private val _appState = MutableStateFlow(AppState())
    val appState: StateFlow<AppState> = _appState.asStateFlow()
    val snackbarHost = SnackbarHostState()
    val drawerState = DrawerState(DrawerValue.Closed)

    init {
        onEvent(AppEvent.SetLocale(context.getCurrentLocale()))
        viewModelScope.launch {
            getTheme().collectLatest { theme ->
                _appState.update {
                    it.copy(theme = theme)
                }
            }
        }
    }

    fun onEvent(event: AppEvent) {
        when (event) {
            is AppEvent.SetTheme -> {
                setTheme(event.theme)
            }
            is AppEvent.SetLocale -> {
                setLocale(event.locale)
            }
            is AppEvent.SetReady -> {
                _appState.update {
                    it.copy(isReady = event.value)
                }
            }
            is AppEvent.SetBottomBarVisible -> {
                setBottomBarVisible(event.value)
            }
            is AppEvent.ShowSnackbar -> {
                viewModelScope.launch {
                    snackbarHost.showSnackbar(event.message, event.label, event.withDismissAction, event.duration)
                }
            }
        }
    }

    fun setTheme(theme: String) {
        viewModelScope.launch {
            dataStore.putPreference(StorageConstants.THEME, theme)
            _appState.update {
                it.copy(theme = theme)
            }
        }
    }

    suspend fun getTheme(): Flow<String> {
        return dataStore.getPreference(StorageConstants.THEME, "system")
    }

    fun setLocale(locale: String) {
        ctx.get()!!.setLocale(locale)
        val needsRtl = ctx.get()!!.getCurrentLocale() == "fa"
        _appState.update {
            it.copy(locale = locale, isRtl = needsRtl)
        }
    }

    fun setBottomBarVisible(value: Boolean) {
        _appState.update {
            it.copy(isBottomBarVisible = value)
        }
    }

    suspend fun showSnackbar(message: String, actionLabel: String? = null, duration: SnackbarDuration = SnackbarDuration.Short, withDismissAction: Boolean = false): SnackbarResult {
        return snackbarHost.showSnackbar(
            message = message,
            actionLabel = actionLabel,
            duration = duration,
            withDismissAction = withDismissAction
        )
    }

    fun getLocaleName(): String = when(appState.value.locale) {
        "fa" -> "پارسی \uD83C\uDDEE\uD83C\uDDF7"
        "en" -> "English \uD83C\uDDFA\uD83C\uDDF8"
        "fr" -> "Français \uD83C\uDDEB\uD83C\uDDF7"
        else -> ""
    }
}
