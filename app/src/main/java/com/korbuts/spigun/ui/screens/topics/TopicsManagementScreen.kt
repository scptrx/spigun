package com.korbuts.spigun.ui.screens.topics

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.korbuts.spigun.R
import com.korbuts.spigun.data.model.TopicsPack
import com.korbuts.spigun.ui.common.SpigunCard
import com.korbuts.spigun.ui.common.SpigunHeader
import com.korbuts.spigun.ui.common.SpigunSectionHeader
import com.korbuts.spigun.ui.common.vibrate
import com.korbuts.spigun.ui.theme.SpigunTheme
import kotlinx.parcelize.Parcelize

sealed class TopicDialog : Parcelable {
    @Parcelize object AddPack : TopicDialog()
    @Parcelize data class EditPack(val pack: TopicsPack) : TopicDialog()
    @Parcelize data class DeletePack(val pack: TopicsPack) : TopicDialog()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicsManagementScreen(
    onBack: () -> Unit,
    viewModel: TopicsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var activeDialog by rememberSaveable { mutableStateOf<TopicDialog?>(null) }
    val view = LocalView.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.topics_title)) },
                navigationIcon = {
                    IconButton(onClick = {
                        view.vibrate()
                        onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    view.vibrate()
                    activeDialog = TopicDialog.AddPack
                },
                containerColor = SpigunTheme.colors.primary,
                contentColor = SpigunTheme.colors.onPrimary,
                shape = RoundedCornerShape(24.dp),
                icon = { Icon(Icons.Default.Add, contentDescription = null, tint = SpigunTheme.colors.onPrimary) },
                text = { Text(stringResource(R.string.topics_add_pack), fontWeight = FontWeight.Bold, color = SpigunTheme.colors.onPrimary) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SpigunHeader(
                title = stringResource(R.string.topics_header_title),
                description = stringResource(R.string.topics_header_description)
            )

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text(stringResource(R.string.topics_search_placeholder)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                if (uiState.customPacks.isNotEmpty()) {
                    item {
                        SpigunSectionHeader(title = stringResource(R.string.topics_custom_packs), icon = Icons.Default.Category)
                    }
                    items(uiState.customPacks) { pack ->
                        TopicPackItem(
                            pack = pack,
                            onEdit = { activeDialog = TopicDialog.EditPack(it) },
                            onDelete = { activeDialog = TopicDialog.DeletePack(it) }
                        )
                    }
                }

                item {
                    SpigunSectionHeader(title = stringResource(R.string.topics_default_packs), icon = Icons.Default.AutoAwesome)
                }

                items(uiState.defaultPacks) { pack ->
                    TopicPackItem(
                        pack = pack,
                        onEdit = { activeDialog = TopicDialog.EditPack(it) },
                        onDelete = { activeDialog = TopicDialog.DeletePack(it) }
                    )
                }
            }
        }
    }

    when (val dialog = activeDialog) {
        is TopicDialog.AddPack -> {
            AddEditTopicPackDialog(
                onDismiss = { activeDialog = null },
                onConfirm = { name, words ->
                    viewModel.addTopicPack(name, words)
                    activeDialog = null
                }
            )
        }
        is TopicDialog.EditPack -> {
            AddEditTopicPackDialog(
                pack = dialog.pack,
                onDismiss = { activeDialog = null },
                onConfirm = { name, words ->
                    viewModel.editTopicPack(dialog.pack.copy(name = name, words = words))
                    activeDialog = null
                }
            )
        }
        is TopicDialog.DeletePack -> {
            DeleteTopicConfirmationDialog(
                pack = dialog.pack,
                onDismiss = { activeDialog = null },
                onConfirm = {
                    viewModel.deleteTopicPack(dialog.pack.id)
                    activeDialog = null
                }
            )
        }
        null -> {}
    }
}

@Composable
fun TopicPackItem(
    pack: TopicsPack,
    onEdit: (TopicsPack) -> Unit,
    onDelete: (TopicsPack) -> Unit
) {
    val view = LocalView.current
    SpigunCard(
        colors = CardDefaults.cardColors(
            containerColor = if (pack.isCustom) SpigunTheme.colors.secondaryContainer else SpigunTheme.colors.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pack.name,
                    style = SpigunTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.topics_word_count, pack.words.size),
                    style = SpigunTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            IconButton(onClick = {
                view.vibrate()
                onEdit(pack)
            }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(20.dp))
            }
            if (pack.isCustom) {
                IconButton(onClick = {
                    view.vibrate()
                    onDelete(pack)
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun AddEditTopicPackDialog(
    pack: TopicsPack? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, List<String>) -> Unit
) {
    val view = LocalView.current
    var name by rememberSaveable { mutableStateOf(pack?.name ?: "") }
    var wordsString by rememberSaveable { mutableStateOf(pack?.words?.joinToString(", ") ?: "") }
    
    val wordsList = wordsString.split(",").map { it.trim() }.filter { it.isNotBlank() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(if (pack == null) R.string.dialog_add_pack_title else R.string.dialog_edit_pack_title)) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.dialog_pack_name_label)) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = wordsString,
                    onValueChange = { wordsString = it },
                    label = { Text(stringResource(R.string.dialog_pack_words_label)) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                if (wordsList.size < 5 && wordsString.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.dialog_pack_min_words_error),
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
                    if (name.isNotBlank() && wordsList.size >= 5) {
                        view.vibrate()
                        onConfirm(name, wordsList)
                    }
                },
                enabled = name.isNotBlank() && wordsList.size >= 5
            ) {
                Text(stringResource(if (pack == null) R.string.dialog_confirm_add else R.string.dialog_confirm_save))
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
fun DeleteTopicConfirmationDialog(
    pack: TopicsPack,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val view = LocalView.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_delete_pack_title)) },
        text = { Text(stringResource(R.string.dialog_delete_pack_text, pack.name)) },
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
