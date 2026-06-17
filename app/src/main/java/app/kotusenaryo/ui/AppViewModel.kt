package app.kotusenaryo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kotusenaryo.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

data class AppState(
    val currentTab: Int = 0,
    val isOnboarded: Boolean = false,
    val isVerifyingCode: Boolean = false,
    val verificationError: String? = null,
    val activeBanner: String? = null,
    val maps: List<CrisisMap> = listOf(
        CrisisMap("m1", "Resmi Ana Harita", "Genel kriz koordinasyon", false),
        CrisisMap("m2", "Grup Alfa", "Arama Kurtarma Gönüllüleri", true)
    ),
    val currentMapId: String = "m1",
    val activeFilter: PointType? = null,
    val selectedPointId: String? = null,
    val profile: UserProfile = UserProfile("u1", "Operatör_7"),
    val notifications: List<Notification> = listOf(
        Notification("n1", "Sistem", "Harita güncellendi", "10:42")
    )
)

class AppViewModel : ViewModel() {
    private val _state = MutableStateFlow(AppState())
    val state = _state.asStateFlow()

    fun verifyCode(code: String) {
        viewModelScope.launch {
            _state.update { it.copy(isVerifyingCode = true, verificationError = null) }
            delay(1000)
            if (code == "ALPHA-77") {
                _state.update { it.copy(isVerifyingCode = false, isOnboarded = true) }
                showNotification("Hoş geldin, Operatör.")
            } else {
                _state.update { it.copy(isVerifyingCode = false, verificationError = "Geçersiz veya süresi dolmuş kod.") }
            }
        }
    }

    fun setTab(index: Int) { _state.update { it.copy(currentTab = index) } }
    fun selectMap(id: String) { _state.update { it.copy(currentMapId = id, selectedPointId = null) } }
    fun toggleMapFilter(type: PointType) {
        _state.update { if(it.activeFilter == type) it.copy(activeFilter = null) else it.copy(activeFilter = type) }
    }
    fun selectPoint(id: String?) { _state.update { it.copy(selectedPointId = id) } }
    fun showNotification(msg: String) {
        viewModelScope.launch {
            _state.update { it.copy(activeBanner = msg) }
            delay(3000)
            _state.update { it.copy(activeBanner = null) }
        }
    }
    fun markNotificationRead(id: String) {
        _state.update { s -> s.copy(notifications = s.notifications.map { if(it.id == id) it.copy(isRead = true) else it }) }
    }
}
