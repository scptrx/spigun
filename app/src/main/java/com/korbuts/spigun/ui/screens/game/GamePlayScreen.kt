package com.korbuts.spigun.ui.screens.game

import android.content.res.Configuration
import android.view.HapticFeedbackConstants
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.korbuts.spigun.R
import com.korbuts.spigun.data.model.PlayerRole
import com.korbuts.spigun.ui.common.RevealHintContent
import com.korbuts.spigun.ui.common.RevealedContent
import com.korbuts.spigun.ui.common.onClickSingle
import com.korbuts.spigun.ui.theme.SpigunTheme
import com.korbuts.spigun.ui.common.vibrate

@Composable
fun GamePlayScreen(
    onStartDiscussion: () -> Unit,
    viewModel: GamePlayViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val view = LocalView.current
    
    BackHandler {

    }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (!uiState.isGameInitialized) return

    val currentPlayer = uiState.players[uiState.currentPlayerIndex]
    val isLastPlayer = viewModel.isLastPlayer()
    
    val onStartDiscussionSingle = onStartDiscussion.onClickSingle()

    var lockedRole by remember { mutableStateOf(PlayerRole.PLAYER) }
    var lockedTopic by remember { mutableStateOf("") }
    var lockedCategory by remember { mutableStateOf("") }
    
    if (uiState.isRevealed) {
        lockedRole = uiState.playerRoles[currentPlayer.id] ?: PlayerRole.PLAYER
        lockedTopic = uiState.assignedTopic
        lockedCategory = uiState.assignedCategory
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
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.game_pass_to_player),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    PlayerIdentity(name = currentPlayer.name)
                }

                LazyColumn(
                    modifier = Modifier.weight(1.2f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    item {
                        RevealCard(
                            isRevealed = uiState.isRevealed,
                            role = lockedRole,
                            topic = lockedTopic,
                            category = lockedCategory,
                            spyKnowsCategory = uiState.spyKnowsCategory,
                            onToggle = {
                                view.vibrate(HapticFeedbackConstants.LONG_PRESS)
                                viewModel.toggleReveal()
                            },
                            modifier = Modifier.height(180.dp)
                        )
                    }
                    item {
                        GameProgress(
                            currentIndex = uiState.currentPlayerIndex,
                            totalCount = uiState.players.size
                        )
                    }
                    item {
                        NavigationButton(
                            isLastPlayer = isLastPlayer,
                            isEnabled = uiState.isRevealed,
                            onClick = {
                                view.vibrate(HapticFeedbackConstants.LONG_PRESS)
                                if (isLastPlayer) onStartDiscussionSingle() else viewModel.nextPlayer()
                            }
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
                verticalArrangement = Arrangement.spacedBy(32.dp),
                contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp)
            ) {
                item {
                    Text(
                        text = stringResource(R.string.game_pass_to_player),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    PlayerIdentity(name = currentPlayer.name)
                }

                item {
                    RevealCard(
                        isRevealed = uiState.isRevealed,
                        role = lockedRole,
                        topic = lockedTopic,
                        category = lockedCategory,
                        spyKnowsCategory = uiState.spyKnowsCategory,
                        onToggle = {
                            view.vibrate(HapticFeedbackConstants.LONG_PRESS)
                            viewModel.toggleReveal()
                        }
                    )
                }

                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(48.dp)
                    ) {
                        GameProgress(
                            currentIndex = uiState.currentPlayerIndex,
                            totalCount = uiState.players.size
                        )

                        NavigationButton(
                            isLastPlayer = isLastPlayer,
                            isEnabled = uiState.isRevealed,
                            onClick = {
                                view.vibrate(HapticFeedbackConstants.LONG_PRESS)
                                if (isLastPlayer) onStartDiscussionSingle() else viewModel.nextPlayer()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerIdentity(name: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(SpigunTheme.colors.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun GameProgress(currentIndex: Int, totalCount: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.game_player_progress, currentIndex + 1, totalCount),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.spacedBy(if (totalCount > 10) 4.dp else 8.dp)
        ) {
            repeat(totalCount) { index ->
                val alpha = if (index <= currentIndex) 1f else 0.3f
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White.copy(alpha = alpha))
                )
            }
        }
    }
}

@Composable
private fun NavigationButton(
    isLastPlayer: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        shape = RoundedCornerShape(16.dp),
        enabled = isEnabled,
        colors = if (isLastPlayer) {
            androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
        } else {
            androidx.compose.material3.ButtonDefaults.buttonColors()
        }
    ) {
        Text(
            text = if (isLastPlayer) {
                stringResource(R.string.game_start_discussion)
            } else {
                stringResource(R.string.game_next_player)
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun RevealCard(
    isRevealed: Boolean,
    role: PlayerRole,
    topic: String,
    category: String,
    spyKnowsCategory: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(24.dp)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(shape)
            .clickable { onToggle() },
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = SpigunTheme.colors.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Crossfade(targetState = isRevealed, label = "RevealAnimation") { revealed ->
                if (!revealed) {
                    RevealHintContent()
                } else {
                    RevealedContent(
                        role = role,
                        topic = topic,
                        category = category,
                        spyKnowsCategory = spyKnowsCategory
                    )
                }
            }
        }
    }
}
