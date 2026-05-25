package com.korbuts.spigun.ui.screens.game

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.korbuts.spigun.R
import com.korbuts.spigun.ui.common.onClickSingle
import com.korbuts.spigun.ui.theme.SpigunTheme
import com.korbuts.spigun.ui.common.vibrate

@Composable
fun GameTimerScreen(
    onFinish: () -> Unit,
    viewModel: GamePlayViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val view = LocalView.current
    
    BackHandler {

    }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    
    val onFinishSingle = onFinish.onClickSingle()

    LaunchedEffect(Unit) {
        viewModel.startTimer()
    }

    Scaffold { padding ->
        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    TimerHeader()
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    item {
                        TimerDisplay(seconds = uiState.remainingTimeSeconds)
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                    item {
                        TimerControls(
                            isRunning = uiState.isTimerRunning,
                            isTimeUp = uiState.isTimeUp,
                            onToggle = {
                                view.vibrate()
                                viewModel.toggleTimer()
                            },
                            onEndEarly = {
                                view.vibrate()
                                viewModel.endRoundEarly()
                            },
                            onFinish = onFinishSingle
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(48.dp),
                contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp)
            ) {
                item {
                    TimerHeader()
                }

                item {
                    TimerDisplay(seconds = uiState.remainingTimeSeconds)
                }

                item {
                    TimerControls(
                        isRunning = uiState.isTimerRunning,
                        isTimeUp = uiState.isTimeUp,
                        onToggle = {
                            view.vibrate()
                            viewModel.toggleTimer()
                        },
                        onEndEarly = {
                            view.vibrate()
                            viewModel.endRoundEarly()
                        },
                        onFinish = onFinishSingle
                    )
                }
            }
        }
    }
}

@Composable
private fun TimerHeader() {
    Column {
        Text(
            text = stringResource(R.string.timer_header_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.timer_header_description),
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun TimerDisplay(seconds: Long) {
    val timeText = remember(seconds) {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        "%02d:%02d".format(minutes, remainingSeconds)
    }

    Text(
        text = timeText,
        style = MaterialTheme.typography.displayLarge.copy(
            fontSize = 100.sp,
            fontWeight = FontWeight.Black
        ),
        color = if (seconds <= 10) SpigunTheme.colors.error else Color.White
    )
}

@Composable
private fun TimerControls(
    isRunning: Boolean,
    isTimeUp: Boolean,
    onToggle: () -> Unit,
    onEndEarly: () -> Unit,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (isTimeUp) {
            Text(
                text = stringResource(R.string.timer_time_up),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = SpigunTheme.colors.error
            )
            
            Button(
                onClick = onFinish,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = stringResource(R.string.timer_finish_voting),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onToggle,
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = stringResource(if (isRunning) R.string.timer_pause else R.string.timer_resume),
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onEndEarly,
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SpigunTheme.colors.error)
                ) {
                    Icon(Icons.Default.Stop, contentDescription = null)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = stringResource(R.string.timer_end_now),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
