package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AppState(
    val maps: Map<String, CrisisMap> = emptyMap(),
    val parties: List<Party> = emptyList(),
    val currentMapId: String? = null,
    val currentTab: Int = 0,
    val userStatus: UserStatus = UserStatus.AVAILABLE,
    val isOnboarded: Boolean = false,
    val activeNotification: String? = null,
    val activeCheckin: String? = null,
    val activeBanner: String? = null,
    val selectedPointId: String? = null,
    val notifications: List<AppNotification> = emptyList(),
    val mapFilters: Set<PointType> = PointType.values().toSet(),
    val coordinatorSession: CoordinatorCheckinSession? = null,
    val isVerifyingCode: Boolean = false,
    val verificationError: String? = null
)

class AppViewModel : ViewModel() {
    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    init {
        val officialMap = CrisisMap(
            id = "official",
            label = "Resmi Ana Harita",
            networkLabel = "GÜVENLİ AĞ",
            type = MapType.OFFICIAL,
            role = Role.MEMBER,
            badge = "✓",
            points = listOf(
                StationPoint(
                    id = "p1",
                    lat = 41.0082,
                    lng = 28.9784,
                    type = PointType.GATHERING,
                    title = "Sultanahmet Meydanı",
                    neededCount = 50,
                    currentCount = 10,
                    notes = listOf("Çadır alanı kuruldu."),
                    roster = listOf(Member("AK", "#B6F24A")),
                    isEvent = false
                )
            )
        )

        val partyAMap = CrisisMap(
            id = "party_a",
            label = "Yıldız Mahallesi",
            networkLabel = "KAPALI AĞ",
            type = MapType.PARTY,
            role = Role.MEMBER,
            badge = "Y",
            points = emptyList()
        )

        val partyBMap = CrisisMap(
            id = "party_b",
            label = "Liman İşçileri",
            networkLabel = "KAPALI AĞ",
            type = MapType.PARTY,
            role = Role.COORDINATOR,
            badge = "L",
            points = emptyList()
        )

        val parties = listOf(
            Party("party_a", "🏘️", "Yıldız Mahallesi", 120, 45, Role.MEMBER, true, "Yerel Gözetim"),
            Party("party_b", "⚓", "Liman İşçileri", 300, 150, Role.COORDINATOR, true, "Kritik Lojistik")
        )

        val initialNotifications = listOf(
            AppNotification("n1", "Yeni Güncelleme", "Harita kullanım kılavuzu eklendi.", "10 dk önce", NotificationType.INFO, false),
            AppNotification("n2", "Sultanahmet Tahliye", "Bölgede yoğunluk azalıyor.", "1 saat önce", NotificationType.ALERT, true),
            AppNotification("n3", "Yoklama Beklentisi", "Liman İşçileri koordinatörü yoklama başlattı.", "2 saat önce", NotificationType.CHECKIN, true)
        )

        _state.update {
            it.copy(
                maps = mapOf(
                    officialMap.id to officialMap,
                    partyAMap.id to partyAMap,
                    partyBMap.id to partyBMap
                ),
                parties = parties,
                currentMapId = officialMap.id,
                isOnboarded = false,
                notifications = initialNotifications
            )
        }
    }

    fun selectPoint(pointId: String?) {
        _state.update { it.copy(selectedPointId = pointId) }
    }
    
    fun clearNotification() {
        _state.update { it.copy(activeNotification = null) }
    }

    fun joinPoint(pointId: String) {
        _state.update { state ->
            val myMember = Member("BEN", "#5AB0EC")
            var newMaps = state.maps.mapValues { (_, map) ->
                map.copy(points = map.points.map { pt ->
                    if (pt.roster.any { it.initials == "BEN" }) {
                        pt.copy(roster = pt.roster.filter { it.initials != "BEN" }, currentCount = pt.currentCount - 1)
                    } else pt
                })
            }
            var wasFull = false
            newMaps = newMaps.mapValues { (_, map) ->
                map.copy(points = map.points.map { pt ->
                    if (pt.id == pointId) {
                        if (pt.currentCount >= pt.neededCount) wasFull = true
                        pt.copy(roster = pt.roster + myMember, currentCount = pt.currentCount + 1)
                    } else pt
                })
            }
            state.copy(
                maps = newMaps,
                userStatus = UserStatus.ON_DUTY,
                activeNotification = if (wasFull) "Nokta dolu — yedeğe geçildi." else "Nöbete katılındı."
            )
        }
    }
    
    fun leavePoint(pointId: String) {
        _state.update { state ->
            val newMaps = state.maps.mapValues { (_, map) ->
                map.copy(points = map.points.map { pt ->
                    if (pt.id == pointId && pt.roster.any { it.initials == "BEN" }) {
                        pt.copy(roster = pt.roster.filter { it.initials != "BEN" }, currentCount = pt.currentCount - 1)
                    } else pt
                })
            }
            state.copy(
                maps = newMaps,
                userStatus = UserStatus.AVAILABLE,
                activeNotification = "Nöbetten ayrıldınız."
            )
        }
    }
    fun cycleStatus() {
        _state.update {
            val nextStatus = when (it.userStatus) {
                UserStatus.AVAILABLE -> UserStatus.EN_ROUTE
                UserStatus.EN_ROUTE -> UserStatus.ON_DUTY
                UserStatus.ON_DUTY -> UserStatus.AVAILABLE
            }
            it.copy(userStatus = nextStatus)
        }
    }
    fun switchMap(mapId: String) {
        _state.update { it.copy(currentMapId = mapId) }
    }
    fun switchTab(tabIndex: Int) {
        _state.update { it.copy(currentTab = tabIndex) }
    }
    fun showNotification(message: String) {
        _state.update { it.copy(activeNotification = message) }
    }
    fun triggerCoordAction() {
        val currentState = _state.value
        val map = currentState.maps[currentState.currentMapId] ?: return
        if (map.type == MapType.OFFICIAL) {
            _state.update { it.copy(activeBanner = "Dikkat: Sultanahmet Meydanı güncel toplanma noktası olarak işaretlendi.") }
        } else {
            if (map.role == Role.COORDINATOR) {
                // start coordinator check-in
                _state.update { 
                    it.copy(
                        coordinatorSession = CoordinatorCheckinSession(isActive = true, totalMembers = map.points.sumOf { p -> p.roster.size } + 120, waitingMembers = 120),
                        activeNotification = "Yoklama başlatılıyor..."
                    ) 
                }
                viewModelScope.launch {
                    var safe = 0
                    var help = 0
                    var wait = 120
                    var logs = listOf<String>()
                    val names = listOf("Ahmet", "Ayşe", "Mehmet", "Elif", "Can", "Zeynep")
                    for (i in 1..10) {
                        delay(1200)
                        if (_state.value.coordinatorSession?.isActive == false) break
                        if (Math.random() > 0.8) {
                            help += 1
                            wait -= 1
                            logs = listOf("${names.random()} YARDIM İSTEDİ!") + logs
                        } else {
                            safe += 1
                            wait -= 1
                            logs = listOf("${names.random()} güvende.") + logs
                        }
                        _state.update { st ->
                            st.copy(coordinatorSession = st.coordinatorSession?.copy(
                                safeMembers = safe,
                                helpMembers = help,
                                waitingMembers = wait,
                                logs = logs.take(5)
                            ))
                        }
                    }
                }
            } else {
                _state.update { 
                    it.copy(activeNotification = "Bu haritada koordinatör değilsin — yoklama yetkin yok") 
                }
            }
        }
    }
    
    fun toggleMapFilter(type: PointType) {
        _state.update { 
            val newFilters = it.mapFilters.toMutableSet()
            if (newFilters.contains(type)) newFilters.remove(type) else newFilters.add(type)
            it.copy(mapFilters = newFilters)
        }
    }
    
    fun markNotificationRead(id: String) {
        _state.update {
            it.copy(notifications = it.notifications.map { n -> if (n.id == id) n.copy(isRead = true) else n })
        }
    }
    
    fun verifyCode(code: String) {
        if (code.isBlank()) return
        _state.update { it.copy(isVerifyingCode = true, verificationError = null) }
        viewModelScope.launch {
            delay(1500)
            if (code.length < 5) {
                _state.update { it.copy(isVerifyingCode = false, verificationError = "Geçersiz kod formatı.") }
            } else {
                _state.update { it.copy(isVerifyingCode = false, isOnboarded = true) }
            }
        }
    }
    
    fun stopCoordinatorSession() {
        _state.update { it.copy(coordinatorSession = null, activeNotification = "Yoklama sonlandırıldı.") }
    }
    fun respondCheckin(isSafe: Boolean) {
        val msg = if (isSafe) "Güvende olarak işaretlendin" else "Yardım talebin koordinatöre iletildi"
        _state.update { it.copy(activeCheckin = null, activeNotification = msg) }
    }
    fun dismissBanner() {
        _state.update { it.copy(activeBanner = null) }
    }
    fun actOnBanner() {
        _state.update { it.copy(activeBanner = null, selectedPointId = "p1", currentTab = 0) }
    }
    fun onboard() {
        // Obsolete, using verifyCode now, but keep for fallback
        _state.update { it.copy(isOnboarded = true) }
    }
}
