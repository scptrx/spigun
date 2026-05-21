package com.korbuts.spigun.ui.screens.players

import android.os.Parcelable
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import com.korbuts.spigun.ui.theme.SpigunTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.korbuts.spigun.R
import com.korbuts.spigun.data.model.Player
import com.korbuts.spigun.data.model.PlayerGroup
import com.korbuts.spigun.ui.common.SpigunCard
import com.korbuts.spigun.ui.common.SpigunHeader
import com.korbuts.spigun.ui.common.SpigunSectionHeader
import com.korbuts.spigun.ui.common.vibrate
import kotlinx.parcelize.Parcelize

sealed class PlayerDialog : Parcelable {
    @Parcelize object AddPlayer : PlayerDialog()
    @Parcelize object CreateGroup : PlayerDialog()
    @Parcelize data class EditPlayer(val player: Player) : PlayerDialog()
    @Parcelize data class EditGroup(val group: PlayerGroup) : PlayerDialog()
    @Parcelize data class DeletePlayer(val player: Player) : PlayerDialog()
    @Parcelize data class DeleteGroup(val group: PlayerGroup) : PlayerDialog()
    @Parcelize data class PlayerWarning(val groups: List<PlayerGroup>) : PlayerDialog()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerManagementScreen(
    onBack: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var activeDialog by rememberSaveable { mutableStateOf<PlayerDialog?>(null) }

    val view = LocalView.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.players_title)) },
                navigationIcon = {
                    IconButton(onClick = {
                        view.vibrate()
                        onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.selectedPlayerIds.size >= 3) {
                        Button(
                            onClick = { 
                                view.vibrate()
                                activeDialog = PlayerDialog.CreateGroup
                            },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(stringResource(R.string.players_save_group))
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { 
                    view.vibrate()
                    activeDialog = PlayerDialog.AddPlayer
                },
                containerColor = SpigunTheme.colors.primary,
                contentColor = SpigunTheme.colors.onPrimary,
                shape = RoundedCornerShape(24.dp),
                icon = { Icon(Icons.Default.Add, contentDescription = null, tint = SpigunTheme.colors.onPrimary) },
                text = { Text(stringResource(R.string.players_add_player), fontWeight = FontWeight.Bold, color = SpigunTheme.colors.onPrimary) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SpigunHeader(
                title = stringResource(R.string.players_header_title),
                description = stringResource(R.string.players_header_description)
            )

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text(stringResource(R.string.players_search_placeholder)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                if (uiState.groups.isNotEmpty()) {
                    item(key = "groups_header") {
                        SpigunSectionHeader(title = stringResource(R.string.players_saved_groups), icon = Icons.Default.Group)
                    }
                    items(
                        items = uiState.groups,
                        key = { "group_${it.id}" },
                        contentType = { "group" }
                    ) { group ->
                        GroupItem(
                            group = group,
                            onEdit = { activeDialog = PlayerDialog.EditGroup(it) },
                            onDelete = { activeDialog = PlayerDialog.DeleteGroup(it) }
                        )
                    }
                }

                item(key = "players_header") {
                    SpigunSectionHeader(title = stringResource(R.string.players_all_players), icon = Icons.Default.Person)
                }

                items(
                    items = uiState.players,
                    key = { "player_${it.id}" },
                    contentType = { "player" }
                ) { player ->
                    PlayerItem(
                        player = player,
                        isSelected = uiState.selectedPlayerIds.contains(player.id),
                        onToggle = { viewModel.togglePlayerSelection(player.id) },
                        onEdit = { activeDialog = PlayerDialog.EditPlayer(it) },
                        onDelete = {
                            val criticalGroups = viewModel.getCriticalGroupsForPlayer(player.id)
                            if (criticalGroups.isNotEmpty()) {
                                activeDialog = PlayerDialog.PlayerWarning(criticalGroups)
                            } else {
                                activeDialog = PlayerDialog.DeletePlayer(it)
                            }
                        }
                    )
                }
            }
        }
    }

    when (val dialog = activeDialog) {
        is PlayerDialog.AddPlayer -> {
            AddPlayerDialog(
                onDismiss = { activeDialog = null },
                onConfirm = { name ->
                    viewModel.addPlayer(name)
                    activeDialog = null
                }
            )
        }
        is PlayerDialog.CreateGroup -> {
            CreateGroupDialog(
                onDismiss = { activeDialog = null },
                onConfirm = { name ->
                    viewModel.createGroup(name)
                    activeDialog = null
                }
            )
        }
        is PlayerDialog.EditPlayer -> {
            EditPlayerDialog(
                player = dialog.player,
                onDismiss = { activeDialog = null },
                onConfirm = { updatedPlayer ->
                    viewModel.editPlayer(updatedPlayer)
                    activeDialog = null
                }
            )
        }
        is PlayerDialog.EditGroup -> {
            EditGroupDialog(
                group = dialog.group,
                onDismiss = { activeDialog = null },
                onConfirm = { updatedGroup ->
                    viewModel.editGroup(updatedGroup)
                    activeDialog = null
                }
            )
        }
        is PlayerDialog.DeletePlayer -> {
            DeleteConfirmationDialog(
                title = stringResource(R.string.dialog_delete_player_title),
                text = stringResource(R.string.dialog_delete_player_text, dialog.player.name),
                onDismiss = { activeDialog = null },
                onConfirm = {
                    viewModel.deletePlayer(dialog.player.id)
                    activeDialog = null
                }
            )
        }
        is PlayerDialog.DeleteGroup -> {
            DeleteConfirmationDialog(
                title = stringResource(R.string.dialog_delete_group_title),
                text = stringResource(R.string.dialog_delete_group_text, dialog.group.name),
                onDismiss = { activeDialog = null },
                onConfirm = {
                    viewModel.deleteGroup(dialog.group.id)
                    activeDialog = null
                }
            )
        }
        is PlayerDialog.PlayerWarning -> {
            DeletePlayerWarningDialog(
                groups = dialog.groups,
                onDismiss = { activeDialog = null }
            )
        }
        null -> {}
    }
}

@Composable
private fun PlayerItem(
    player: Player,
    isSelected: Boolean,
    onToggle: () -> Unit,
    onEdit: (Player) -> Unit,
    onDelete: (Player) -> Unit
) {
    val view = LocalView.current
    SpigunCard(
        onClick = onToggle,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SpigunTheme.colors.primaryContainer else SpigunTheme.colors.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (isSelected) SpigunTheme.colors.primary else Color.Gray
            )
            Spacer(modifier = Modifier.size(12.dp))
            Text(
                text = player.name,
                style = SpigunTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { 
                view.vibrate()
                onEdit(player) 
            }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(20.dp))
            }
            IconButton(onClick = { 
                view.vibrate()
                onDelete(player) 
            }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun GroupItem(
    group: PlayerGroup,
    onEdit: (PlayerGroup) -> Unit,
    onDelete: (PlayerGroup) -> Unit
) {
    val view = LocalView.current
    SpigunCard(
        colors = CardDefaults.cardColors(containerColor = SpigunTheme.colors.secondaryContainer)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = group.name,
                    style = SpigunTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = group.players.joinToString(", ") { it.name },
                    style = SpigunTheme.typography.bodySmall,
                    color = SpigunTheme.colors.onSecondaryContainer.copy(alpha = 0.7f)
                )
            }
            IconButton(onClick = { 
                view.vibrate()
                onEdit(group) 
            }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(20.dp))
            }
            IconButton(onClick = { 
                view.vibrate()
                onDelete(group) 
            }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun AddPlayerDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    val view = LocalView.current
    var name by rememberSaveable { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_add_player_title)) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.dialog_player_name_label)) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        },
        confirmButton = {
            Button(onClick = { 
                if (name.isNotBlank()) {
                    view.vibrate()
                    onConfirm(name)
                }
            }) {
                Text(stringResource(R.string.dialog_confirm_add))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                view.vibrate()
                onDismiss()
            }) {
                Text(stringResource(R.string.dialog_confirm_cancel))
            }
        }
    )
}

@Composable
private fun EditPlayerDialog(player: Player, onDismiss: () -> Unit, onConfirm: (Player) -> Unit) {
    val view = LocalView.current
    var name by rememberSaveable { mutableStateOf(player.name) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_edit_player_title)) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.dialog_player_name_label)) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        },
        confirmButton = {
            Button(onClick = { 
                if (name.isNotBlank()) {
                    view.vibrate()
                    onConfirm(player.copy(name = name))
                }
            }) {
                Text(stringResource(R.string.dialog_confirm_save))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                view.vibrate()
                onDismiss()
            }) {
                Text(stringResource(R.string.dialog_confirm_cancel))
            }
        }
    )
}

@Composable
private fun EditGroupDialog(group: PlayerGroup, onDismiss: () -> Unit, onConfirm: (PlayerGroup) -> Unit) {
    val view = LocalView.current
    var name by rememberSaveable { mutableStateOf(group.name) }
    var players by rememberSaveable { mutableStateOf(group.players) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_edit_group_title)) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.dialog_group_name_label)) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(stringResource(R.string.dialog_players_in_group_label), style = SpigunTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                    items(
                        items = players,
                        key = { it.id }
                    ) { player ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(player.name)
                            IconButton(onClick = { 
                                view.vibrate()
                                players = players.filter { it.id != player.id }
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
                if (players.size < 3) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.dialog_group_min_players_error),
                        color = SpigunTheme.colors.error,
                        style = SpigunTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    if (name.isNotBlank() && players.size >= 3) {
                        view.vibrate()
                        onConfirm(group.copy(name = name, players = players))
                    }
                },
                enabled = name.isNotBlank() && players.size >= 3
            ) {
                Text(stringResource(R.string.dialog_confirm_save))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                view.vibrate()
                onDismiss()
            }) {
                Text(stringResource(R.string.dialog_confirm_cancel))
            }
        }
    )
}

@Composable
private fun CreateGroupDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    val view = LocalView.current
    var name by rememberSaveable { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_save_group_title)) },
        text = {
            Column {
                Text(stringResource(R.string.dialog_save_group_hint))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.dialog_group_name_label)) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = { 
                view.vibrate()
                onConfirm(name) 
            }) {
                Text(stringResource(R.string.dialog_confirm_save))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                view.vibrate()
                onDismiss()
            }) {
                Text(stringResource(R.string.dialog_confirm_cancel))
            }
        }
    )
}

@Composable
private fun DeleteConfirmationDialog(
    title: String,
    text: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val view = LocalView.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = {
            Button(
                onClick = {
                    view.vibrate()
                    onConfirm()
                },
                colors = ButtonDefaults.buttonColors(containerColor = SpigunTheme.colors.error)
            ) {
                Text(stringResource(R.string.dialog_confirm_delete))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                view.vibrate()
                onDismiss()
            }) {
                Text(stringResource(R.string.dialog_confirm_cancel))
            }
        }
    )
}

@Composable
private fun DeletePlayerWarningDialog(
    groups: List<PlayerGroup>,
    onDismiss: () -> Unit
) {
    val view = LocalView.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_cannot_delete_player_title)) },
        text = {
            Column {
                Text(stringResource(R.string.dialog_cannot_delete_player_text))
                Spacer(modifier = Modifier.height(8.dp))
                groups.forEach { group ->
                    Text("• ${group.name}", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(R.string.dialog_cannot_delete_player_footer))
            }
        },
        confirmButton = {
            Button(onClick = {
                view.vibrate()
                onDismiss()
            }) {
                Text(stringResource(R.string.dialog_confirm_ok))
            }
        }
    )
}
