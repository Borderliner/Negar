package meshki.studio.negar

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _isReady = mutableStateOf(false)
    val isReady get() = _isReady

    init {
        viewModelScope.launch {
            _isReady.value = true
        }
    }
}
