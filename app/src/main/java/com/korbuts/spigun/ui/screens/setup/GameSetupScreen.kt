package com.korbuts.spigun.ui.screens.setup

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Topic
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.korbuts.spigun.R
import com.korbuts.spigun.ui.common.SpigunAlertDialog
import com.korbuts.spigun.ui.common.SpigunCard
import com.korbuts.spigun.ui.common.SpigunDialogConfirmButton
import com.korbuts.spigun.ui.common.SpigunHeader
import com.korbuts.spigun.ui.common.SpigunSectionHeader
import com.korbuts.spigun.ui.common.onClickSingle
import com.korbuts.spigun.ui.common.vibrate
import com.korbuts.spigun.ui.theme.SpigunTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameSetupScreen(
    onBack: () -> Unit,
    onManageGroups: () -> Unit,
    onStartGame: () -> Unit,
    viewModel: GameSetupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showSurpriseInfo by remember { mutableStateOf(false) }
    val view = LocalView.current

    val selectedGroup = remember(uiState.groups, uiState.roundConfig.selectedGroupId) {
        uiState.groups.find { it.id == uiState.roundConfig.selectedGroupId }
    }
    val maxSpies = remember(selectedGroup) {
        (selectedGroup?.players?.size ?: 5).coerceAtLeast(1)
    }
    
    val onStartGameSingle = onStartGame.onClickSingle()
    val onBackSingle = onBack.onClickSingle()
    val onManageGroupsSingle = onManageGroups.onClickSingle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.setup_title)) },
                navigationIcon = {
                    IconButton(onClick = {
                        view.vibrate()
                        onBackSingle()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = SpigunTheme.colors.primary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                SpigunHeader(
                    title = stringResource(R.string.setup_header_title),
                    description = stringResource(R.string.setup_header_description)
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    item {
                        SpigunSectionHeader(title = stringResource(R.string.setup_group_label), icon = Icons.Default.Groups)
                        if (uiState.groups.isEmpty()) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                Text(
                                    text = stringResource(R.string.setup_no_groups_error),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SpigunTheme.colors.error,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                Button(
                                    onClick = {
                                        view.vibrate()
                                        onManageGroupsSingle()
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(stringResource(R.string.setup_create_group_button))
                                }
                            }
                        }
                    }

                    if (uiState.groups.isNotEmpty()) {
                        items(uiState.groups, key = { it.id }) { group ->
                            GroupSelectionItem(
                                name = group.name,
                                isSelected = uiState.roundConfig.selectedGroupId == group.id,
                                onSelect = {
                                    view.vibrate()
                                    viewModel.selectGroup(group.id)
                                }
                            )
                        }
                    }

                    item {
                        SpigunSectionHeader(title = stringResource(R.string.setup_duration_label), icon = Icons.Default.Timer)
                        Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Slider(
                                value = uiState.roundConfig.roundDurationMinutes.toFloat(),
                                onValueChange = {
                                    val newVal = it.toInt()
                                    if (newVal != uiState.roundConfig.roundDurationMinutes) {
                                        view.vibrate()
                                        viewModel.updateRoundDuration(newVal)
                                    }
                                },
                                valueRange = 1f..15f,
                                steps = 14,
                                modifier = Modifier.weight(1f)
                            )
                            Box(modifier = Modifier.width(48.dp), contentAlignment = Alignment.CenterEnd) {
                                Text(
                                    text = "${uiState.roundConfig.roundDurationMinutes}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    item {
                        SpigunSectionHeader(title = stringResource(R.string.setup_spies_label), icon = Icons.Default.RadioButtonUnchecked)
                        Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Slider(
                                value = uiState.roundConfig.spyCount.toFloat().coerceAtMost(maxSpies.toFloat()),
                                onValueChange = {
                                    val newVal = it.toInt().coerceAtMost(maxSpies)
                                    if (newVal != uiState.roundConfig.spyCount) {
                                        view.vibrate()
                                        viewModel.updateSpyCount(newVal)
                                    }
                                },
                                valueRange = 1f..maxSpies.toFloat(),
                                steps = if (maxSpies > 1) maxSpies - 2 else 0,
                                modifier = Modifier.weight(1f),
                                enabled = selectedGroup != null
                            )
                            Box(modifier = Modifier.width(48.dp), contentAlignment = Alignment.CenterEnd) {
                                Text(
                                    text = "${uiState.roundConfig.spyCount}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    item {
                        SpigunSectionHeader(title = stringResource(R.string.setup_topics_label), icon = Icons.Default.Topic)
                    }

                    items(uiState.topicPacks, key = { it.id }) { pack ->
                        TopicSelectionItem(
                            name = pack.name,
                            wordCount = pack.words.size,
                            isSelected = uiState.roundConfig.activeTopicsPackIds.contains(pack.id),
                            onToggle = {
                                view.vibrate()
                                viewModel.toggleTopicPack(pack.id)
                            }
                        )
                    }

                    item {
                        SpigunSectionHeader(title = stringResource(R.string.setup_modifiers_label), icon = Icons.Default.AutoAwesome)
                        Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            ModifierToggle(
                                title = stringResource(R.string.setup_spy_knows_category),
                                checked = uiState.roundConfig.spyKnowsCategory,
                                onCheckedChange = { viewModel.toggleSpyKnowsCategory() }
                            )
                            ModifierToggle(
                                title = stringResource(R.string.setup_surprise_game),
                                checked = uiState.roundConfig.surpriseGameEnabled,
                                onCheckedChange = { viewModel.toggleSurpriseGame() },
                                onInfoClick = { showSurpriseInfo = true }
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }

                Button(
                    onClick = {
                        view.vibrate()
                        onStartGameSingle()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(64.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = selectedGroup != null && uiState.roundConfig.activeTopicsPackIds.isNotEmpty()
                ) {
                    Text(
                        text = stringResource(R.string.setup_start_game),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    if (showSurpriseInfo) {
        SurpriseInfoDialog(onDismiss = { showSurpriseInfo = false })
    }
}

@Composable
fun GroupSelectionItem(name: String, isSelected: Boolean, onSelect: () -> Unit) {
    SpigunCard(
        onClick = onSelect,
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
                tint = if (isSelected) SpigunTheme.colors.primary else SpigunTheme.colors.gray
            )
            Spacer(modifier = Modifier.size(12.dp))
            Text(text = name, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun TopicSelectionItem(name: String, wordCount: Int, isSelected: Boolean, onToggle: () -> Unit) {
    SpigunCard(
        onClick = onToggle,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SpigunTheme.colors.secondaryContainer else SpigunTheme.colors.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (isSelected) SpigunTheme.colors.primary else SpigunTheme.colors.gray
            )
            Spacer(modifier = Modifier.size(12.dp))
            Column {
                Text(text = name, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = stringResource(R.string.topics_word_count, wordCount),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun ModifierToggle(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onInfoClick: (() -> Unit)? = null
) {
    val view = LocalView.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            if (onInfoClick != null) {
                IconButton(onClick = {
                    view.vibrate()
                    onInfoClick()
                }) {
                    Icon(Icons.Default.Info, contentDescription = "Info", modifier = Modifier.size(20.dp))
                }
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = { 
                view.vibrate()
                onCheckedChange(it) 
            }
        )
    }
}

@Composable
fun SurpriseInfoDialog(onDismiss: () -> Unit) {
    SpigunAlertDialog(
        onDismissRequest = onDismiss,
        title = stringResource(R.string.info_surprise_title),
        confirmButton = {
            SpigunDialogConfirmButton(
                text = stringResource(R.string.dialog_confirm_ok),
                onClick = onDismiss
            )
        }, dismissButton = { }
    ) { Text(stringResource(R.string.info_surprise_text)) }
}
