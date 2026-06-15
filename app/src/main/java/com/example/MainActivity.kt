package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.outlined.Casino
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.ExploreScreen
import com.example.ui.FavoritesScreen
import com.example.ui.ProfileOnboardingScreen
import com.example.ui.RouletteScreen
import com.example.ui.StatsProfileScreen
import com.example.ui.VibeViewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PrimaryTeal
import com.example.ui.theme.SecondaryCoral

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppEntry()
            }
        }
    }
}

@Composable
fun MainAppEntry() {
    val viewModel: VibeViewModel = viewModel()
    val userProfile by viewModel.userProfile.collectAsState()

    if (userProfile == null) {
        // Force User Registration Onboarding
        ProfileOnboardingScreen(
            onRegister = { email, nickname ->
                viewModel.registerProfile(email, nickname)
            }
        )
    } else {
        val email = userProfile?.email ?: ""
        val nickname = userProfile?.nickname ?: ""

        // Complete App Shell with Bottom Navigation Bar
        var selectedTab by remember { mutableIntStateOf(0) }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("bottom_nav_bar")
                ) {
                    // TAB 1: Roulette
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 0) Icons.Filled.Casino else Icons.Outlined.Casino,
                                contentDescription = "Roulette"
                            )
                        },
                        label = { Text("转盘") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SecondaryCoral,
                            indicatorColor = SecondaryCoral.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_tab_roulette")
                    )

                    // TAB 2: Explore Plaza
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 1) Icons.Filled.Widgets else Icons.Outlined.Widgets,
                                contentDescription = "Explore"
                            )
                        },
                        label = { Text("广场") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryTeal,
                            indicatorColor = PrimaryTeal.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_tab_explore")
                    )

                    // TAB 3: Favorites Chest
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 2) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorites"
                            )
                        },
                        label = { Text("收藏") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SecondaryCoral,
                            indicatorColor = SecondaryCoral.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_tab_favorites")
                    )

                    // TAB 4: Stats Hub & Profile
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 3) Icons.Filled.Person else Icons.Outlined.Person,
                                contentDescription = "Profile"
                            )
                        },
                        label = { Text("主页") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryTeal,
                            indicatorColor = PrimaryTeal.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_tab_profile")
                    )
                }
            }
        ) { innerPadding ->
            Crossfade(
                targetState = selectedTab,
                modifier = Modifier.padding(innerPadding),
                label = "MainCrossfadeNavigation"
            ) { tab ->
                when (tab) {
                    0 -> RouletteScreen(viewModel = viewModel)
                    1 -> ExploreScreen(viewModel = viewModel)
                    2 -> FavoritesScreen(viewModel = viewModel)
                    3 -> StatsProfileScreen(viewModel = viewModel, email = email, nickname = nickname, onChangeTab = { selectedTab = it })
                }
            }
        }
    }
}
