package com.santiago.soberpath.presentation.screen.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.santiago.soberpath.R
import com.santiago.soberpath.presentation.util.asString
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onNavigateHome: () -> Unit,
    viewModel: OnboardingViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                OnboardingContract.UiEffect.NavigateHome -> onNavigateHome()
                is OnboardingContract.UiEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message.asString(context))
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.onboarding_title))
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.onIntent(OnboardingContract.UiIntent.Skip)
                        }
                    ) {
                        Text(stringResource(R.string.onboarding_skip_button))
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { padding ->

        if (state.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(12.dp))
                Text(stringResource(R.string.onboarding_loading))
            }
            return@Scaffold
        }

        val slide = state.currentSlide

        if (slide == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.error_generic),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        viewModel.onIntent(OnboardingContract.UiIntent.Skip)
                    }
                ) {
                    Text(stringResource(R.string.onboarding_skip_button))
                }
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val progress = (state.currentIndex + 1).toFloat() / state.slides.size.toFloat()

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            AsyncImage(
                model = slide.imageUrl,
                contentDescription = stringResource(R.string.onboarding_image_description),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = slide.title,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = slide.description,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "${state.currentIndex + 1} / ${state.slides.size}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.onIntent(OnboardingContract.UiIntent.Previous)
                    },
                    enabled = !state.isFirstSlide,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.onboarding_previous_button))
                }

                Button(
                    onClick = {
                        if (state.isLastSlide) {
                            viewModel.onIntent(OnboardingContract.UiIntent.Start)
                        } else {
                            viewModel.onIntent(OnboardingContract.UiIntent.Next)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        if (state.isLastSlide) {
                            stringResource(R.string.onboarding_start_button)
                        } else {
                            stringResource(R.string.onboarding_next_button)
                        }
                    )
                }
            }
        }
    }
}