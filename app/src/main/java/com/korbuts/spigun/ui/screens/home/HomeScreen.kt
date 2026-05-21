package com.korbuts.spigun.ui.screens.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onStartNewGame: () -> Unit,
    onManageGroups: () -> Unit,
    onBrowseTopics: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(24.dp)
    ) {
        if (isLandscape) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Header(modifier = Modifier.weight(1f))
                Buttons(
                    onStartNewGame = onStartNewGame,
                    onManageGroups = onManageGroups,
                    onBrowseTopics = onBrowseTopics,
                    modifier = Modifier.weight(1f),
                    buttonWidthFraction = 1f
                )
            }
        } else {
            Header(modifier = Modifier.align(Alignment.TopStart))
            Buttons(
                onStartNewGame = onStartNewGame,
                onManageGroups = onManageGroups,
                onBrowseTopics = onBrowseTopics,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                buttonWidthFraction = 0.5f
            )
        }
    }
}

@Composable
private fun Header(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "spigun",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "show players their role, then let the group find who doesn't know the topic.",
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun Buttons(
    onStartNewGame: () -> Unit,
    onManageGroups: () -> Unit,
    onBrowseTopics: () -> Unit,
    modifier: Modifier = Modifier,
    buttonWidthFraction: Float
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onStartNewGame,
            modifier = Modifier
                .fillMaxWidth(buttonWidthFraction)
                .height(64.dp)
        ) {
            Text(
                text = "new game",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onManageGroups,
            modifier = Modifier
                .fillMaxWidth(buttonWidthFraction)
                .height(64.dp)
        ) {
            Text(
                text = "my player groups",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBrowseTopics,
            modifier = Modifier
                .fillMaxWidth(buttonWidthFraction)
                .height(64.dp)
        ) {
            Text(
                text = "topics",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start
            )
        }
    }
}
