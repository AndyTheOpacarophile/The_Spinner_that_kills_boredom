package com.example.data

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String, // 灵感漫游, 居室整活, 城市漂流, 肉身出逃
    val duration: String,
    val cost: String,
    val social: String,
    val energyLevel: String, // Low, Medium, High
    val description: String,
    val link: String,
    val tags: String, // Comma-separated tags
    val isFavorite: Boolean = false,
    val isCustom: Boolean = false,
    val skill: String = "无",
    val minAge: Int = 12
)

@Entity(tableName = "activity_logs")
data class ActivityLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val activityId: Int,
    val activityTitle: String,
    val category: String,
    val timestamp: Long = System.currentTimeMillis(),
    val durationMinutes: Int
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val email: String,
    val nickname: String,
    val joinedAt: Long = System.currentTimeMillis()
)

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activities ORDER BY id ASC")
    fun getAllActivities(): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities WHERE isFavorite = 1 ORDER BY title ASC")
    fun getFavoriteActivities(): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities WHERE category = :category ORDER BY id ASC")
    fun getActivitiesByCategory(category: String): Flow<List<ActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(activities: List<ActivityEntity>)

    @Query("UPDATE activities SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Int, isFavorite: Boolean)

    @Delete
    suspend fun deleteActivity(activity: ActivityEntity)

    @Query("SELECT COUNT(*) FROM activities")
    suspend fun getActivityCountDirect(): Int
}

@Dao
interface ActivityLogDao {
    @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<ActivityLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ActivityLogEntity)

    @Query("DELETE FROM activity_logs WHERE id = :logId")
    suspend fun deleteLog(logId: Int)

    @Query("SELECT SUM(durationMinutes) FROM activity_logs")
    fun getTotalMinutesFlow(): Flow<Int?>

    @Query("SELECT COUNT(*) FROM activity_logs")
    fun getTotalCountFlow(): Flow<Int?>
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile LIMIT 1")
    fun getProfileFlow(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile LIMIT 1")
    suspend fun getProfileDirect(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: UserProfileEntity)

    @Query("DELETE FROM user_profile")
    suspend fun clearProfile()
}

@Database(
    entities = [ActivityEntity::class, ActivityLogEntity::class, UserProfileEntity::class],
    version = 2,
    exportSchema = false
)
abstract class VibeDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
    abstract fun logDao(): ActivityLogDao
    abstract fun profileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: VibeDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): VibeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VibeDatabase::class.java,
                    "vibe_settle_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(VibeDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class VibeDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.activityDao())
                }
            }
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    try {
                        val count = database.activityDao().getActivityCountDirect()
                        if (count == 0) {
                            populateDatabase(database.activityDao())
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        suspend fun populateDatabase(activityDao: ActivityDao) {
            // Seed sample activities with highly creative options and Bento Grid names
            val presetActivities = listOf(
                // Category 1: 灵感漫游
                ActivityEntity(
                    title = "Build a 3AM Playlist",
                    category = "灵感漫游",
                    duration = "15-40 min",
                    cost = "Free",
                    social = "Solo/Friends",
                    energyLevel = "Low",
                    description = "Make a playlist that feels like staring out a rainy window at 3AM.",
                    link = "https://open.spotify.com/",
                    tags = "music,aesthetic,creative",
                    skill = "无",
                    minAge = 12
                ),
                ActivityEntity(
                    title = "GeoGuessr Quick Match",
                    category = "灵感漫游",
                    duration = "5-15 min",
                    cost = "Free",
                    social = "Solo/Friends",
                    energyLevel = "Low",
                    description = "Play one fast GeoGuessr round and see if you are secretly cracked at geography.",
                    link = "https://www.geoguessr.com/",
                    tags = "game,quick,geography",
                    skill = "观察力",
                    minAge = 12
                ),
                ActivityEntity(
                    title = "Pinterest Identity Crisis",
                    category = "灵感漫游",
                    duration = "15-30 min",
                    cost = "Free",
                    social = "Solo",
                    energyLevel = "Low",
                    description = "Create a board for the person you wish you were.",
                    link = "https://www.pinterest.com/",
                    tags = "aesthetic,internet,creative",
                    skill = "审美",
                    minAge = 12
                ),
                ActivityEntity(
                    title = "Wikipedia Speedrun",
                    category = "灵感漫游",
                    duration = "10-20 min",
                    cost = "Free",
                    social = "Solo/Friends",
                    energyLevel = "Low",
                    description = "Get from one random Wikipedia page to another in under 10 clicks.",
                    link = "https://www.wikipedia.org/",
                    tags = "internet,challenge,rabbit-hole",
                    skill = "联想能力",
                    minAge = 12
                ),
                ActivityEntity(
                    title = "Rednote Deep Dive",
                    category = "灵感漫游",
                    duration = "10-25 min",
                    cost = "Free",
                    social = "Solo",
                    energyLevel = "Low",
                    description = "Search a random niche hobby on 小红书 and fall into the rabbit hole.",
                    link = "https://www.xiaohongshu.com/",
                    tags = "internet,trend,rabbit-hole",
                    skill = "探索",
                    minAge = 12
                ),
                ActivityEntity(
                    title = "Make a Horrendous Meme",
                    category = "灵感漫游",
                    duration = "5-20 min",
                    cost = "Free",
                    social = "Solo/Friends",
                    energyLevel = "Low",
                    description = "Create the dumbest meme possible using your camera roll.",
                    link = "",
                    tags = "funny,creative,social",
                    skill = "幽默感",
                    minAge = 12
                ),
                ActivityEntity(
                    title = "深夜街景流浪 (Midnight Maps GPS)",
                    category = "灵感漫游",
                    duration = "15-30 min",
                    cost = "Free / WiFi",
                    social = "Solo",
                    energyLevel = "Low",
                    description = "Drop a random pin on Google Maps in rural Iceland/Japan and discover 3 cozy-looking houses/shops.",
                    link = "https://maps.google.com/",
                    tags = "internet,cozy,exploration",
                    skill = "想象力",
                    minAge = 12
                ),
                ActivityEntity(
                    title = "蒸汽波海报制作 (Vaporwave Design)",
                    category = "灵感漫游",
                    duration = "20-40 min",
                    cost = "Free",
                    social = "Solo",
                    energyLevel = "Low",
                    description = "Create a surreal, overly saturated retro cyberpunk image using free online editors with text in a foreign language.",
                    link = "https://www.canva.com",
                    tags = "creative,aesthetic,design",
                    skill = "审美力",
                    minAge = 12
                ),

                // Category 2: 居室整活
                ActivityEntity(
                    title = "Pushups Every Death",
                    category = "居室整活",
                    duration = "10-30 min",
                    cost = "Free",
                    social = "Solo/Friends",
                    energyLevel = "High",
                    description = "Every death in-game equals 10 pushups.",
                    link = "",
                    tags = "gaming,fitness,challenge",
                    skill = "意志力",
                    minAge = 16
                ),
                ActivityEntity(
                    title = "Boss Fight Workout",
                    category = "居室整活",
                    duration = "15-40 min",
                    cost = "Free",
                    social = "Solo",
                    energyLevel = "High",
                    description = "Exercise while anime OST plays dramatically in the background.",
                    link = "https://www.youtube.com/",
                    tags = "anime,fitness,dopamine",
                    skill = "体能",
                    minAge = 12
                ),
                ActivityEntity(
                    title = "Speedrun Shower",
                    category = "居室整活",
                    duration = "5-10 min",
                    cost = "Free",
                    social = "Solo",
                    energyLevel = "Medium",
                    description = "Finish your shower before the current song ends.",
                    link = "",
                    tags = "challenge,funny,quick",
                    skill = "敏捷性",
                    minAge = 8
                ),
                ActivityEntity(
                    title = "Mirror NPC Dialogue",
                    category = "居室整活",
                    duration = "5-15 min",
                    cost = "Free",
                    social = "Solo/Friends",
                    energyLevel = "Medium",
                    description = "Practice absurd NPC dialogue in front of the mirror.",
                    link = "",
                    tags = "chaotic,funny,acting",
                    skill = "演绎",
                    minAge = 6
                ),
                ActivityEntity(
                    title = "Floor Is Lava",
                    category = "居室整活",
                    duration = "5-15 min",
                    cost = "Free",
                    social = "Solo/Friends",
                    energyLevel = "Medium",
                    description = "Navigate your room without touching the floor.",
                    link = "",
                    tags = "challenge,fun,chaotic",
                    skill = "平衡力",
                    minAge = 6
                ),
                ActivityEntity(
                    title = "Reaction Time Duel",
                    category = "居室整活",
                    duration = "5-20 min",
                    cost = "Free",
                    social = "Solo/Friends",
                    energyLevel = "Medium",
                    description = "Compete for the fastest Human Benchmark reaction score.",
                    link = "https://humanbenchmark.com/tests/reactiontime",
                    tags = "competitive,gaming,quick",
                    skill = "反应力",
                    minAge = 8
                ),
                ActivityEntity(
                    title = "静音拟音功夫武打 (Muted Kung-Fu Shadows)",
                    category = "居室整活",
                    duration = "10-20 min",
                    cost = "Free",
                    social = "Solo",
                    energyLevel = "Medium",
                    description = "Shadowbox or dance to an anime/movie combat sequence while wearing soft socks to make zero landing sound.",
                    link = "",
                    tags = "funny,fitness,sneaky",
                    skill = "敏捷度",
                    minAge = 12
                ),
                ActivityEntity(
                    title = "铅笔重力平衡斜塔 (Pencil Balance Tower)",
                    category = "居室整活",
                    duration = "10-30 min",
                    cost = "Free",
                    social = "Solo/Friends",
                    energyLevel = "Low",
                    description = "Construct a structurally complex tower using nothing but pencils, kitchen cutlery, and gravity on your desk.",
                    link = "",
                    tags = "challenge,focus,indoor",
                    skill = "耐心与微操",
                    minAge = 8
                ),

                // Category 3: 城市漂流
                ActivityEntity(
                    title = "Color Walk",
                    category = "城市漂流",
                    duration = "15-40 min",
                    cost = "Free",
                    social = "Solo/Friends",
                    energyLevel = "Low",
                    description = "Pick a color and photograph 5 things matching it outside.",
                    link = "",
                    tags = "photography,citywalk,trend",
                    skill = "摄影",
                    minAge = 8
                ),
                ActivityEntity(
                    title = "Convenience Store Roulette",
                    category = "城市漂流",
                    duration = "10-25 min",
                    cost = "$2-10",
                    social = "Solo/Friends",
                    energyLevel = "Low",
                    description = "Buy the snack with the weirdest packaging.",
                    link = "",
                    tags = "food,funny,exploration",
                    skill = "探索精神",
                    minAge = 6
                ),
                ActivityEntity(
                    title = "Fake Tourist Mode",
                    category = "城市漂流",
                    duration = "30-90 min",
                    cost = "$0-10",
                    social = "Solo/Friends",
                    energyLevel = "Low",
                    description = "Go somewhere nearby you have never actually explored before.",
                    link = "",
                    tags = "citywalk,exploration,adventure",
                    skill = "好奇心",
                    minAge = 6
                ),
                ActivityEntity(
                    title = "Photodump Hunt",
                    category = "城市漂流",
                    duration = "20-45 min",
                    cost = "Free",
                    social = "Solo/Friends",
                    energyLevel = "Low",
                    description = "Take 10 photos that feel like an album cover.",
                    link = "",
                    tags = "photography,aesthetic,creative",
                    skill = "审美",
                    minAge = 8
                ),
                ActivityEntity(
                    title = "Find a Tiny Cafe",
                    category = "城市漂流",
                    duration = "20-60 min",
                    cost = "$5-15",
                    social = "Solo/Friends",
                    energyLevel = "Low",
                    description = "Walk until you discover a cafe you have never noticed before.",
                    link = "",
                    tags = "cafe,explore,citywalk",
                    skill = "发现美",
                    minAge = 12
                ),
                ActivityEntity(
                    title = "Silent Walk",
                    category = "城市漂流",
                    duration = "15-30 min",
                    cost = "Free",
                    social = "Solo",
                    energyLevel = "Low",
                    description = "Walk outside for 15 minutes without touching your phone.",
                    link = "",
                    tags = "mindfulness,healing,outside",
                    skill = "专注力",
                    minAge = 8
                ),
                ActivityEntity(
                    title = "百步街头无声默片 (100-Step Cinema)",
                    category = "城市漂流",
                    duration = "15-30 min",
                    cost = "Free",
                    social = "Solo",
                    energyLevel = "Low",
                    description = "Walk 100 paces observing passersby, translating their movements and expressions/gestures into silent cinema plot subtitles inside your head.",
                    link = "",
                    tags = "mindfulness,imaginary,humor",
                    skill = "观察力",
                    minAge = 10
                ),
                ActivityEntity(
                    title = "街头野性艺术字体采风 (Urban Brand Font Hunt)",
                    category = "城市漂流",
                    duration = "30-50 min",
                    cost = "Free",
                    social = "Solo/Friends",
                    energyLevel = "Low",
                    description = "Photograph 5 bizarre shop typography styles, misspelled English signs, or hand-painted old alleyway calligraphy notices.",
                    link = "",
                    tags = "photography,aesthetic,exploration",
                    skill = "艺术审美",
                    minAge = 12
                ),

                // Category 4: 肉身出逃
                ActivityEntity(
                    title = "Pokemon GO Walk",
                    category = "肉身出逃",
                    duration = "20-60 min",
                    cost = "Free",
                    social = "Solo/Friends",
                    energyLevel = "Medium",
                    description = "Gamify touching grass.",
                    link = "https://pokemongolive.com/",
                    tags = "walking,game,outside",
                    skill = "耐力",
                    minAge = 8
                ),
                ActivityEntity(
                    title = "Last 10 Second Sprint",
                    category = "肉身出逃",
                    duration = "5-15 min",
                    cost = "Free",
                    social = "Solo",
                    energyLevel = "High",
                    description = "Sprint before the crossing timer hits zero.",
                    link = "",
                    tags = "running,chaotic,quick",
                    skill = "爆发力",
                    minAge = 12
                ),
                ActivityEntity(
                    title = "Random Destination Run",
                    category = "肉身出逃",
                    duration = "15-40 min",
                    cost = "Free",
                    social = "Solo/Friends",
                    energyLevel = "High",
                    description = "Pick a random nearby landmark and race there.",
                    link = "",
                    tags = "running,challenge,adventure",
                    skill = "定向运动",
                    minAge = 12
                ),
                ActivityEntity(
                    title = "Staircase Boss Battle",
                    category = "肉身出逃",
                    duration = "10-25 min",
                    cost = "Free",
                    social = "Solo/Friends",
                    energyLevel = "High",
                    description = "Run stairs until your legs enter survival mode.",
                    link = "",
                    tags = "fitness,challenge,cardio",
                    skill = "心肺代偿",
                    minAge = 14
                ),
                ActivityEntity(
                    title = "Bike Without GPS",
                    category = "肉身出逃",
                    duration = "45-120 min",
                    cost = "Free",
                    social = "Solo/Friends",
                    energyLevel = "High",
                    description = "Cycle somewhere new without opening maps.",
                    link = "",
                    tags = "cycling,exploration,adventure",
                    skill = "空间位置感",
                    minAge = 14
                ),
                ActivityEntity(
                    title = "Catch Sunset Before It Ends",
                    category = "肉身出逃",
                    duration = "15-45 min",
                    cost = "Free",
                    social = "Solo/Friends",
                    energyLevel = "Medium",
                    description = "Rush outside and find the best sunset spot possible.",
                    link = "",
                    tags = "sunset,aesthetic,outside",
                    skill = "追光",
                    minAge = 6
                ),
                ActivityEntity(
                    title = "寻影踩线突围大作战 (Shadow Sidewalk Escape)",
                    category = "肉身出逃",
                    duration = "10-25 min",
                    cost = "Free",
                    social = "Solo/Friends",
                    energyLevel = "Medium",
                    description = "Walk alongside street sidewalks while stepping exclusively inside building shadows. Lose a 'life' if you touch direct sunlight!",
                    link = "",
                    tags = "challenge,outside,childhood",
                    skill = "平衡力与反应",
                    minAge = 8
                ),
                ActivityEntity(
                    title = "反向指南针迷走 (Reverse GPS Vagabond)",
                    category = "肉身出逃",
                    duration = "30-75 min",
                    cost = "Free",
                    social = "Solo/Friends",
                    energyLevel = "High",
                    description = "Pick a compass direction (e.g. North-West) and wander on foot/bike there, turning only when blocked by a real brick wall/private security gates.",
                    link = "",
                    tags = "adventure,cycling,exploration",
                    skill = "空间方向感",
                    minAge = 14
                )
            )
            activityDao.insertAll(presetActivities)
        }
    }
}
