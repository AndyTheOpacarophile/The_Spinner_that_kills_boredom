package com.example.data

import kotlinx.coroutines.flow.Flow

class VibeRepository(
    private val activityDao: ActivityDao,
    private val logDao: ActivityLogDao,
    private val profileDao: UserProfileDao
) {
    // Activities
    val allActivities: Flow<List<ActivityEntity>> = activityDao.getAllActivities()
    val favoriteActivities: Flow<List<ActivityEntity>> = activityDao.getFavoriteActivities()

    fun getActivitiesByCategory(category: String): Flow<List<ActivityEntity>> {
        return activityDao.getActivitiesByCategory(category)
    }

    suspend fun insertActivity(activity: ActivityEntity) {
        activityDao.insertActivity(activity)
    }

    suspend fun updateFavorite(id: Int, isFavorite: Boolean) {
        activityDao.updateFavorite(id, isFavorite)
    }

    suspend fun deleteActivity(activity: ActivityEntity) {
        activityDao.deleteActivity(activity)
    }

    // Logs
    val allLogs: Flow<List<ActivityLogEntity>> = logDao.getAllLogs()
    val totalMinutes: Flow<Int?> = logDao.getTotalMinutesFlow()
    val totalCount: Flow<Int?> = logDao.getTotalCountFlow()

    suspend fun logActivityCompletion(activityId: Int, title: String, category: String, durationMinutes: Int) {
        val logObj = ActivityLogEntity(
            activityId = activityId,
            activityTitle = title,
            category = category,
            durationMinutes = durationMinutes
        )
        logDao.insertLog(logObj)
    }

    suspend fun deleteLog(logId: Int) {
        logDao.deleteLog(logId)
    }

    // User Profile
    val userProfile: Flow<UserProfileEntity?> = profileDao.getProfileFlow()

    suspend fun getProfileDirect(): UserProfileEntity? {
        return profileDao.getProfileDirect()
    }

    suspend fun saveProfile(email: String, nickname: String) {
        val profile = UserProfileEntity(email = email, nickname = nickname)
        profileDao.saveProfile(profile)
    }

    suspend fun clearProfile() {
        profileDao.clearProfile()
    }
}
