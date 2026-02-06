package com.ecolix.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.text.input.ImeAction

@Composable
fun CardContainer(
    containerColor: Color = MaterialTheme.colorScheme.surface,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            content()
        }
    }
}

@Composable
fun GradientIconBox(
    colors: List<Color>,
    icon: ImageVector,
    size: Dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.linearGradient(colors)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(size / 2)
        )
    }
}

@Composable
fun StatusPill(text: String, colors: List<Color>) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Brush.horizontalGradient(colors))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

@Composable
fun IconButtonCard(
    icon: ImageVector,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(containerColor)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor
        )
    }
}

@Composable
fun TagPill(label: String, color: Color, isSmall: Boolean = false) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(color.copy(alpha = 0.14f))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(999.dp))
            .padding(
                horizontal = if (isSmall) 6.dp else 10.dp, 
                vertical = if (isSmall) 2.dp else 4.dp
            )
    ) {
        Text(
            text = label,
            color = color,
            style = if (isSmall) 
                MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp)
            else 
                MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

@Composable
fun SearchableDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    options: List<String>,
    onSelect: (String) -> Unit,
    colors: com.ecolix.data.models.DashboardColors
) {
    var search by androidx.compose.runtime.remember(expanded) { androidx.compose.runtime.mutableStateOf("") }
    val filtered = options.filter { it.contains(search, ignoreCase = true) }

    androidx.compose.material3.DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier.background(colors.card).width(200.dp)
    ) {
        androidx.compose.material3.OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            placeholder = { Text("Rechercher...", fontSize = 12.sp) },
            modifier = Modifier.padding(8.dp).fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, null, modifier = Modifier.size(16.dp)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedTextColor = colors.textPrimary,
                unfocusedTextColor = colors.textPrimary,
                unfocusedBorderColor = colors.divider
            )
        )
        filtered.forEach { option ->
            androidx.compose.material3.DropdownMenuItem(
                text = { Text(option, color = colors.textPrimary, fontSize = 14.sp) },
                onClick = { onSelect(option) },
                colors = androidx.compose.material3.MenuDefaults.itemColors(textColor = colors.textPrimary)
            )
        }
        if (filtered.isEmpty()) {
            androidx.compose.material3.DropdownMenuItem(
                text = { Text("Aucun résultat", color = colors.textMuted, fontSize = 14.sp) },
                onClick = { }
            )
        }
    }
}

@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    colors: com.ecolix.data.models.DashboardColors
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.card,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = colors.textPrimary, fontWeight = FontWeight.Bold)
                androidx.compose.material3.IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, null, tint = colors.textMuted)
                }
            }
        },
        text = { Text(message, color = colors.textPrimary) },
        confirmButton = {
            androidx.compose.material3.Button(
                onClick = onConfirm,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error, contentColor = Color.White),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text("Supprimer")
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text("Annuler", color = colors.textMuted)
            }
        }
    )
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    colors: com.ecolix.data.models.DashboardColors,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Rechercher...", fontSize = 14.sp) },
        leadingIcon = { Icon(Icons.Default.Search, null, tint = colors.textMuted) },
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
            focusedTextColor = colors.textPrimary,
            unfocusedTextColor = colors.textPrimary,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = colors.divider.copy(alpha = if (colors.textPrimary == Color(0xFF1E293B)) 0.8f else 0.5f),
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = colors.textMuted,
            focusedPlaceholderColor = colors.textMuted,
            unfocusedPlaceholderColor = colors.textMuted.copy(alpha = 0.7f)
        )
    )
}
@Composable
fun ActionButton(
    icon: ImageVector,
    label: String,
    bg: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    border: Boolean = false,
    colors: com.ecolix.data.models.DashboardColors? = null,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .then(if (border && colors != null) Modifier.border(1.dp, colors.divider, RoundedCornerShape(12.dp)) else Modifier)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = contentColor)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, color = contentColor, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), maxLines = 1)
    }
}

@Composable
fun SectionHeader(title: String, colors: com.ecolix.data.models.DashboardColors) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.2.sp
            ),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(colors.divider.copy(alpha = 0.5f)))
    }
}

@Composable
fun FormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    colors: com.ecolix.data.models.DashboardColors,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    icon: ImageVector? = null,
    readOnly: Boolean = false,
    isError: Boolean = false,
    error: String? = null,
    lines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onIconClick: (() -> Unit)? = null
) {
    val finalIsError = isError || error != null
    
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = { if (placeholder.isNotEmpty()) Text(placeholder) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = readOnly,
            isError = finalIsError,
            maxLines = lines,
            minLines = lines,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            trailingIcon = icon?.let { 
                { 
                   IconButton(onClick = { onIconClick?.invoke() }, enabled = onIconClick != null) {
                       Icon(it, contentDescription = null, modifier = Modifier.size(18.dp), tint = if (finalIsError) MaterialTheme.colorScheme.error else colors.textMuted) 
                   }
                } 
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = colors.textPrimary,
                unfocusedTextColor = colors.textPrimary,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = colors.divider,
                errorBorderColor = MaterialTheme.colorScheme.error
            )
        )
        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp)
            )
        }
    }
}
