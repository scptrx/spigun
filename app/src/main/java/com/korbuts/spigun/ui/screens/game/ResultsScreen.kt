package com.korbuts.spigun.ui.screens.game

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.korbuts.spigun.R
import com.korbuts.spigun.data.model.PlayerRole
import com.korbuts.spigun.ui.common.SpigunCard
import com.korbuts.spigun.ui.common.SpigunHeader
import com.korbuts.spigun.ui.common.onClickSingle
import com.korbuts.spigun.ui.common.vibrate
import com.korbuts.spigun.ui.theme.SpigunTheme

@Composable
fun ResultsScreen(
    onPlayAgain: () -> Unit,
    onBackHome: () -> Unit,
    viewModel: GamePlayViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val view = LocalView.current
    
    BackHandler {

    }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    
    val onPlayAgainSingle = onPlayAgain.onClickSingle()
    val onBackHomeSingle = onBackHome.onClickSingle()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            if (!uiState.isSecretRevealed) {
                VotingPhase(
                    uiState = uiState,
                    onSelectPlayer = { 
                        view.vibrate()
                        viewModel.toggleVotedPlayer(it) 
                    },
                    onReveal = {
                        view.vibrate()
                        viewModel.revealSecret()
                    }
                )
            } else {
                if (isLandscape) {
                    RevealPhaseLandscape(
                        uiState = uiState,
                        onPlayAgain = onPlayAgainSingle,
                        onBackHome = onBackHomeSingle
                    )
                } else {
                    RevealPhasePortrait(
                        uiState = uiState,
                        onPlayAgain = onPlayAgainSingle,
                        onBackHome = onBackHomeSingle
                    )
                }
            }
        }
    }
}

@Composable
private fun VotingPhase(
    uiState: GamePlayUiState,
    onSelectPlayer: (String) -> Unit,
    onReveal: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        SpigunHeader(
            title = stringResource(R.string.results_header_title),
            description = stringResource(R.string.results_header_description),
            modifier = Modifier.padding(0.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.players) { player ->
                val isSelected = uiState.votedPlayerIds.contains(player.id)
                SpigunCard(
                    onClick = { onSelectPlayer(player.id) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) SpigunTheme.colors.primaryContainer else SpigunTheme.colors.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (isSelected) SpigunTheme.colors.primary else Color.Gray
                        )
                        Spacer(modifier = Modifier.size(12.dp))
                        Text(text = player.name, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }

        Button(
            onClick = onReveal,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = uiState.votedPlayerIds.size == uiState.configuredSpyCount
        ) {
            Text(
                text = stringResource(R.string.results_reveal_button),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun RevealPhasePortrait(
    uiState: GamePlayUiState,
    onPlayAgain: () -> Unit,
    onBackHome: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RevealHeader(uiState)
            
            Spacer(modifier = Modifier.height(48.dp))
            
            RevealContent(uiState)
        }

        ActionButtons(onPlayAgain, onBackHome)
    }
}

@Composable
private fun RevealPhaseLandscape(
    uiState: GamePlayUiState,
    onPlayAgain: () -> Unit,
    onBackHome: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            RevealHeader(uiState)
        }

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                RevealContent(uiState)
            }
            
            ActionButtons(onPlayAgain, onBackHome)
        }
    }
}

@Composable
private fun RevealHeader(uiState: GamePlayUiState) {
    val allPickedAreSpies = uiState.votedPlayerIds.all { 
        uiState.playerRoles[it] == PlayerRole.SPY 
    }
    
    val victoryText = when {
        uiState.everyoneIsSpy -> stringResource(R.string.results_surprise_everyone_spy)
        uiState.noOneIsSpy -> stringResource(R.string.results_surprise_no_spy)
        allPickedAreSpies -> stringResource(R.string.results_victory_players)
        else -> stringResource(R.string.results_victory_spies)
    }
    val victoryColor = if (allPickedAreSpies || uiState.noOneIsSpy) SpigunTheme.colors.success else SpigunTheme.colors.error

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(tween(1000)) + slideInVertically(tween(1000)) { -40 }
    ) {
        Text(
            text = victoryText,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Black,
            color = victoryColor,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RevealContent(uiState: GamePlayUiState) {
    Text(
        text = stringResource(R.string.results_topic_was),
        style = MaterialTheme.typography.titleMedium,
        color = Color.Gray
    )
    Text(
        text = uiState.assignedTopic,
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.ExtraBold,
        textAlign = TextAlign.Center
    )
    Text(
        text = uiState.assignedCategory,
        style = MaterialTheme.typography.bodyMedium,
        color = Color.Gray
    )
    
    Spacer(modifier = Modifier.height(32.dp))
    HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
    Spacer(modifier = Modifier.height(32.dp))

    Text(
        text = stringResource(R.string.results_spies_were),
        style = MaterialTheme.typography.titleMedium,
        color = Color.Gray
    )
    Spacer(modifier = Modifier.height(16.dp))
    
    uiState.players.filter { uiState.playerRoles[it.id] == PlayerRole.SPY }.forEach { spy ->
        Text(
            text = spy.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = SpigunTheme.colors.error
        )
    }
}

@Composable
private fun ActionButtons(onPlayAgain: () -> Unit, onBackHome: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(
            onClick = onPlayAgain,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
        ) {
            Text(text = stringResource(R.string.results_play_again), fontWeight = FontWeight.Bold)
        }
        
        OutlinedButton(
            onClick = onBackHome,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(text = stringResource(R.string.results_back_home), fontWeight = FontWeight.Bold)
        }
    }
}
