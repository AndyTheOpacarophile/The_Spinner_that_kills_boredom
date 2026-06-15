package com.example.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.ActivityEntity
import com.example.data.ActivityLogEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

// ---------------------------------------------------------------------
// ONBOARDING / USER REGISTRATION SCREEN (注册制度)
// ---------------------------------------------------------------------
@Composable
fun ProfileOnboardingScreen(
    onRegister: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(CosmicDarkBackground, CosmicDarkSurface)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(24.dp))
                .testTag("onboarding_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicDarkSurface.copy(alpha = 0.95f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Cool brand logo header icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(PrimaryTeal.copy(alpha = 0.15f), CircleShape)
                        .border(1.dp, PrimaryTeal, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Casino,
                        contentDescription = "Casino Logo",
                        tint = PrimaryTeal,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "VibeSettle 闲停",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "建立您的专属闲适档案，开始治愈无聊",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Nickname Field
                OutlinedTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    label = { Text("昵称 (Nickname)", color = Color.White.copy(alpha = 0.6f)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = PrimaryTeal,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_nickname"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Email Field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("注册邮箱 (Email)", color = Color.White.copy(alpha = 0.6f)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = PrimaryTeal,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_email"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                if (isError) {
                    Text(
                        text = errorMessage,
                        color = CrimsonAccent,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        textAlign = TextAlign.Start
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        val trimmedEmail = email.trim()
                        val trimmedNickname = nickname.trim()
                        if (trimmedNickname.isEmpty()) {
                            isError = true
                            errorMessage = "请输入昵称！"
                        } else if (trimmedEmail.isEmpty() || !trimmedEmail.contains("@")) {
                            isError = true
                            errorMessage = "请输入有效的电子邮箱地址！"
                        } else {
                            isError = false
                            onRegister(trimmedEmail, trimmedNickname)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("onboarding_submit_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryTeal,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "开启大门 (Enter)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}


// ---------------------------------------------------------------------
// SCREEN 1: ROULETTE SCREEN (转盘/抽签)
// ---------------------------------------------------------------------
@Composable
fun RouletteScreen(
    viewModel: VibeViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allActivities by viewModel.allActivities.collectAsState()
    val matches = remember(allActivities, viewModel.selectedCategories, viewModel.selectedCostFilter, viewModel.selectedSocialFilter) {
        viewModel.getActiveActivities()
    }

    val rotationAngle by animateFloatAsState(
        targetValue = viewModel.targetRotationAngle,
        animationSpec = tween(
            durationMillis = 3000,
            easing = androidx.compose.animation.core.CubicBezierEasing(0.12f, 0.8f, 0.2f, 1.0f)
        )
    )

    // Result dialog handler
    var showResultDialog by remember { mutableStateOf(false) }
    var showAddFromRouletteDialog by remember { mutableStateOf(false) }
    val wheelState = viewModel.wheelState

    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
    val sliceDegrees = remember(matches.size) { 360f / matches.size.coerceAtLeast(1) }
    var lastTickIndex by remember { mutableStateOf(-1) }

    LaunchedEffect(rotationAngle) {
        if (matches.isNotEmpty()) {
            val currentTickIndex = ((rotationAngle + (sliceDegrees / 2f)) / sliceDegrees).toInt()
            if (lastTickIndex != -1 && currentTickIndex != lastTickIndex) {
                try {
                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                } catch (e: java.lang.Exception) {
                    // Ignore if haptic is not supported by environment
                }
            }
            lastTickIndex = currentTickIndex
        }
    }

    LaunchedEffect(wheelState) {
        if (wheelState is WheelState.Selected) {
            showResultDialog = true
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "VibeSettle 转盘",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "闲下来的时候，摇一个专属治愈行动吧",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sub-category Filter Tabs in a scrolling Row matching theme colors
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "选择能量方位 (Categories)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                androidx.compose.material3.TextButton(
                    onClick = { showAddFromRouletteDialog = true },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    colors = ButtonDefaults.textButtonColors(contentColor = PrimaryTeal)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Custom",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    val isAllCats = viewModel.selectedCategories.size == 4
                    Text(
                        text = if (isAllCats) "添砖加瓦" else "新增已选",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val cats = listOf("全部", "灵感漫游", "居室整活", "城市漂流", "肉身出逃")
                cats.forEach { cat ->
                    val isSelected = if (cat == "全部") {
                        viewModel.selectedCategories.size == 4
                    } else {
                        viewModel.selectedCategories.contains(cat)
                    }
                    val catColor = when (cat) {
                        "灵感漫游" -> ColorIndoorCalm
                        "居室整活" -> ColorIndoorActive
                        "城市漂流" -> ColorOutdoorCalm
                        "肉身出逃" -> ColorOutdoorActive
                        else -> PrimaryTeal
                    }
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.toggleCategoryFilter(cat) },
                        label = { Text(cat, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = catColor,
                            selectedLabelColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Secondary filters for Cost and Social
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Cost Filter Combobox Simulated via chips
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "成本负担 (Cost)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("全部", "免费", "付费").forEach { c ->
                            val active = viewModel.selectedCostFilter == c
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (active) SecondaryCoral else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { viewModel.selectedCostFilter = c }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = c,
                                    fontSize = 11.sp,
                                    color = if (active) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Social style Filter
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "陪伴模式 (Social)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("全部", "单人", "多人").forEach { s ->
                            val active = viewModel.selectedSocialFilter == s
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (active) PrimaryTeal else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { viewModel.selectedSocialFilter = s }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = s,
                                    fontSize = 11.sp,
                                    color = if (active) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Wheel visualization
            VibeRouletteWheel(
                activities = matches,
                rotationProvider = { rotationAngle },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Info state
            Text(
                text = "${matches.size} 个活动已装载入盘",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (matches.isEmpty()) CrimsonAccent else MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.spinWheel() },
                enabled = matches.isNotEmpty() && wheelState !is WheelState.Spinning,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(52.dp)
                    .testTag("spin_the_wheel_button"),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SecondaryCoral,
                    contentColor = Color.White
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.Refresh, contentDescription = "Spin")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (wheelState is WheelState.Spinning) "正在狂飙中..." else "命运转盘，启动！",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }

    // Modal Details overlay when landed
    if (showResultDialog && wheelState is WheelState.Selected) {
        val selectedAct = wheelState.activity
        
        Dialog(onDismissRequest = {
            showResultDialog = false
            viewModel.resetWheel()
        }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicDarkSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    val categoryColor = when (selectedAct.category) {
                        "灵感漫游" -> ColorIndoorCalm
                        "居室整活" -> ColorIndoorActive
                        "城市漂流" -> ColorOutdoorCalm
                        "肉身出逃" -> ColorOutdoorActive
                        else -> PrimaryTeal
                    }

                    // Sector / Category Badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(categoryColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                .border(1.dp, categoryColor, RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = selectedAct.category,
                                color = categoryColor,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = { viewModel.toggleFavorite(selectedAct) }) {
                            Icon(
                                imageVector = if (selectedAct.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (selectedAct.isFavorite) CrimsonAccent else Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = selectedAct.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Brief description
                    Text(
                        text = selectedAct.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                    Spacer(modifier = Modifier.height(12.dp))

                    // Grid tags for costs/suitability
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        VibeAttrBadge(
                            icon = Icons.Outlined.AccessTime,
                            label = selectedAct.duration,
                            color = Color.Cyan
                        )
                        VibeAttrBadge(
                            icon = Icons.Outlined.Payments,
                            label = selectedAct.cost,
                            color = Color.Yellow
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        VibeAttrBadge(
                            icon = Icons.Outlined.Psychology,
                            label = "精神: ${selectedAct.energyLevel}",
                            color = Color.Magenta
                        )
                        VibeAttrBadge(
                            icon = Icons.Outlined.Cake,
                            label = "${selectedAct.minAge}岁+",
                            color = Color.Green
                        )
                    }

                    if (selectedAct.skill != "无") {
                        Spacer(modifier = Modifier.height(8.dp))
                        VibeAttrBadge(
                            icon = Icons.Outlined.MilitaryTech,
                            label = "需求能力: ${selectedAct.skill}",
                            color = SecondaryCoral,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // If builtin link exists
                    if (selectedAct.link.isNotEmpty()) {
                        Button(
                            onClick = {
                                val openIntent = Intent(Intent.ACTION_VIEW, Uri.parse(selectedAct.link))
                                try {
                                    context.startActivity(openIntent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "无法打开网页链接，请检查浏览器配置！", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Filled.Language, contentDescription = "Link")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("内置直达网页链接 (网页游戏等)")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Logging mechanism
                    var durationLoggedMinutes by remember { mutableStateOf(15) }
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "你在上面花了多少时间？",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                "$durationLoggedMinutes 分钟",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                        Slider(
                            value = durationLoggedMinutes.toFloat(),
                            onValueChange = { durationLoggedMinutes = it.toInt() },
                            valueRange = 5f..120f,
                            steps = 23,
                            colors = SliderDefaults.colors(
                                activeTrackColor = SecondaryCoral,
                                thumbColor = SecondaryCoral
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                showResultDialog = false
                                viewModel.resetWheel()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
                            Text("放弃")
                        }

                        Button(
                            onClick = {
                                viewModel.logCompletion(
                                    activityId = selectedAct.id,
                                    title = selectedAct.title,
                                    category = selectedAct.category,
                                    durationMinutes = durationLoggedMinutes
                                )
                                showResultDialog = false
                                viewModel.resetWheel()
                                Toast.makeText(context, "已成功打卡记账！", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryCoral),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("打卡完成")
                        }
                    }
                }
            }
        }
    }

    if (showAddFromRouletteDialog) {
        AddActivityDialog(
            initialCategory = viewModel.selectedCategories.firstOrNull() ?: "灵感漫游",
            onDismiss = { showAddFromRouletteDialog = false },
            onSave = { title, category, duration, cost, social, energy, desc, link, tags, skill, age ->
                viewModel.addCustomActivity(
                    title = title,
                    category = category,
                    duration = duration,
                    cost = cost,
                    social = social,
                    energyLevel = energy,
                    description = desc,
                    link = link,
                    tags = tags,
                    skill = skill,
                    minAge = age
                )
                showAddFromRouletteDialog = false
                Toast.makeText(context, "自嗨活动 '$title' 已装载到 '$category' 板块中！", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun VibeAttrBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.06f), RoundedCornerShape(8.dp))
            .border(0.5.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.85f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}


// ---------------------------------------------------------------------
// SCREEN 2: ALL ACTIVITIES & CUSTOM ADD BASE (活动广场/收藏/自定义添加)
// ---------------------------------------------------------------------
@Composable
fun ExploreScreen(
    viewModel: VibeViewModel,
    modifier: Modifier = Modifier
) {
    val allActivities by viewModel.allActivities.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showAddFormDialog by remember { mutableStateOf(false) }

    val filteredList = remember(allActivities, searchQuery) {
        allActivities.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true) ||
                    it.category.contains(searchQuery)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddFormDialog = true },
                containerColor = SecondaryCoral,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_activity_fab_button")
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Custom")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = "活动广场",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "你可以自主浏览、收藏或新增自己的自嗨活动",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("搜索活动标题、标签...") },
                leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = "Search") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("explore_search_bar"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("explore_list")
            ) {
                items(filteredList) { activity ->
                    ActivityCard(
                        activity = activity,
                        onFavoriteToggle = { viewModel.toggleFavorite(activity) },
                        onDelete = {
                            if (activity.isCustom) {
                                viewModel.deleteCustomActivity(activity)
                            }
                        }
                    )
                }
            }
        }
    }

    if (showAddFormDialog) {
        AddActivityDialog(
            onDismiss = { showAddFormDialog = false },
            onSave = { title, category, duration, cost, social, energy, desc, link, tags, skill, age ->
                viewModel.addCustomActivity(
                    title = title,
                    category = category,
                    duration = duration,
                    cost = cost,
                    social = social,
                    energyLevel = energy,
                    description = desc,
                    link = link,
                    tags = tags,
                    skill = skill,
                    minAge = age
                )
                showAddFormDialog = false
            }
        )
    }
}

@Composable
fun ActivityCard(
    activity: ActivityEntity,
    onFavoriteToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColor = when (activity.category) {
        "灵感漫游" -> ColorIndoorCalm
        "居室整活" -> ColorIndoorActive
        "城市漂流" -> ColorOutdoorCalm
        "肉身出逃" -> ColorOutdoorActive
        else -> PrimaryTeal
    }

    val isDark = isSystemInDarkTheme()
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (isDark) CosmicDarkSurfaceVariant else categoryColor.copy(alpha = 0.25f),
                shape = RoundedCornerShape(24.dp)
            )
            .testTag("activity_card_${activity.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) CosmicDarkSurface else Color.White
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(categoryColor.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = activity.category,
                        color = categoryColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (activity.isCustom) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(SecondaryCoral.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "自定义",
                            color = SecondaryCoral,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = onFavoriteToggle,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (activity.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (activity.isFavorite) CrimsonAccent else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }

                if (activity.isCustom) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete",
                            tint = CrimsonAccent.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = activity.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Description
            Text(
                text = activity.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Attrs Badge row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AttrChip(icon = Icons.Outlined.AccessTime, label = activity.duration)
                AttrChip(icon = Icons.Outlined.Payments, label = activity.cost)
                AttrChip(icon = Icons.Outlined.Person, label = activity.social)
            }
        }
    }
}

@Composable
fun AttrChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun AddActivityDialog(
    initialCategory: String = "灵感漫游",
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String, String, String, String, String, Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(if (initialCategory == "全部") "灵感漫游" else initialCategory) }
    var duration by remember { mutableStateOf("15-30 min") }
    var cost by remember { mutableStateOf("Free") }
    var social by remember { mutableStateOf("Solo") }
    var energy by remember { mutableStateOf("Medium") }
    var link by remember { mutableStateOf("") }
    var skill by remember { mutableStateOf("无") }
    var minAge by remember { mutableStateOf("12") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .border(2.dp, PrimaryTeal.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicDarkSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Text(
                    text = "新建自嗨项",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("活动名称 (Title)", color = Color.White.copy(alpha = 0.6f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = PrimaryTeal,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Description
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("描述 (Description)", color = Color.White.copy(alpha = 0.6f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = PrimaryTeal,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Category selector
                Text("板块选择", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("灵感漫游", "居室整活", "城市漂流", "肉身出逃").forEach { cat ->
                        val active = category == cat
                        val catColor = when (cat) {
                            "灵感漫游" -> ColorIndoorCalm
                            "居室整活" -> ColorIndoorActive
                            "城市漂流" -> ColorOutdoorCalm
                            "肉身出逃" -> ColorOutdoorActive
                            else -> PrimaryTeal
                        }
                        Box(
                            modifier = Modifier
                                .border(
                                    1.dp,
                                    if (active) catColor else Color.White.copy(alpha = 0.2f),
                                    RoundedCornerShape(8.dp)
                                )
                                .background(if (active) catColor.copy(alpha = 0.15f) else Color.Transparent)
                                .clickable { category = cat }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(cat, color = if (active) catColor else Color.White, fontSize = 12.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                // Duration & Social & Cost
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = duration,
                        onValueChange = { duration = it },
                        label = { Text("时长 (Duration)", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = cost,
                        onValueChange = { cost = it },
                        label = { Text("成本 (Cost)", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = social,
                        onValueChange = { social = it },
                        label = { Text("模式 (Social)", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = energy,
                        onValueChange = { energy = it },
                        label = { Text("精力 (Energy)", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Skill Requirement & Age Limit
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = skill,
                        onValueChange = { skill = it },
                        label = { Text("所需门槛/技术 (Skill)", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.weight(1.3f)
                    )
                    OutlinedTextField(
                        value = minAge,
                        onValueChange = { minAge = it },
                        label = { Text("适应最低年龄", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Direct Link
                OutlinedTextField(
                    value = link,
                    onValueChange = { link = it },
                    label = { Text("内置超链接 / 游戏直达网址 (Link)", color = Color.White.copy(alpha = 0.6f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = PrimaryTeal,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                    ),
                    placeholder = { Text("https://...", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Text("取消")
                    }
                    Button(
                        onClick = {
                            if (title.trim().isNotEmpty()) {
                                onSave(
                                    title.trim(),
                                    category,
                                    duration.trim(),
                                    cost.trim(),
                                    social.trim(),
                                    energy,
                                    desc.trim(),
                                    link.trim(),
                                    "",
                                    skill.trim(),
                                    minAge.toIntOrNull() ?: 12
                                )
                            }
                        },
                        enabled = title.trim().isNotEmpty(),
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryCoral)
                    ) {
                        Text("保存")
                    }
                }
            }
        }
    }
}


// ---------------------------------------------------------------------
// SCREEN 3: FAVORITES SCREEN (收藏喜欢的活动)
// ---------------------------------------------------------------------
@Composable
fun FavoritesScreen(
    viewModel: VibeViewModel,
    modifier: Modifier = Modifier
) {
    val favActivities by viewModel.favoriteActivities.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = "心中的白月光",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "这些是你收藏的、随叫随到的心头好活动",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (favActivities.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("favorites_empty_state"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "暂未收藏任何活动！",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "你可以去活动广场或是用转盘打卡一些活动并添加喜欢",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(start = 32.dp, end = 32.dp, top = 4.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("favorites_list")
                ) {
                    items(favActivities) { activity ->
                        ActivityCard(
                            activity = activity,
                            onFavoriteToggle = { viewModel.toggleFavorite(activity) },
                            onDelete = {}
                        )
                    }
                }
            }
        }
    }
}


// ---------------------------------------------------------------------
// SCREEN 4: STATS & PROFILE & GOVERNANCE SIM (主页 - 记录与达人系统)
// ---------------------------------------------------------------------
@Composable
fun StatsProfileScreen(
    viewModel: VibeViewModel,
    email: String,
    nickname: String,
    onChangeTab: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val allLogs by viewModel.allLogs.collectAsState()
    val totalMins by viewModel.totalMinutes.collectAsState()
    val totalCount by viewModel.totalCount.collectAsState()
    val favoriteActivities by viewModel.favoriteActivities.collectAsState()
    val isDark = isSystemInDarkTheme()

    // Aggregate values for colors progress bar chart
    val (ic, ia, oc, oa) = remember(allLogs) {
        var indoorCalm = 0
        var indoorActive = 0
        var outdoorCalm = 0
        var outdoorActive = 0
        allLogs.forEach { log ->
            when (log.category) {
                "灵感漫游" -> indoorCalm++
                "居室整活" -> indoorActive++
                "城市漂流" -> outdoorCalm++
                "肉身出逃" -> outdoorActive++
            }
        }
        val sum = indoorCalm + indoorActive + outdoorCalm + outdoorActive
        if (sum == 0) {
            // Give uniform sample weight initially for visualization
            listOf(1, 1, 1, 1)
        } else {
            listOf(indoorCalm, indoorActive, outdoorCalm, outdoorActive)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Bento Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "VIBE CODING MENTOR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) BentoPurpleBorder else BentoPurpleAccent,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                    Text(
                        text = "Hi, $nickname 👋",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                // Account Avatar circle acting as logout trigger
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (isDark) CosmicDarkSurfaceVariant else BentoPurpleBG)
                        .clickable { viewModel.logoutProfile() }
                        .testTag("logout_profile_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = nickname.take(2).uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (isDark) Color.White else BentoPurpleAccent
                    )
                }
            }

            // BENTO GRID SPATIAL ARRANGEMENT

            // 1. LUCKY DRAW CARD: Decide for me right now (Landed roulette pivot)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .clickable { onChangeTab(0) }
                    .border(
                        width = 1.dp,
                        color = if (isDark) CosmicDarkSurfaceVariant else BentoBlueBorder,
                        shape = RoundedCornerShape(32.dp)
                    ),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) CosmicDarkSurface else BentoBlueBG
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(if (isDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.5f), CircleShape)
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "LUCKY DRAW",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.Cyan else BentoPurpleAccent
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "面对闲适选择困难？\n立刻让命运转盘做决定",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 30.sp,
                        color = if (isDark) Color.White else BentoTextDark,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "轻点卡片即刻抽取",
                            fontSize = 13.sp,
                            color = if (isDark) Color.White.copy(alpha = 0.6f) else BentoTextDark.copy(alpha = 0.6f)
                        )
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(BentoTextDark, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Spin the vibe wheel",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // 2. MIDDLE TWO GRID CELLS (FAVES & ACTIVE TIME)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Cell A: Faves (心中的白月光)
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(32.dp))
                        .clickable { onChangeTab(2) }
                        .border(
                            width = 1.dp,
                            color = if (isDark) CosmicDarkSurfaceVariant else BentoPurpleBorder,
                            shape = RoundedCornerShape(32.dp)
                        ),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) CosmicDarkSurface else BentoPurpleBG
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("✨", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "FAVES",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White.copy(alpha = 0.5f) else BentoTextDark.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${favoriteActivities.size} 项收藏",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else BentoTextDark
                        )
                    }
                }

                // Cell B: Active Time (累计时长)
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(32.dp))
                        .border(
                            width = 1.dp,
                            color = if (isDark) CosmicDarkSurfaceVariant else BentoGreenBorder,
                            shape = RoundedCornerShape(32.dp)
                        ),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) CosmicDarkSurface else BentoGreenBG
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("⏱️", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "ACTIVE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White.copy(alpha = 0.5f) else BentoTextDark.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        val formattedHours = if (totalMins >= 60) "${totalMins / 60}h ${totalMins % 60}m" else "${totalMins}m"
                        Text(
                            text = formattedHours,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else BentoTextDark
                        )
                    }
                }
            }

            // 3. SECOND ROW (CATEGORIES MAP & WORKSPACE INSIGHTS)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Cell C: Categories Pie/Bar (配比)
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(32.dp))
                        .border(
                            width = 1.dp,
                            color = if (isDark) CosmicDarkSurfaceVariant else BentoRedBorder,
                            shape = RoundedCornerShape(32.dp)
                        ),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) CosmicDarkSurface else BentoRedBG
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "CATEGORIES",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White.copy(alpha = 0.5f) else BentoTextDark.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Small aesthetic composition
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                "文武动静配比",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else BentoTextDark
                            )

                            VibeCompletionProgressBar(
                                indoorCalm = ic,
                                indoorActive = ia,
                                outdoorCalm = oc,
                                outdoorActive = oa,
                                modifier = Modifier.padding(vertical = 4.dp).height(12.dp)
                            )
                        }
                    }
                }

                // Cell D: Governance & Join community (达人自治)
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(32.dp))
                        .border(
                            width = 1.dp,
                            color = if (isDark) CosmicDarkSurfaceVariant else BentoLavenderBorder,
                            shape = RoundedCornerShape(32.dp)
                        ),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) CosmicDarkSurface else BentoLavenderBG
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy((-8).dp)
                        ) {
                            Box(modifier = Modifier.size(24.dp).background(Color.Gray, CircleShape).border(1.5.dp, Color.White, CircleShape))
                            Box(modifier = Modifier.size(24.dp).background(Color.LightGray, CircleShape).border(1.5.dp, Color.White, CircleShape))
                            Box(modifier = Modifier.size(24.dp).background(PrimaryTeal, CircleShape).border(1.5.dp, Color.White, CircleShape))
                        }
                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "加入 84 名身边的闲适达人，共建自嗨广场。",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 14.sp,
                            color = if (isDark) Color.White.copy(alpha = 0.8f) else Color(0xFF49454F)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BentoPurpleAccent, RoundedCornerShape(12.dp))
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("加入自治", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 4. LATEST IN-PLACE ACTIVITY BANNER (RECENT ACTIVITY DARK CARD)
            val latestLog = allLogs.firstOrNull()
            if (latestLog != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(32.dp))
                        .border(
                            width = 1.dp,
                            color = if (isDark) PrimaryTeal.copy(alpha = 0.3f) else Color.Black,
                            shape = RoundedCornerShape(32.dp)
                        ),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) CosmicDarkSurfaceVariant else BentoDarkBG
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFFFFD8E4), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🎵", fontSize = 18.sp)
                            }
                            Column {
                                Text(
                                    text = latestLog.activityTitle,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "最近一次打卡 • ${latestLog.durationMinutes} 分钟",
                                    color = Color(0xFF938F99),
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .background(Color.Transparent)
                                .border(1.dp, Color(0xFFD0BCFF), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "REPLAY",
                                color = Color(0xFFD0BCFF),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Mock self-govern trigger Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (isDark) CosmicDarkSurfaceVariant else BentoLavenderBorder,
                        RoundedCornerShape(24.dp)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.People,
                            contentDescription = "Community",
                            tint = BentoPurpleAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "达人线下自治圈 (Showcase)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "以下由注册超50次的「星级达人」发起的线下自治自嗨活动，展现你的主场：",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )

                    MockGovernanceEvent(
                        title = "【室外文】寻找城市绝美落日追光Citywalk",
                        master = "小红书追光手 / Andy",
                        stars = 5,
                        joinedCount = "18/30人",
                        vibePoints = "+15 VibePoints"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    MockGovernanceEvent(
                        title = "【室内武】极速反应力擂台赛 2048对黑棋",
                        master = "2048天王 / Lily",
                        stars = 4,
                        joinedCount = "8/10人",
                        vibePoints = "+20 VibePoints"
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // History Logs Titles
            Text(
                text = "打卡履历 (Logs History)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (allLogs.isEmpty()) {
                Text(
                    text = "暂无打卡数据，快去使用转盘抽取活动完成一次吧！",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                allLogs.forEach { log ->
                    LogHistoryItem(
                        log = log,
                        onDeleteClick = { viewModel.deleteLog(log.id) }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
fun MockGovernanceEvent(
    title: String,
    master: String,
    stars: Int,
    joinedCount: String,
    vibePoints: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f), RoundedCornerShape(10.dp))
            .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("发起达人: $master", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(6.dp))
                repeat(stars) {
                    Icon(imageVector = Icons.Filled.Star, contentDescription = null, tint = Color.Yellow, modifier = Modifier.size(10.dp))
                }
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Box(
                modifier = Modifier
                    .background(PrimaryTeal.copy(alpha = 0.12f), CircleShape)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(joinedCount, fontSize = 9.sp, color = PrimaryTeal, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(vibePoints, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SecondaryCoral)
        }
    }
}

@Composable
fun LegendLabel(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun LogHistoryItem(
    log: ActivityLogEntity,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColor = when (log.category) {
        "灵感漫游" -> ColorIndoorCalm
        "居室整活" -> ColorIndoorActive
        "城市漂流" -> ColorOutdoorCalm
        "肉身出逃" -> ColorOutdoorActive
        else -> PrimaryTeal
    }

    val dateStr = remember(log.timestamp) {
        val sdf = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
        sdf.format(Date(log.timestamp))
    }

    val isDark = isSystemInDarkTheme()
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (isDark) CosmicDarkSurfaceVariant else categoryColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp)
            )
            .testTag("log_item_${log.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDark) CosmicDarkSurface else Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(36.dp)
                    .background(categoryColor, RoundedCornerShape(2.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = log.activityTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        text = log.category,
                        fontSize = 10.sp,
                        color = categoryColor,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "完成时间: $dateStr",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = "${log.durationMinutes} min",
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                color = SecondaryCoral
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.DeleteOutline,
                    contentDescription = "Delete log",
                    tint = CrimsonAccent.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
