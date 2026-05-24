package com.santiago.soberpath.presentation.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.santiago.soberpath.R
import com.santiago.soberpath.presentation.util.asString
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onDailyCheckIn: () -> Unit,
    onMotivation: () -> Unit,
    onMilestones: () -> Unit,
    onSettings: () -> Unit,
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
                is HomeContract.UiEffect.ShowMessage ->
                    snackbarHostState.showSnackbar(effect.message.asString(context))
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.home_title)) }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (!state.hasHabit) {
                Text(text = stringResource(R.string.home_empty_title), style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(R.string.home_empty_description))
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.home_time_since_last_relapse),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (state.timeSinceRelapse.isBlank()) {
                                stringResource(R.string.home_time_placeholder)
                            } else {
                                state.timeSinceRelapse
                            },
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.home_savings_label),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (state.savingsText.isBlank()) {
                                stringResource(R.string.home_savings_placeholder)
                            } else {
                                state.savingsText
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (state.motivationalMessage.isBlank()) {
                        stringResource(R.string.home_motivational_message)
                    } else {
                        state.motivationalMessage
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = { viewModel.onIntent(HomeContract.UiIntent.DailyCheckInClicked) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.home_daily_check_in))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { viewModel.onIntent(HomeContract.UiIntent.RegisterRelapseClicked) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.home_register_relapse))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.onIntent(HomeContract.UiIntent.MotivationClicked) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.home_go_to_motivation))
                }
                Button(
                    onClick = { viewModel.onIntent(HomeContract.UiIntent.MilestonesClicked) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.home_go_to_milestones))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { viewModel.onIntent(HomeContract.UiIntent.SettingsClicked) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.home_go_to_settings))
            }
        }
    }
}
