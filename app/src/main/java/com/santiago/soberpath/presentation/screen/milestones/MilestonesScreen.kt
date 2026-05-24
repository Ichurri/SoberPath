package com.santiago.soberpath.presentation.screen.milestones

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.santiago.soberpath.R

data class MilestoneUi(
    val title: String,
    val daysRequired: Int,
    val achieved: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MilestonesScreen(
    onBack: () -> Unit,
    milestones: List<MilestoneUi> = emptyList()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.milestones_title)) },
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
            if (milestones.isEmpty()) {
                Text(text = stringResource(R.string.milestones_empty))
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(milestones) { milestone ->
                        MilestoneItem(milestone)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun MilestoneItem(milestone: MilestoneUi) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val icon = if (milestone.achieved) {
            Icons.Default.CheckCircle
        } else {
            Icons.Default.RadioButtonUnchecked
        }
        Icon(imageVector = icon, contentDescription = null)
        Text(text = milestone.title)
        Text(text = stringResource(R.string.milestones_days_required, milestone.daysRequired))
    }
}


