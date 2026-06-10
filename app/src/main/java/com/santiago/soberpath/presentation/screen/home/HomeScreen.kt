package com.santiago.soberpath.presentation.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.santiago.soberpath.R
import com.santiago.soberpath.presentation.util.asString
import com.santiago.soberpath.ui.theme.AquaGreenDark
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    onDailyCheckIn: () -> Unit,
    onMotivation: () -> Unit,
    onMilestones: () -> Unit,
    onSettings: () -> Unit,
    onRecoverySetup: () -> Unit,
    onRegisterRelapse: () -> Unit,
    onHabits: () -> Unit,
    onRelapseHistory: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                HomeContract.UiEffect.NavigateDailyCheckIn -> onDailyCheckIn()
                HomeContract.UiEffect.NavigateMotivation -> onMotivation()
                HomeContract.UiEffect.NavigateMilestones -> onMilestones()
                HomeContract.UiEffect.NavigateSettings -> onSettings()
                HomeContract.UiEffect.NavigateRecoverySetup -> onRecoverySetup()
                HomeContract.UiEffect.NavigateHabits -> onHabits()
                HomeContract.UiEffect.NavigateRegisterRelapse -> onRegisterRelapse()
                HomeContract.UiEffect.NavigateRelapseHistory -> onRelapseHistory()
                is HomeContract.UiEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message.asString(context))
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // Se eliminó el TopAppBar clásico para integrar un header personalizado dentro del scroll
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                        )
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Encabezado principal personalizado
            HomeHeader(
                onHabitsClicked = { viewModel.onIntent(HomeContract.UiIntent.HabitsClicked) },
                onSettingsClicked = { viewModel.onIntent(HomeContract.UiIntent.SettingsClicked) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (!state.hasHabit) {
                EmptyHomeState(
                    onCreateTracking = {
                        viewModel.onIntent(HomeContract.UiIntent.SetupRecoveryClicked)
                    }
                )
            } else {
                // Título del hábito activo
                Text(
                    text = state.habitName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Visualizador circular de sobriedad
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    SoberCircularProgress(timeSinceRelapse = state.timeSinceRelapse)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Rejilla de métricas principales
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeMetricCard(
                        title = stringResource(R.string.home_savings_label),
                        value = if (state.savingsText.isBlank()) {
                            stringResource(R.string.home_savings_placeholder)
                        } else {
                            state.savingsText
                        },
                        icon = Icons.Default.MonetizationOn,
                        iconColor = AquaGreenDark,
                        modifier = Modifier.weight(1f)
                    )

                    HomeMetricCard(
                        title = stringResource(R.string.home_relapse_history_title),
                        value = state.relapseCount.toString(),
                        subtitle = if (state.lastRelapseDate.isBlank()) {
                            stringResource(R.string.home_last_relapse_empty)
                        } else {
                            stringResource(
                                R.string.home_last_relapse,
                                state.lastRelapseDate
                            )
                        },
                        icon = Icons.Default.History,
                        iconColor = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.onIntent(HomeContract.UiIntent.RelapseHistoryClicked)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Banner dinámico de Check-in
                val todayString = java.time.LocalDate.now().toString()
                val checkedInToday = state.recentCheckIns.any { it.date == todayString }

                DynamicCheckInBanner(
                    checkedInToday = checkedInToday,
                    onDailyCheckIn = {
                        viewModel.onIntent(HomeContract.UiIntent.DailyCheckInClicked)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Mensaje Motivacional
                HomeMessageCard(
                    text = if (state.motivationalMessage.isBlank()) {
                        stringResource(R.string.home_motivational_message)
                    } else {
                        state.motivationalMessage
                    }
                )

                if (state.emergencyTipsEnabled && state.emergencyTipsMessage.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))

                    HomeSectionCard(
                        title = stringResource(R.string.home_emergency_tips_title)
                    ) {
                        Text(
                            text = state.emergencyTipsMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Check-ins recientes
                HomeSectionCard(
                    title = stringResource(R.string.home_checkins_title)
                ) {
                    if (state.recentCheckIns.isEmpty()) {
                        Text(
                            text = stringResource(R.string.home_checkins_empty),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                        )
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            state.recentCheckIns.forEach { checkIn ->
                                Text(
                                    text = stringResource(
                                        R.string.home_checkins_item,
                                        checkIn.date,
                                        checkIn.mood,
                                        checkIn.cravingLevel
                                    ),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Rejilla de acciones estilo dashboard
                DashboardGrid(
                    onMilestones = { viewModel.onIntent(HomeContract.UiIntent.MilestonesClicked) },
                    onMotivation = { viewModel.onIntent(HomeContract.UiIntent.MotivationClicked) },
                    onHabits = { viewModel.onIntent(HomeContract.UiIntent.HabitsClicked) },
                    onRegisterRelapse = { viewModel.onIntent(HomeContract.UiIntent.RegisterRelapseClicked) }
                )
            }
        }
    }
}

@Composable
private fun HomeHeader(
    onHabitsClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "SoberPath",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.home_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = onHabitsClicked,
                modifier = Modifier.background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(50)
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.FormatListBulleted,
                    contentDescription = stringResource(R.string.home_go_to_habits),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(
                onClick = onSettingsClicked,
                modifier = Modifier.background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(50)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.home_go_to_settings),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun SoberCircularProgress(
    timeSinceRelapse: String,
    modifier: Modifier = Modifier
) {
    // Parsear días, horas, minutos y segundos desde el string "Xd Xh Xm Xs" o "00d 00h 00m 00s"
    val parts = timeSinceRelapse.split(" ")
    val daysText = parts.getOrNull(0)?.replace("d", "")?.trim() ?: "0"
    val hoursText = parts.getOrNull(1)?.replace("h", "")?.trim() ?: "0"
    val minutesText = parts.getOrNull(2)?.replace("m", "")?.trim() ?: "0"
    val secondsText = parts.getOrNull(3)?.replace("s", "")?.trim() ?: "0"

    val daysInt = daysText.toIntOrNull() ?: 0

    // Cálculo dinámico de progreso hacia el siguiente logro
    val milestones = listOf(1, 3, 7, 14, 30, 60, 90)
    val nextMilestone = milestones.firstOrNull { it > daysInt } ?: 100
    val prevMilestone = milestones.lastOrNull { it <= daysInt } ?: 0
    val totalRange = nextMilestone - prevMilestone

    val milestoneProgress = if (totalRange > 0) {
        val daysProgress = (daysInt - prevMilestone).toFloat()
        val hoursInt = hoursText.toIntOrNull() ?: 0
        val fraction = hoursInt.toFloat() / 24f
        (daysProgress + fraction) / totalRange.toFloat()
    } else {
        1f
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(200.dp)
    ) {
        val primaryColor = MaterialTheme.colorScheme.primary
        val trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)

        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            // Anillo de fondo
            drawCircle(
                color = trackColor,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 14.dp.toPx())
            )

            // Arco activo
            drawArc(
                color = primaryColor,
                startAngle = -90f,
                sweepAngle = 360f * milestoneProgress.coerceIn(0f, 1f),
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = 14.dp.toPx(),
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = daysText,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = if (daysInt == 1) "DÍA LIBRE" else "DÍAS LIBRES",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${hoursText}h ${minutesText}m ${secondsText}s",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Meta: ${nextMilestone}d",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun EmptyHomeState(
    onCreateTracking: () -> Unit
) {
    HomeSectionCard(
        title = stringResource(R.string.home_empty_title),
        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
    ) {
        Text(
            text = stringResource(R.string.home_empty_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onCreateTracking,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = stringResource(R.string.home_empty_button),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeMetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null
) {
    Card(
        onClick = onClick ?: {},
        enabled = onClick != null,
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.60f)
                )
            }
        }
    }
}

@Composable
private fun DynamicCheckInBanner(
    checkedInToday: Boolean,
    onDailyCheckIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (checkedInToday) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
            } else {
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = if (checkedInToday) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                            } else {
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                            },
                            shape = RoundedCornerShape(50)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (checkedInToday) {
                            Icons.Default.CheckCircle
                        } else {
                            Icons.Default.Checklist
                        },
                        contentDescription = null,
                        tint = if (checkedInToday) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSecondaryContainer
                        }
                    )
                }
                Column {
                    Text(
                        text = if (checkedInToday) {
                            stringResource(R.string.home_checkin_ready_title)
                        } else {
                            stringResource(R.string.home_checkin_pending_title)
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (checkedInToday) {
                            stringResource(R.string.home_checkin_ready_description)
                        } else {
                            stringResource(R.string.home_checkin_pending_description)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                    )
                }
            }
            if (!checkedInToday) {
                Button(
                    onClick = onDailyCheckIn,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.home_checkin_register_button),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeMessageCard(
    text: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FormatQuote,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.TopStart)
            )
            Text(
                text = text,
                modifier = Modifier.padding(start = 24.dp, top = 8.dp),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.80f)
            )
        }
    }
}

@Composable
private fun HomeSectionCard(
    title: String,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            content()
        }
    }
}

@Composable
fun DashboardGrid(
    onMilestones: () -> Unit,
    onMotivation: () -> Unit,
    onHabits: () -> Unit,
    onRegisterRelapse: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardTile(
                title = stringResource(R.string.dashboard_milestones_title),
                description = stringResource(R.string.dashboard_milestones_description),
                icon = Icons.Default.EmojiEvents,
                iconColor = AquaGreenDark,
                containerColor = MaterialTheme.colorScheme.surface,
                onClick = onMilestones,
                modifier = Modifier.weight(1f)
            )
            DashboardTile(
                title = stringResource(R.string.dashboard_motivation_title),
                description = stringResource(R.string.dashboard_motivation_description),
                icon = Icons.Default.Favorite,
                iconColor = Color(0xFFE57373),
                containerColor = MaterialTheme.colorScheme.surface,
                onClick = onMotivation,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardTile(
                title = stringResource(R.string.dashboard_habits_title),
                description = stringResource(R.string.dashboard_habits_description),
                icon = Icons.AutoMirrored.Filled.FormatListBulleted,
                iconColor = MaterialTheme.colorScheme.primary,
                containerColor = MaterialTheme.colorScheme.surface,
                onClick = onHabits,
                modifier = Modifier.weight(1f)
            )
            DashboardTile(
                title = stringResource(R.string.dashboard_relapse_title),
                description = stringResource(R.string.dashboard_relapse_description),
                icon = Icons.Default.Warning,
                iconColor = MaterialTheme.colorScheme.error,
                containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.05f)
                    .compositeOver(MaterialTheme.colorScheme.surface),
                onClick = onRegisterRelapse,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun DashboardTile(
    title: String,
    description: String,
    icon: ImageVector,
    iconColor: Color,
    containerColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDefaultSurface = containerColor == MaterialTheme.colorScheme.surface
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDefaultSurface) 2.dp else 0.dp,
            pressedElevation = if (isDefaultSurface) 6.dp else 0.dp,
            focusedElevation = if (isDefaultSurface) 3.dp else 0.dp,
            hoveredElevation = if (isDefaultSurface) 3.dp else 0.dp,
            draggedElevation = if (isDefaultSurface) 4.dp else 0.dp,
            disabledElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = iconColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                )
            }
        }
    }
}
