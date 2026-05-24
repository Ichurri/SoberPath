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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.santiago.soberpath.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onDailyCheckIn: () -> Unit,
    onRegisterRelapse: () -> Unit,
    onMotivation: () -> Unit,
    onMilestones: () -> Unit,
    onSettings: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.home_title)) }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.home_time_since_last_relapse),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.home_time_placeholder),
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
                    Text(text = stringResource(R.string.home_savings_placeholder))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = stringResource(R.string.home_motivational_message))
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onDailyCheckIn, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.home_daily_check_in))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onRegisterRelapse, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.home_register_relapse))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = onMotivation, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.home_go_to_motivation))
                }
                Button(onClick = onMilestones, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.home_go_to_milestones))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onSettings, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.home_go_to_settings))
            }
        }
    }
}
