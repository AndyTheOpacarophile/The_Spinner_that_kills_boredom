package com.example.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

sealed interface WheelState {
    object Idle : WheelState
    object Spinning : WheelState
    data class Selected(val activity: ActivityEntity) : WheelState
}

class VibeViewModel(application: Application) : AndroidViewModel(application) {
    private val database = VibeDatabase.getDatabase(application, viewModelScope)
    private val repository = VibeRepository(
        database.activityDao(),
        database.logDao(),
        database.profileDao()
    )

    // User Profile
    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // All activities list
    val allActivities: StateFlow<List<ActivityEntity>> = repository.allActivities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Favorites list
    val favoriteActivities: StateFlow<List<ActivityEntity>> = repository.favoriteActivities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Statistics
    val allLogs: StateFlow<List<ActivityLogEntity>> = repository.allLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalMinutes: StateFlow<Int> = repository.totalMinutes
        .map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalCount: StateFlow<Int> = repository.totalCount
        .map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Filter states
    var selectedCategories by mutableStateOf(setOf("灵感漫游", "居室整活", "城市漂流", "肉身出逃"))
    var selectedCostFilter by mutableStateOf("全部") // 全部, 免费, 付费
    var selectedSocialFilter by mutableStateOf("全部") // 全部, Solo, Solo/Friends, Friends

    fun toggleCategoryFilter(category: String) {
        if (category == "全部") {
            // "全部" toggles: if not all selected, select all. If all selected, keep all selected (default state is select all).
            selectedCategories = setOf("灵感漫游", "居室整活", "城市漂流", "肉身出逃")
        } else {
            val current = selectedCategories.toMutableSet()
            if (current.contains(category)) {
                // Prevent empty filter to avoid breaking spinner
                if (current.size > 1) {
                    current.remove(category)
                }
            } else {
                current.add(category)
            }
            selectedCategories = current
        }
    }

    // Spin states
    var wheelState by mutableStateOf<WheelState>(WheelState.Idle)
        private set

    var targetRotationAngle by mutableStateOf(0f)
        private set

    // Selected temporary activity for logging
    var activeLoggingActivity by mutableStateOf<ActivityEntity?>(null)

    // Filter match helper
    fun matchesFilters(activity: ActivityEntity): Boolean {
        val catMatch = selectedCategories.contains(activity.category)
        val costMatch = when (selectedCostFilter) {
            "全部" -> true
            "免费" -> activity.cost.equals("Free", ignoreCase = true) || activity.cost == "0"
            "付费" -> !activity.cost.equals("Free", ignoreCase = true) && activity.cost != "0"
            else -> true
        }
        val socialMatch = when (selectedSocialFilter) {
            "全部" -> true
            "单人" -> activity.social.contains("Solo", ignoreCase = true)
            "多人" -> activity.social.contains("Friends", ignoreCase = true)
            else -> true
        }
        return catMatch && costMatch && socialMatch
    }

    // Blend categories beautifully around the wheel slice using interleaving technique
    fun interleaveCategories(list: List<ActivityEntity>): List<ActivityEntity> {
        val groups = list.groupBy { it.category }.values.map { it.toMutableList() }
        val result = ArrayList<ActivityEntity>(list.size)
        var remaining = true
        var index = 0
        while (remaining) {
            remaining = false
            for (group in groups) {
                if (index < group.size) {
                    result.add(group[index])
                    remaining = true
                }
            }
            index++
        }
        return result
    }

    // Get final sorted interleaved lists to feed both Roulette and ViewModel Spin selection
    fun getActiveActivities(): List<ActivityEntity> {
        val filtered = allActivities.value.filter { matchesFilters(it) }
        return interleaveCategories(filtered)
    }

    // Spin function with simulated deceleration physics for maximum visual satisfaction
    fun spinWheel() {
        if (wheelState == WheelState.Spinning) return

        viewModelScope.launch {
            val validActivities = getActiveActivities()
            if (validActivities.isEmpty()) {
                // No matches
                return@launch
            }

            wheelState = WheelState.Spinning
            
            // Randomly select target item
            val selectedIndex = Random.nextInt(validActivities.size)
            val selectedActivity = validActivities[selectedIndex]

            // We make the wheel spin multiple full rotations (e.g., 5 to 8 rotations) 
            // plus an offset to land exactly on the slice.
            val currentRotation = targetRotationAngle
            val baseRotation = currentRotation - (currentRotation % 360f)
            val extraRotations = (5 + Random.nextInt(4)) * 360f
            val sectionAngle = 360f / validActivities.size
            val targetOffsetInDegrees = 360f - (selectedIndex * sectionAngle) - (sectionAngle / 2f)
            
            // New target angle guarantees forward rotation and exact alignment on physical slice
            targetRotationAngle = baseRotation + extraRotations + targetOffsetInDegrees

            // Simulate delay for spin completion animation
            // In the UI, we can animate rotation using animateFloatAsState
            delay(2800)

            wheelState = WheelState.Selected(selectedActivity)
            // Save as active for easy track-logging
            activeLoggingActivity = selectedActivity
        }
    }

    fun resetWheel() {
        wheelState = WheelState.Idle
        activeLoggingActivity = null
    }

    // Log complete activity
    fun logCompletion(activityId: Int, title: String, category: String, durationMinutes: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.logActivityCompletion(
                activityId = activityId,
                title = title,
                category = category,
                durationMinutes = durationMinutes
            )
        }
    }

    fun deleteLog(logId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteLog(logId)
        }
    }

    // Toggle favorite status
    fun toggleFavorite(activity: ActivityEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateFavorite(activity.id, !activity.isFavorite)
        }
    }

    // Create a new customized activity
    fun addCustomActivity(
        title: String,
        category: String,
        duration: String,
        cost: String,
        social: String,
        energyLevel: String,
        description: String,
        link: String,
        tags: String,
        skill: String,
        minAge: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val newAct = ActivityEntity(
                title = title,
                category = category,
                duration = duration,
                cost = cost,
                social = social,
                energyLevel = energyLevel,
                description = description,
                link = link,
                tags = tags,
                isCustom = true,
                skill = skill,
                minAge = minAge
            )
            repository.insertActivity(newAct)
        }
    }

    fun deleteCustomActivity(activity: ActivityEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteActivity(activity)
        }
    }

    // User Profile Actions
    fun registerProfile(email: String, nickname: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveProfile(email, nickname)
        }
    }

    fun logoutProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearProfile()
        }
    }
}
