package meshki.studio.negarname

import android.content.Context
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel(ctx: Context) : ViewModel() {
    private val _isReady = mutableStateOf(false)
    val isReady get() = _isReady

    private val _isRtl = mutableStateOf(false)
    val isRtl get() = _isRtl

    private val _locale = mutableStateOf("en")
    val locale get() = _locale

    init {
        viewModelScope.launch {
            _isReady.value = true
            _locale.value = getCurrentLocale(ctx)
            _isRtl.value = locale.value == "fa"
        }
    }

    fun setLocale(ctx: Context, tag: String) {
        _locale.value = tag
        meshki.studio.negarname.setLocale(ctx, tag)
        println(locale.value)
    }
}
