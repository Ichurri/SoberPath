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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.santiago.soberpath.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyCheckInScreen(
    onBack: () -> Unit
) {
    var mood by rememberSaveable { mutableStateOf("") }
    var cravingLevel by rememberSaveable { mutableStateOf(3f) }
    var note by rememberSaveable { mutableStateOf("") }
    var pledgeChecked by rememberSaveable { mutableStateOf(false) }

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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = mood,
                onValueChange = { mood = it },
                label = { Text(stringResource(R.string.daily_check_in_mood_label)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = stringResource(R.string.daily_check_in_craving_label))
            Slider(
                value = cravingLevel,
                onValueChange = { cravingLevel = it },
                valueRange = 1f..5f,
                steps = 3
            )
            Text(text = stringResource(R.string.daily_check_in_craving_value, cravingLevel.toInt()))
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text(stringResource(R.string.daily_check_in_note_label)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = pledgeChecked, onCheckedChange = { pledgeChecked = it })
                Text(text = stringResource(R.string.daily_check_in_pledge_label))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = { /* Placeholder for save */ }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.daily_check_in_save_button))
            }
        }
    }
}


