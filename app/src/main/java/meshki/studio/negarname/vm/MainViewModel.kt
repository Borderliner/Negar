package meshki.studio.negarname.vm

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import meshki.studio.negarname.util.getCurrentLocale
import java.lang.ref.WeakReference

class MainViewModel(ctx: Context) : ViewModel() {
    private val _ctx = WeakReference(ctx)
    private val _isReady = mutableStateOf(false)
    private val _locale = mutableStateOf("en")
    private val _bottomBarVisible = mutableStateOf(true)

    val isReady get() = _isReady.value
    val isRtl get() = _locale.value == "fa"
    val locale get() = _locale.value
    val isBottomBarVisible: State<Boolean> = _bottomBarVisible

    init {
        viewModelScope.launch {
            _isReady.value = true
            _locale.value = getCurrentLocale(ctx)
        }
    }

    fun setLocale(tag: String) {
        _locale.value = tag
        meshki.studio.negarname.util.setLocale(_ctx.get()!!, tag)
        println("Is RTL: $isRtl")
    }

    fun setBottomBarVisible(value: Boolean) {
        _bottomBarVisible.value = value
    }
}
