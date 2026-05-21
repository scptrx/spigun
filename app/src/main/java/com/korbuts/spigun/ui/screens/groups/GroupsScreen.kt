package com.korbuts.spigun.ui.screens.groups

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun GroupsScreen(
    onAddGroup: () -> Unit,
    onSelectGroup: (Long) -> Unit
) {
    Text(text = "Groups Screen")
}
