package com.santiago.soberpath.presentation.screen.recoverysetup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.santiago.soberpath.R
import com.santiago.soberpath.presentation.util.asString
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecoverySetupScreen(
    onNavigateHome: () -> Unit,
    viewModel: RecoverySetupViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                RecoverySetupContract.UiEffect.NavigateHome -> onNavigateHome()

                is RecoverySetupContract.UiEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message.asString(context))
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.recovery_setup_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.Center
            ) {
                RecoverySetupHeroCard()

                Spacer(modifier = Modifier.height(18.dp))

                RecoverySetupFormCard(
                    habitName = state.habitName,
                    dailyCost = state.dailyCost,
                    currency = state.currency,
                    isSaving = state.isSaving,
                    nameError = state.nameError?.asString(context),
                    costError = state.costError?.asString(context),
                    onHabitNameChanged = {
                        viewModel.onIntent(
                            RecoverySetupContract.UiIntent.HabitNameChanged(it)
                        )
                    },
                    onDailyCostChanged = {
                        viewModel.onIntent(
                            RecoverySetupContract.UiIntent.DailyCostChanged(it)
                        )
                    },
                    onCurrencyChanged = {
                        viewModel.onIntent(
                            RecoverySetupContract.UiIntent.CurrencyChanged(it)
                        )
                    },
                    onSaveClicked = {
                        viewModel.onIntent(
                            RecoverySetupContract.UiIntent.SaveClicked
                        )
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun RecoverySetupHeroCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.13f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp)
        ) {
            Text(
                text = stringResource(R.string.recovery_setup_header),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.recovery_setup_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
            )
        }
    }
}

@Composable
private fun RecoverySetupFormCard(
    habitName: String,
    dailyCost: String,
    currency: String,
    isSaving: Boolean,
    nameError: String?,
    costError: String?,
    onHabitNameChanged: (String) -> Unit,
    onDailyCostChanged: (String) -> Unit,
    onCurrencyChanged: (String) -> Unit,
    onSaveClicked: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.recovery_setup_habit_name_label),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = habitName,
                onValueChange = onHabitNameChanged,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(stringResource(R.string.recovery_setup_habit_name_placeholder))
                },
                isError = nameError != null,
                supportingText = {
                    nameError?.let {
                        Text(text = it)
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                colors = recoveryTextFieldColors()
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = dailyCost,
                onValueChange = onDailyCostChanged,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(stringResource(R.string.recovery_setup_daily_cost_label))
                },
                placeholder = {
                    Text(stringResource(R.string.recovery_setup_daily_cost_placeholder))
                },
                isError = costError != null,
                supportingText = {
                    costError?.let {
                        Text(text = it)
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                colors = recoveryTextFieldColors()
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = currency,
                onValueChange = onCurrencyChanged,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(stringResource(R.string.recovery_setup_currency_label))
                },
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                colors = recoveryTextFieldColors()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onSaveClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !isSaving,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.45f),
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 3.dp,
                    pressedElevation = 0.dp
                )
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = stringResource(R.string.recovery_setup_save_button),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun recoveryTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.22f),
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.60f),
    cursorColor = MaterialTheme.colorScheme.primary,
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    errorBorderColor = MaterialTheme.colorScheme.tertiary,
    errorLabelColor = MaterialTheme.colorScheme.tertiary,
    errorCursorColor = MaterialTheme.colorScheme.tertiary
)