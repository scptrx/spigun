package com.korbuts.spigun.ui.common

import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.korbuts.spigun.R
import com.korbuts.spigun.ui.theme.SpigunTheme

fun View.vibrate(constant: Int = HapticFeedbackConstants.KEYBOARD_TAP) {
    performHapticFeedback(constant)
}

@Composable
fun SpigunHeader(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = title,
            style = SpigunTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = description,
            color = SpigunTheme.colors.gray,
            fontWeight = FontWeight.Bold,
            style = SpigunTheme.typography.bodyMedium
        )
    }
}

@Composable
fun SpigunSectionHeader(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = title,
            style = SpigunTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
fun SpigunCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    cornerRadius: Dp = 16.dp,
    colors: CardColors = CardDefaults.cardColors(containerColor = SpigunTheme.colors.surfaceVariant),
    content: @Composable () -> Unit,
) {
    val view = LocalView.current
    val shape = RoundedCornerShape(cornerRadius)

    val clickableModifier = remember(onClick, shape, view) {
        if (onClick != null) {
            Modifier.clip(shape).clickable {
                view.vibrate()
                onClick.invoke()
            }
        } else {
            Modifier
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .then(clickableModifier),
        shape = shape,
        colors = colors
    ) {
        content()
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = stringResource(R.string.players_search_placeholder),
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        singleLine = true,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun SpigunFloatingButton(
    label: String,
    onClick: () -> Unit
) {
    val view = LocalView.current
    ExtendedFloatingActionButton(
        onClick = {
            view.vibrate()
            onClick()
        },
        containerColor = SpigunTheme.colors.primary,
        contentColor = SpigunTheme.colors.onPrimary,
        shape = RoundedCornerShape(24.dp),
        icon = { Icon(Icons.Default.Add, contentDescription = null, tint = SpigunTheme.colors.onPrimary) },
        text = { Text(label, fontWeight = FontWeight.Bold, color = SpigunTheme.colors.onPrimary) }
    )
}