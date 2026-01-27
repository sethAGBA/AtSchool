package com.ecolix.presentation.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecolix.data.models.DashboardColors
import com.ecolix.data.models.User
import com.ecolix.data.models.UserRole
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserForm(
    user: User? = null,
    colors: DashboardColors,
    isCompact: Boolean = false,
    onSave: (User) -> Unit
) {
    // UI State
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Identité", "Sécurité", "Permissions", "Profil")

    // General Identity State
    var fullName by remember { mutableStateOf(user?.fullName ?: "") }
    var username by remember { mutableStateOf(user?.username ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var selectedRole by remember { mutableStateOf(user?.role ?: UserRole.STUDENT) }
    var status by remember { mutableStateOf(user?.status ?: "Actif") }
    
    // Security State
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    // Permissions State (Mock granular permissions)
    val permissions = remember { 
        mutableStateMapOf(
            "view_grades" to true,
            "edit_grades" to false,
            "view_absences" to true,
            "manage_absences" to false,
            "view_finances" to false,
            "manage_users" to false
        )
    }

    // Profile Details State
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    
    // Linked Entity State
    var linkedStaffId by remember { mutableStateOf(user?.linkedStaffId ?: "") }
    val linkedStudentIds = remember { mutableStateListOf<String>().apply { addAll(user?.linkedStudentIds ?: emptyList()) } }

    // Validation
    var showErrors by remember { mutableStateOf(false) }
    val isFormValid = fullName.isNotBlank() && username.isNotBlank() && email.contains("@") && (user != null || password.length >= 6) && password == confirmPassword

    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f)
    ) {
        // Tab Navigation
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            edgePadding = 0.dp,
            divider = { HorizontalDivider(color = colors.divider.copy(alpha = 0.5f)) }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontSize = 13.sp, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Content Area
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> IdentityTab(
                    fullName, { fullName = it }, 
                    username, { username = it }, 
                    email, { email = it }, 
                    selectedRole, { selectedRole = it }, 
                    status, { status = it }, 
                    linkedStaffId, { linkedStaffId = it },
                    linkedStudentIds,
                    colors, isCompact, showErrors
                )
                1 -> SecurityTab(password, { password = it }, confirmPassword, { confirmPassword = it }, passwordVisible, { passwordVisible = it }, colors, showErrors)
                2 -> PermissionsTab(permissions, colors)
                3 -> ProfileTab(phone, { phone = it }, address, { address = it }, bio, { bio = it }, colors)
            }
        }

        // Bottom Actions
        Column(modifier = Modifier.padding(top = 16.dp)) {
            HorizontalDivider(color = colors.divider.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (isFormValid) {
                        onSave(User(
                            id = user?.id ?: "USR_${Random.nextInt(1000, 9999)}",
                            fullName = fullName,
                            username = username,
                            email = email,
                            role = selectedRole,
                        status = status,
                        lastLogin = user?.lastLogin,
                        linkedStudentIds = if (selectedRole == UserRole.PARENT || selectedRole == UserRole.STUDENT) linkedStudentIds.toList() else emptyList(),
                        linkedStaffId = if (selectedRole == UserRole.TEACHER || selectedRole == UserRole.ADMIN || selectedRole == UserRole.MANAGER) linkedStaffId else null
                    ))
                    } else {
                        showErrors = true
                        selectedTab = 0 // Return to first tab to show errors
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Save, null)
                Spacer(modifier = Modifier.width(12.dp))
                Text(if (user == null) "Créer le Compte" else "Enregistrer les Modifications", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun IdentityTab(
    fullName: String, onFullNameChange: (String) -> Unit,
    username: String, onUsernameChange: (String) -> Unit,
    email: String, onEmailChange: (String) -> Unit,
    selectedRole: UserRole, onRoleChange: (UserRole) -> Unit,
    status: String, onStatusChange: (String) -> Unit,
    linkedStaffId: String, onLinkedStaffIdChange: (String) -> Unit,
    linkedStudentIds: MutableList<String>,
    colors: DashboardColors, isCompact: Boolean, showErrors: Boolean
) {
    var teacherSpecialization by remember { mutableStateOf("") }
    
    LazyColumn(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        item {
            SectionHeader("Informations de Base", colors)
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                FormTextField("Nom Complet *", fullName, onFullNameChange, colors, icon = Icons.Default.Badge, isError = showErrors && fullName.isBlank())
                FormTextField("Nom d'Utilisateur *", username, onUsernameChange, colors, icon = Icons.Default.Person, isError = showErrors && username.isBlank())
                FormTextField("Email Professionnel *", email, onEmailChange, colors, icon = Icons.Default.Email, isError = showErrors && !email.contains("@"))
            }
        }

        item {
            SectionHeader("Assignation du Rôle", colors)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Le rôle définit les accès par défaut :", style = MaterialTheme.typography.labelMedium, color = colors.textMuted)
                val roleRows = if (isCompact) UserRole.entries.chunked(2) else listOf(UserRole.entries.toList())
                roleRows.forEach { rowRoles ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        rowRoles.forEach { role ->
                            RoleCard(role, selectedRole == role, { onRoleChange(role) }, colors, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
        
        // Dynamic Fields Based on Role
        if (selectedRole == UserRole.TEACHER) {
            item {
                SectionHeader("Spécialisation Enseignant", colors)
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    FormTextField(
                        label = "Matières / Spécialités", 
                        value = teacherSpecialization, 
                        onValueChange = { teacherSpecialization = it }, 
                        colors = colors,
                        placeholder = "Mathématiques, Physique...",
                        icon = Icons.Default.HistoryEdu
                    )
                }
            }
        }

        if (selectedRole == UserRole.STUDENT) {
            item {
                SectionHeader("Détails Scolaires", colors)
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    FormTextField(
                        label = "Classe de l'Élève", 
                        value = "", // Mock
                        onValueChange = { }, 
                        colors = colors,
                        placeholder = "6ème A, Terminale C...",
                        icon = Icons.Default.Class
                    )
                    FormTextField(
                        label = "Lien de Parenté (Parent)", 
                        value = if (linkedStudentIds.isNotEmpty()) linkedStudentIds.first() else "", 
                        onValueChange = { if (it.isEmpty()) linkedStudentIds.clear() else { if (linkedStudentIds.isEmpty()) linkedStudentIds.add(it) else linkedStudentIds[0] = it } }, 
                        colors = colors,
                        placeholder = "Rechercher un parent...",
                        icon = Icons.Default.FamilyRestroom
                    )
                }
            }
        }

        if (selectedRole == UserRole.PARENT) {
            item {
                SectionHeader("Liaison Élèves", colors)
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    var studentSearch by remember { mutableStateOf("") }
                    
                    FormTextField(
                        label = "Ajouter un élève *", 
                        value = studentSearch, 
                        onValueChange = { studentSearch = it }, 
                        colors = colors,
                        placeholder = "Nom ou Matricule de l'élève...",
                        icon = Icons.Default.Add,
                        onIconClick = { 
                            if (studentSearch.isNotBlank()) {
                                linkedStudentIds.add(studentSearch)
                                studentSearch = ""
                            }
                        }
                    )
                    
                    if (linkedStudentIds.isNotEmpty()) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            linkedStudentIds.forEach { studentId ->
                                AssistChip(
                                    onClick = { },
                                    label = { Text(studentId, fontSize = 12.sp) },
                                    trailingIcon = { 
                                        Icon(
                                            Icons.Default.Close, 
                                            null, 
                                            modifier = Modifier.size(16.dp).clickable { linkedStudentIds.remove(studentId) }
                                        ) 
                                    },
                                    colors = AssistChipDefaults.assistChipColors(
                                        labelColor = colors.textPrimary,
                                        containerColor = colors.background
                                    )
                                )
                            }
                        }
                    } else if (showErrors) {
                        Text("Au moins un élève doit être relié", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        item {
            SectionHeader("Statut du Compte", colors)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("Actif", "Inactif", "Suspendu").forEach { label ->
                    val color = when(label) { "Actif" -> Color(0xFF10B981) "Inactif" -> Color(0xFF64748B) else -> Color(0xFFEF4444) }
                    FilterChip(
                        selected = status == label,
                        onClick = { onStatusChange(label) },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = color.copy(alpha = 0.15f), selectedLabelColor = color)
                    )
                }
            }
        }
    }
}

@Composable
private fun SecurityTab(
    password: String, onPasswordChange: (String) -> Unit,
    confirmPassword: String, onConfirmChange: (String) -> Unit,
    passwordVisible: Boolean, onVisibilityChange: (Boolean) -> Unit,
    colors: DashboardColors, showErrors: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        SectionHeader("Sécurisation", colors)
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            FormTextField(
                label = "Mot de passe",
                value = password,
                onValueChange = onPasswordChange,
                colors = colors,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                onIconClick = { onVisibilityChange(!passwordVisible) },
                isError = showErrors && password.isNotEmpty() && password.length < 6
            )
            
            Text("Minimum 6 caractères. Utilisez des chiffres et symboles.", style = MaterialTheme.typography.labelSmall, color = colors.textMuted)

            FormTextField(
                label = "Confirmer le mot de passe",
                value = confirmPassword,
                onValueChange = onConfirmChange,
                colors = colors,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = showErrors && confirmPassword != password
            )
        }
        
        if (password.isNotEmpty()) {
            val strength = when {
                password.length >= 10 && password.any { it.isDigit() } -> "Fort"
                password.length >= 6 -> "Moyen"
                else -> "Faible"
            }
            val strengthColor = when(strength) { "Fort" -> Color(0xFF10B981) "Moyen" -> Color(0xFFF59E0B) else -> Color(0xFFEF4444) }
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Force du mot de passe : $strength", color = strengthColor, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                LinearProgressIndicator(
                    progress = { when(strength) { "Fort" -> 1f "Moyen" -> 0.6f else -> 0.3f } },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                    color = strengthColor,
                    trackColor = colors.divider
                )
            }
        }
    }
}

@Composable
private fun PermissionsTab(permissions: MutableMap<String, Boolean>, colors: DashboardColors) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { SectionHeader("Accès Granulaires", colors) }
        
        val permissionList = listOf(
            "view_grades" to "Voir les notes des élèves",
            "edit_grades" to "Modifier les notes/évaluations",
            "view_absences" to "Consulter les feuilles de présence",
            "manage_absences" to "Enregistrer les absences",
            "view_finances" to "Accéder aux données financières",
            "manage_users" to "Gérer les autres utilisateurs"
        )
        
        items(permissionList) { (key, label) ->
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(colors.background).padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(label, style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
                Switch(
                    checked = permissions[key] ?: false,
                    onCheckedChange = { permissions[key] = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}

@Composable
private fun ProfileTab(
    phone: String, onPhoneChange: (String) -> Unit,
    address: String, onAddressChange: (String) -> Unit,
    bio: String, onBioChange: (String) -> Unit,
    colors: DashboardColors
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        item {
            SectionHeader("Informations Complémentaires", colors)
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                FormTextField("Téléphone", phone, onPhoneChange, colors, icon = Icons.Default.Phone, keyboardType = KeyboardType.Phone)
                FormTextField("Adresse Résidentielle", address, onAddressChange, colors, icon = Icons.Default.Home)
                FormTextField("Biographie / Notes", bio, onBioChange, colors, icon = Icons.Default.Notes, lines = 3)
            }
        }
        
        item {
            SectionHeader("Préférences", colors)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Langue de l'interface : ", color = colors.textMuted, style = MaterialTheme.typography.bodyMedium)
                Text("Français (FR)", color = colors.textPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { })
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.Language, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun RoleCard(role: UserRole, isSelected: Boolean, onClick: () -> Unit, colors: DashboardColors, modifier: Modifier = Modifier) {
    val borderColor = if (isSelected) role.color else colors.divider
    val iconColor = if (isSelected) role.color else colors.textMuted
    val containerColor = if (isSelected) role.color.copy(alpha = 0.1f) else colors.card
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(containerColor)
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(role.icon, null, tint = iconColor, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(role.label, color = if (isSelected) colors.textPrimary else colors.textMuted, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, fontSize = 11.sp, textAlign = TextAlign.Center)
        }
    }
}
