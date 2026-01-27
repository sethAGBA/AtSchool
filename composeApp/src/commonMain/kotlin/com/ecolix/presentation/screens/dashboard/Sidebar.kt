package com.ecolix.presentation.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.ecolix.presentation.screens.auth.LoginScreen
import com.ecolix.presentation.theme.LoginDarkGradient
import com.ecolix.presentation.theme.LoginLightGradient

@Composable
fun Sidebar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit
) {
    var isCollapsed by remember { mutableStateOf(false) }
    val width by animateDpAsState(if (isCollapsed) 80.dp else 280.dp, tween(300))
    val navigator = LocalNavigator.currentOrThrow
    val contentColor = if (isDarkMode) Color.White else Color.Black
    val dividerColor = if (isDarkMode) Color.White.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.1f)

    Column(
        modifier = Modifier
            .width(width)
            .fillMaxSize()
            .background(
                if (isDarkMode) Brush.verticalGradient(LoginDarkGradient) else Brush.linearGradient(listOf(Color.White, Color.White))
            )
            .padding(vertical = 18.dp)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = if (isCollapsed) 8.dp else 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(if (isCollapsed) 48.dp else 62.dp)
                    .clip(CircleShape)
                    .background(contentColor.copy(alpha = if (isDarkMode) 0.18f else 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(if (isCollapsed) 24.dp else 34.dp)
                )
            }
            
            if (!isCollapsed) {
                Text(
                    text = "Ecole Manager",
                    color = contentColor,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Clip
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(contentColor.copy(alpha = if (isDarkMode) 0.2f else 0.1f))
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "SAFE MODE",
                        color = contentColor,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }

            IconButton(
                onClick = { isCollapsed = !isCollapsed },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(
                    imageVector = if (isCollapsed) Icons.AutoMirrored.Filled.KeyboardArrowRight else Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = if (isCollapsed) "Agrandir" else "Reduire",
                    tint = contentColor.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
        HorizontalDivider(color = dividerColor)
        
        // Theme Toggle
        if (!isCollapsed) {
             Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onToggleTheme() }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                 Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                        contentDescription = null,
                        tint = if (isDarkMode) contentColor else Color(0xFFFFC107)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = if (isDarkMode) "Mode Sombre" else "Mode Clair",
                        color = contentColor,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                    )
                }
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = { onToggleTheme() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = if (isDarkMode) Color.White else Color.Gray,
                        uncheckedTrackColor = if (isDarkMode) Color.White.copy(alpha = 0.3f) else Color.LightGray.copy(alpha = 0.5f)
                    )
                )
            }
        } else {
             IconButton(
                onClick = { onToggleTheme() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                    contentDescription = null,
                    tint = if (isDarkMode) contentColor else Color(0xFFFFC107)
                )
            }
        }

        HorizontalDivider(color = dividerColor)

        // Menu Items
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(getSidebarItems()) { item ->
                SidebarItemRow(
                    item = item,
                    selected = selectedIndex == item.index,
                    isCollapsed = isCollapsed,
                    contentColor = contentColor,
                    onClick = { onItemSelected(item.index) }
                )
            }
        }

        HorizontalDivider(color = dividerColor)
        Spacer(modifier = Modifier.height(12.dp))
        
        // Logout
        Box(
            modifier = Modifier
                .padding(horizontal = if (isCollapsed) 8.dp else 20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE11D48))
                .clickable { navigator.replaceAll(LoginScreen()) }
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isCollapsed) {
                 Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Se deconnecter",
                    tint = Color.White
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                     Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Se deconnecter",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SidebarItemRow(
    item: SidebarItemData, 
    selected: Boolean, 
    isCollapsed: Boolean,
    contentColor: Color,
    onClick: () -> Unit
) {
    val background = if (selected) contentColor.copy(alpha = 0.1f) else Color.Transparent
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = if (isCollapsed) 8.dp else 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = if (isCollapsed) 0.dp else 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isCollapsed) Arrangement.Center else Arrangement.Start
    ) {
         if (isCollapsed) {
              TooltipBox(
                tooltip = {
                    PlainTooltip {
                        Text(item.title)
                    }
                },
                state = rememberTooltipState(),
                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                    positioning = TooltipAnchorPosition.Above,
                    spacingBetweenTooltipAndAnchor = 10.dp
                )
            ) {
                 Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = if (selected) contentColor else contentColor.copy(alpha = 0.7f),
                    modifier = Modifier.size(24.dp)
                )
            }
         } else {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = if (selected) contentColor else contentColor.copy(alpha = 0.7f),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = item.title,
                color = if (selected) contentColor else contentColor.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 16.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
         }
    }
}

data class SidebarItemData(
    val index: Int,
    val title: String,
    val icon: ImageVector
)

private fun getSidebarItems(): List<SidebarItemData> = listOf(
    SidebarItemData(0, "Tableau de bord", Icons.Filled.Dashboard),
    SidebarItemData(1, "Eleves & Classes", Icons.Filled.People),
    SidebarItemData(2, "Personnel", Icons.Filled.Person),
    SidebarItemData(3, "Notes & Bulletins", Icons.Filled.Description),
    SidebarItemData(4, "Matieres", Icons.AutoMirrored.Filled.MenuBook),
    SidebarItemData(6, "Utilisateurs", Icons.Filled.AdminPanelSettings),
    SidebarItemData(7, "Emplois du Temps", Icons.Filled.CalendarMonth),
    SidebarItemData(8, "Gestion Académique", Icons.Filled.School),
    SidebarItemData(9, "Paiements", Icons.Filled.Payments),
    SidebarItemData(14, "Bibliotheque", Icons.AutoMirrored.Filled.LibraryBooks),
    SidebarItemData(15, "Discipline", Icons.Filled.Gavel),
    SidebarItemData(13, "Signatures & Cachets", Icons.Filled.AssignmentTurnedIn),
    SidebarItemData(10, "Finance & Materiel", Icons.Filled.Inventory2),
    SidebarItemData(11, "Audits", Icons.AutoMirrored.Filled.ReceiptLong),
    SidebarItemData(12, "Mode coffre fort", Icons.Filled.Lock),
    SidebarItemData(16, "Statistiques", Icons.Filled.BarChart),
    SidebarItemData(18, "Comptabilité", Icons.Filled.AccountBalanceWallet),
    SidebarItemData(17, "Communication", Icons.Default.Email),
    SidebarItemData(5, "Parametres", Icons.Filled.Settings)
)
