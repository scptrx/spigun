package com.korbuts.spigun.ui.screens.home

import android.content.res.Configuration
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
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.korbuts.spigun.R
import com.korbuts.spigun.ui.common.SpigunHeader
import com.korbuts.spigun.ui.common.onClickSingle
import com.korbuts.spigun.ui.common.vibrate

@Composable
fun HomeScreen(
    onStartNewGame: () -> Unit,
    onManageGroups: () -> Unit,
    onBrowseTopics: () -> Unit
) {
    val startNewGameSingle = onStartNewGame.onClickSingle()
    val manageGroupsSingle = onManageGroups.onClickSingle()
    val browseTopicsSingle = onBrowseTopics.onClickSingle()

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
                HomeHeader(modifier = Modifier.weight(1f))
                HomeButtons(
                    onStartNewGame = startNewGameSingle,
                    onManageGroups = manageGroupsSingle,
                    onBrowseTopics = browseTopicsSingle,
                    modifier = Modifier.weight(1f),
                    buttonWidthFraction = 1f,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top,
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    HomeHeader()
                    Spacer(modifier = Modifier.height(48.dp))
                }
                item {
                    HomeButtons(
                        onStartNewGame = startNewGameSingle,
                        onManageGroups = manageGroupsSingle,
                        onBrowseTopics = browseTopicsSingle,
                        buttonWidthFraction = 0.6f
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(modifier: Modifier = Modifier) {
    SpigunHeader(
        title = stringResource(R.string.home_title),
        description = stringResource(R.string.home_description),
        modifier = modifier.padding(0.dp)
    )
}

@Composable
private fun HomeButtons(
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
        HomeButton(
            text = stringResource(R.string.home_new_game),
            onClick = onStartNewGame,
            widthFraction = buttonWidthFraction
        )

        Spacer(modifier = Modifier.height(16.dp))

        HomeButton(
            text = stringResource(R.string.home_my_groups),
            onClick = onManageGroups,
            widthFraction = buttonWidthFraction
        )

        Spacer(modifier = Modifier.height(16.dp))

        HomeButton(
            text = stringResource(R.string.home_topics),
            onClick = onBrowseTopics,
            widthFraction = buttonWidthFraction
        )
    }
}

@Composable
private fun HomeButton(
    text: String,
    onClick: () -> Unit,
    widthFraction: Float
) {
    val view = androidx.compose.ui.platform.LocalView.current
    Button(
        onClick = {
            view.vibrate()
            onClick()
        },
        modifier = Modifier
            .fillMaxWidth(widthFraction)
            .height(64.dp),
    ) {
        Text(
            text = text,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start
        )
    }
}
