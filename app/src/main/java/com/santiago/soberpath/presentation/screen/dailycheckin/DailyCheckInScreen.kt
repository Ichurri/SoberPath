package com.santiago.soberpath.presentation.screen.dailycheckin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
fun DailyCheckInScreen(
    onBack: () -> Unit,
    viewModel: DailyCheckInViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                DailyCheckInContract.UiEffect.NavigateBack -> onBack()
                is DailyCheckInContract.UiEffect.ShowMessage ->
                    snackbarHostState.showSnackbar(effect.message.asString(context))
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.daily_check_in_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = state.mood,
                onValueChange = { viewModel.onIntent(DailyCheckInContract.UiIntent.UpdateMood(it)) },
                label = { Text(stringResource(R.string.daily_check_in_mood_label)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = stringResource(R.string.daily_check_in_craving_label))
            Slider(
                value = state.cravingLevel.toFloat(),
                onValueChange = { viewModel.onIntent(DailyCheckInContract.UiIntent.UpdateCraving(it.toInt())) },
                valueRange = 1f..5f,
                steps = 3
            )
            Text(text = stringResource(R.string.daily_check_in_craving_value, state.cravingLevel))
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = state.note,
                onValueChange = { viewModel.onIntent(DailyCheckInContract.UiIntent.UpdateNote(it)) },
                label = { Text(stringResource(R.string.daily_check_in_note_label)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = state.pledgeChecked,
                    onCheckedChange = { viewModel.onIntent(DailyCheckInContract.UiIntent.UpdatePledge(it)) }
                )
                Text(text = stringResource(R.string.daily_check_in_pledge_label))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { viewModel.onIntent(DailyCheckInContract.UiIntent.Save) },
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.daily_check_in_save_button))
            }
        }
    }
}


