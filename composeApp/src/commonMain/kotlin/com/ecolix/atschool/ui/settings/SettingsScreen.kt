package com.ecolix.atschool.ui.settings

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecolix.atschool.ui.dashboard.components.CardContainer
import com.ecolix.atschool.ui.dashboard.models.DashboardColors

@Composable
fun SettingsScreen(
    colors: DashboardColors
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("L'École", "Académique", "Données & Système", "Préférences")

    // State for School Settings
    var schoolName by remember { mutableStateOf("Groupe Scolaire Ecolix") }
    var schoolCode by remember { mutableStateOf("GS-001") }
    var schoolSlogan by remember { mutableStateOf("L'excellence au service de l'avenir") }
    var schoolLevel by remember { mutableStateOf("Primaire") }
    var ministry by remember { mutableStateOf("Ministère de l'Enseignement Primaire, Secondaire et Technique") }
    var republicName by remember { mutableStateOf("RÉPUBLIQUE TOGOLAISE") }
    var republicMotto by remember { mutableStateOf("Travail - Liberté - Patrie") }
    var inspection by remember { mutableStateOf("IEPP Lomé-Centre") }
    var educationDirection by remember { mutableStateOf("Direction Régionale de l'Éducation Maritime") }
    
    // Contact Info
    var phone by remember { mutableStateOf("+228 90 00 00 00") }
    var email by remember { mutableStateOf("contact@ecolix-togo.com") }
    var website by remember { mutableStateOf("www.ecolix-togo.com") }
    var bp by remember { mutableStateOf("BP 1234 Lomé") }
    var address by remember { mutableStateOf("Lomé, Quartier Administratif") }
    var pdfFooter by remember { mutableStateOf("Bulletin de notes officiel - Système Généré par ÉcoliX") }

    // Directors & Civilities
    var genCivility by remember { mutableStateOf("M.") }
    var genDirector by remember { mutableStateOf("Seth Kouamé") }

    var matCivility by remember { mutableStateOf("Mme") }
    var matDirector by remember { mutableStateOf("") }
    
    var priCivility by remember { mutableStateOf("M.") }
    var priDirector by remember { mutableStateOf("") }
    
    var colCivility by remember { mutableStateOf("M.") }
    var colDirector by remember { mutableStateOf("") }
    
    var lycCivility by remember { mutableStateOf("M.") }
    var lycDirector by remember { mutableStateOf("") }
    
    var uniCivility by remember { mutableStateOf("Pr") }
    var uniDirector by remember { mutableStateOf("") }
    
    var supCivility by remember { mutableStateOf("Dr") }
    var supDirector by remember { mutableStateOf("") }

    // Academic Settings
    var academicYear by remember { mutableStateOf("2024-2025") }
    var gradingBase by remember { mutableStateOf("20 / 20") }
    var useTrimesters by remember { mutableStateOf(true) }
    var useSemesters by remember { mutableStateOf(false) }

    // Data & System Settings
    var autoBackup by remember { mutableStateOf(true) }
    var backupFrequency by remember { mutableStateOf("Quotidienne") }
    var retentionDays by remember { mutableStateOf(30f) }
    
    // User Preferences & Profile
    var userDisplayName by remember { mutableStateOf("Seth Kouamé") }
    var userRole by remember { mutableStateOf("Administrateur") }
    var userAvatar by remember { mutableStateOf<String?>(null) }
    var isDarkMode by remember { mutableStateOf(false) }
    var reducedAnimations by remember { mutableStateOf(false) }
    var realTimeNotifications by remember { mutableStateOf(true) }
    var selectedLanguage by remember { mutableStateOf("Français") }
    var is2FAEnabled by remember { mutableStateOf(false) }

    // Dialog States
    var showNewYearDialog by remember { mutableStateOf(false) }
    var showArchiveDialog by remember { mutableStateOf(false) }
    var showAuditDialog by remember { mutableStateOf(false) }

    if (showNewYearDialog) {
        NewAcademicYearDialog(
            colors = colors,
            onDismiss = { showNewYearDialog = false },
            onConfirm = { newYear ->
                academicYear = newYear
                showNewYearDialog = false
            }
        )
    }

    if (showArchiveDialog) {
        ArchiveAcademicYearDialog(
            colors = colors,
            academicYear = academicYear,
            onDismiss = { showArchiveDialog = false },
            onConfirm = {
                // Logic for archiving would go here
                showArchiveDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        Text(
            text = "Paramètres",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.textPrimary
        )

        // Tabs
        SecondaryTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { 
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(selectedTab),
                    color = MaterialTheme.colorScheme.primary
                )
            },
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { 
                        Text(
                            title, 
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        ) 
                    },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = colors.textMuted
                )
            }
        }

        // Animated Content for Tabs
        Box(modifier = Modifier.weight(1f)) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() }
            ) { targetTab ->
                when (targetTab) {
                    0 -> SchoolSettingsTab(
                        colors = colors,
                        schoolName = schoolName, onSchoolNameChange = { schoolName = it },
                        schoolCode = schoolCode, onSchoolCodeChange = { schoolCode = it },
                        schoolSlogan = schoolSlogan, onSchoolSloganChange = { schoolSlogan = it },
                        schoolLevel = schoolLevel, onSchoolLevelChange = { schoolLevel = it },
                        ministry = ministry, onMinistryChange = { ministry = it },
                        republicName = republicName, onRepublicNameChange = { republicName = it },
                        republicMotto = republicMotto, onRepublicMottoChange = { republicMotto = it },
                        inspection = inspection, onInspectionChange = { inspection = it },
                        educationDirection = educationDirection, onEducationDirectionChange = { educationDirection = it },
                        phone = phone, onPhoneChange = { phone = it },
                        email = email, onEmailChange = { email = it },
                        website = website, onWebsiteChange = { website = it },
                        bp = bp, onBpChange = { bp = it },
                        address = address, onAddressChange = { address = it },
                        pdfFooter = pdfFooter, onPdfFooterChange = { pdfFooter = it },
                        genCivility = genCivility, onGenCivilityChange = { genCivility = it },
                        genDirector = genDirector, onGenDirectorChange = { genDirector = it },
                        matCivility = matCivility, onMatCivilityChange = { matCivility = it },
                        matDirector = matDirector, onMatDirectorChange = { matDirector = it },
                        priCivility = priCivility, onPriCivilityChange = { priCivility = it },
                        priDirector = priDirector, onPriDirectorChange = { priDirector = it },
                        colCivility = colCivility, onColCivilityChange = { colCivility = it },
                        colDirector = colDirector, onColDirectorChange = { colDirector = it },
                        lycCivility = lycCivility, onLycCivilityChange = { lycCivility = it },
                        lycDirector = lycDirector, onLycDirectorChange = { lycDirector = it },
                        uniCivility = uniCivility, onUniCivilityChange = { uniCivility = it },
                        uniDirector = uniDirector, onUniDirectorChange = { uniDirector = it },
                        supCivility = supCivility, onSupCivilityChange = { supCivility = it },
                        supDirector = supDirector, onSupDirectorChange = { supDirector = it }
                    )
                    1 -> AcademicSettingsTab(
                        colors = colors,
                        academicYear = academicYear,
                        gradingBase = gradingBase,
                        useTrimesters = useTrimesters,
                        onTrimestersChange = { useTrimesters = it },
                        useSemesters = useSemesters,
                        onSemestersChange = { useSemesters = it },
                        onAddYearClick = { showNewYearDialog = true },
                        onArchiveClick = { showArchiveDialog = true }
                    )
                    2 -> SystemSettingsTab(
                        colors = colors,
                        autoBackup = autoBackup, onAutoBackupChange = { autoBackup = it },
                        backupFrequency = backupFrequency, onBackupFrequencyChange = { backupFrequency = it },
                        retentionDays = retentionDays, onRetentionDaysChange = { retentionDays = it }
                    )
                    3 -> PreferencesSettingsTab(
                        colors = colors,
                        userDisplayName = userDisplayName, onDisplayNameChange = { userDisplayName = it },
                        userRole = userRole, onUserRoleChange = { userRole = it },
                        userAvatar = userAvatar, onAvatarChange = { userAvatar = it },
                        isDarkMode = isDarkMode, onDarkModeChange = { isDarkMode = it },
                        reducedAnimations = reducedAnimations, onReducedAnimationsChange = { reducedAnimations = it },
                        realTimeNotifications = realTimeNotifications, onRealTimeNotificationsChange = { realTimeNotifications = it },
                        selectedLanguage = selectedLanguage, onLanguageChange = { selectedLanguage = it },
                        is2FAEnabled = is2FAEnabled, on2FAChange = { is2FAEnabled = it },
                        onShowAuditHistory = { showAuditDialog = true }
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomSettingsField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    colors: DashboardColors,
    modifier: Modifier = Modifier,
    shape: CornerBasedShape = RoundedCornerShape(12.dp),
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    maxLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        shape = shape,
        readOnly = readOnly,
        trailingIcon = trailingIcon,
        leadingIcon = leadingIcon,
        maxLines = maxLines,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = colors.divider,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = colors.textMuted,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = colors.textPrimary,
            unfocusedTextColor = colors.textPrimary,
            focusedPlaceholderColor = colors.textMuted.copy(alpha = 0.7f),
            unfocusedPlaceholderColor = colors.textMuted.copy(alpha = 0.5f)
        )
    )
}

@Composable
private fun SchoolSettingsTab(
    colors: DashboardColors,
    schoolName: String, onSchoolNameChange: (String) -> Unit,
    schoolCode: String, onSchoolCodeChange: (String) -> Unit,
    schoolSlogan: String, onSchoolSloganChange: (String) -> Unit,
    schoolLevel: String, onSchoolLevelChange: (String) -> Unit,
    ministry: String, onMinistryChange: (String) -> Unit,
    republicName: String, onRepublicNameChange: (String) -> Unit,
    republicMotto: String, onRepublicMottoChange: (String) -> Unit,
    inspection: String, onInspectionChange: (String) -> Unit,
    educationDirection: String, onEducationDirectionChange: (String) -> Unit,
    phone: String, onPhoneChange: (String) -> Unit,
    email: String, onEmailChange: (String) -> Unit,
    website: String, onWebsiteChange: (String) -> Unit,
    bp: String, onBpChange: (String) -> Unit,
    address: String, onAddressChange: (String) -> Unit,
    pdfFooter: String, onPdfFooterChange: (String) -> Unit,
    genCivility: String, onGenCivilityChange: (String) -> Unit,
    genDirector: String, onGenDirectorChange: (String) -> Unit,
    matCivility: String, onMatCivilityChange: (String) -> Unit,
    matDirector: String, onMatDirectorChange: (String) -> Unit,
    priCivility: String, onPriCivilityChange: (String) -> Unit,
    priDirector: String, onPriDirectorChange: (String) -> Unit,
    colCivility: String, onColCivilityChange: (String) -> Unit,
    colDirector: String, onColDirectorChange: (String) -> Unit,
    lycCivility: String, onLycCivilityChange: (String) -> Unit,
    lycDirector: String, onLycDirectorChange: (String) -> Unit,
    uniCivility: String, onUniCivilityChange: (String) -> Unit,
    uniDirector: String, onUniDirectorChange: (String) -> Unit,
    supCivility: String, onSupCivilityChange: (String) -> Unit,
    supDirector: String, onSupDirectorChange: (String) -> Unit
) {
    val levels = listOf("Maternelle", "Primaire", "Collège", "Lycée", "Université", "Enseignement Supérieur", "Complexe Scolaire")
    var showLevelMenu by remember { mutableStateOf(false) }
    val isComplexe = schoolLevel.contains("Complexe", ignoreCase = true)

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            CardContainer(containerColor = colors.card) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Identité de l'Établissement", fontWeight = FontWeight.Bold, color = colors.textPrimary)
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                        colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(44.dp), tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.textLink,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Changer Logo", fontSize = 12.sp)
                        }
                    }

                    Text("Armoiries / Logo de la République", fontWeight = FontWeight.Bold, color = colors.textPrimary, style = MaterialTheme.typography.bodySmall)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(colors.background),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(30.dp), tint = colors.textMuted)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        OutlinedButton(
                            onClick = {},
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, colors.divider)
                        ) {
                            Text("Logo République", fontSize = 11.sp, color = colors.textPrimary)
                        }
                    }

                    CustomSettingsField(
                        value = schoolName,
                        onValueChange = onSchoolNameChange,
                        label = "Nom de l'école",
                        colors = colors,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        CustomSettingsField(
                            value = schoolCode,
                            onValueChange = onSchoolCodeChange,
                            label = "Code Établissement",
                            colors = colors,
                            modifier = Modifier.weight(1f)
                        )
                        Box(modifier = Modifier.weight(1f)) {
                            Column {
                                Text("Niveau Scolaire", style = MaterialTheme.typography.bodySmall, color = colors.textMuted, modifier = Modifier.padding(bottom = 8.dp))
                                OutlinedButton(
                                    onClick = { showLevelMenu = true },
                                    modifier = Modifier.fillMaxWidth().height(52.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.divider)
                                ) {
                                    Text(schoolLevel.ifEmpty { "Sélectionner" }, modifier = Modifier.weight(1f), color = colors.textPrimary, fontSize = 14.sp)
                                    Icon(Icons.Default.ArrowDropDown, null, tint = colors.textMuted)
                                }
                                DropdownMenu(
                                    expanded = showLevelMenu,
                                    onDismissRequest = { showLevelMenu = false },
                                    modifier = Modifier.background(colors.card)
                                ) {
                                    levels.forEach { level ->
                                        DropdownMenuItem(
                                            text = { Text(level, color = colors.textPrimary) },
                                            onClick = {
                                                onSchoolLevelChange(level)
                                                showLevelMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    CustomSettingsField(
                        value = schoolSlogan,
                        onValueChange = onSchoolSloganChange,
                        label = "Devise / Slogan",
                        colors = colors,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        item {
            CardContainer(containerColor = colors.card) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Tutelle & Administration Centrale", fontWeight = FontWeight.Bold, color = colors.textPrimary)
                    
                    CustomSettingsField(
                        value = ministry,
                        onValueChange = onMinistryChange,
                        label = "Ministère de Tutelle",
                        colors = colors,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        CustomSettingsField(
                            value = republicName,
                            onValueChange = onRepublicNameChange,
                            label = "Nom de la République",
                            colors = colors,
                            modifier = Modifier.weight(1f)
                        )
                        CustomSettingsField(
                            value = republicMotto,
                            onValueChange = onRepublicMottoChange,
                            label = "Devise de la République",
                            colors = colors,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    CustomSettingsField(
                        value = educationDirection,
                        onValueChange = onEducationDirectionChange,
                        label = "Direction de l'Enseignement",
                        colors = colors,
                        modifier = Modifier.fillMaxWidth()
                    )

                    CustomSettingsField(
                        value = inspection,
                        onValueChange = onInspectionChange,
                        label = "Inspection de Ressort",
                        colors = colors,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        item {
            CardContainer(containerColor = colors.card) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Direction de l'Établissement", fontWeight = FontWeight.Bold, color = colors.textPrimary)
                    
                    DirectorProfileField("Directeur Général / Fondateur", genCivility, onGenCivilityChange, genDirector, onGenDirectorChange, colors)

                    if (isComplexe) {
                        Text("Responsables de Direction par Niveau", fontWeight = FontWeight.Bold, color = colors.textPrimary, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
                        
                        DirectorProfileField("Direction Maternelle", matCivility, onMatCivilityChange, matDirector, onMatDirectorChange, colors)
                        DirectorProfileField("Direction Primaire", priCivility, onPriCivilityChange, priDirector, onPriDirectorChange, colors)
                        DirectorProfileField("Direction Collège", colCivility, onColCivilityChange, colDirector, onColDirectorChange, colors)
                        DirectorProfileField("Direction Lycée", lycCivility, onLycCivilityChange, lycDirector, onLycDirectorChange, colors)
                        DirectorProfileField("Direction Université", uniCivility, onUniCivilityChange, uniDirector, onUniDirectorChange, colors)
                        DirectorProfileField("Direction Enseignement Supérieur", supCivility, onSupCivilityChange, supDirector, onSupDirectorChange, colors)
                    }
                }
            }
        }

        item {
            CardContainer(containerColor = colors.card) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Coordonnées & Contact", fontWeight = FontWeight.Bold, color = colors.textPrimary)
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        CustomSettingsField(
                            value = phone,
                            onValueChange = onPhoneChange,
                            label = "Téléphone",
                            colors = colors,
                            modifier = Modifier.weight(1f),
                            leadingIcon = { Icon(Icons.Default.Phone, null) }
                        )
                        CustomSettingsField(
                            value = email,
                            onValueChange = onEmailChange,
                            label = "Email",
                            colors = colors,
                            modifier = Modifier.weight(1f),
                            leadingIcon = { Icon(Icons.Default.Email, null) }
                        )
                    }

                    CustomSettingsField(
                        value = website,
                        onValueChange = onWebsiteChange,
                        label = "Site Web",
                        colors = colors,
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Language, null) }
                    )

                    CustomSettingsField(
                        value = bp,
                        onValueChange = onBpChange,
                        label = "Boîte Postale",
                        colors = colors,
                        modifier = Modifier.fillMaxWidth()
                    )

                    CustomSettingsField(
                        value = address,
                        onValueChange = onAddressChange,
                        label = "Adresse Physique",
                        colors = colors,
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.LocationOn, null) }
                    )
                }
            }
        }

        item {
            CardContainer(containerColor = colors.card) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Configuration des Bulletins", fontWeight = FontWeight.Bold, color = colors.textPrimary)
                    
                    CustomSettingsField(
                        value = pdfFooter,
                        onValueChange = onPdfFooterChange,
                        label = "Note de pied de page (Bulletins PDF)",
                        colors = colors,
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            }
        }
        
        item {
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Save, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sauvegarder les informations")
            }
        }
    }
}

@Composable
private fun AcademicSettingsTab(
    colors: DashboardColors,
    academicYear: String,
    gradingBase: String,
    useTrimesters: Boolean,
    onTrimestersChange: (Boolean) -> Unit,
    useSemesters: Boolean,
    onSemestersChange: (Boolean) -> Unit,
    onAddYearClick: () -> Unit,
    onArchiveClick: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            CardContainer(containerColor = colors.card) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Configuration de l'Année", fontWeight = FontWeight.Bold, color = colors.textPrimary)
                    
                    CustomSettingsField(
                        value = academicYear,
                        onValueChange = {},
                        label = "Année Scolaire Active",
                        colors = colors,
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = { Icon(Icons.Default.CalendarToday, null) }
                    )
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CustomSettingsField(
                            value = gradingBase,
                            onValueChange = {},
                            label = "Base de Note",
                            colors = colors,
                            modifier = Modifier.weight(1f),
                            readOnly = true
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Box(modifier = Modifier.weight(1f)) // Placeholder
                    }

                    Text("Découpages de l'Année", fontWeight = FontWeight.Bold, color = colors.textPrimary, style = MaterialTheme.typography.bodySmall)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f).clickable { onTrimestersChange(!useTrimesters) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (useTrimesters) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else colors.background
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp, 
                                if (useTrimesters) MaterialTheme.colorScheme.primary else colors.divider
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = useTrimesters,
                                    onCheckedChange = onTrimestersChange,
                                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                                )
                                Text("Trimestres", color = colors.textPrimary, fontSize = 13.sp)
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f).clickable { onSemestersChange(!useSemesters) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (useSemesters) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else colors.background
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp, 
                                if (useSemesters) MaterialTheme.colorScheme.primary else colors.divider
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = useSemesters,
                                    onCheckedChange = onSemestersChange,
                                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                                )
                                Text("Semestres", color = colors.textPrimary, fontSize = 13.sp)
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = onAddYearClick,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Nouvelle Année")
                        }
                        Button(
                            onClick = onArchiveClick,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF64748B),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Default.Archive, null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Archiver")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SystemSettingsTab(
    colors: DashboardColors,
    autoBackup: Boolean, onAutoBackupChange: (Boolean) -> Unit,
    backupFrequency: String, onBackupFrequencyChange: (String) -> Unit,
    retentionDays: Float, onRetentionDaysChange: (Float) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            CardContainer(containerColor = colors.card) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Sauvegarde Automatique", fontWeight = FontWeight.Bold, color = colors.textPrimary)
                    
                    SettingsToggleRow(
                        label = "Activer la sauvegarde auto",
                        checked = autoBackup,
                        onCheckedChange = onAutoBackupChange,
                        colors = colors
                    )

                    if (autoBackup) {
                        var showFrequencyMenu by remember { mutableStateOf(false) }
                        val frequencies = listOf("Quotidienne", "Hebdomadaire", "Mensuelle")

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Fréquence", color = colors.textPrimary)
                            Box {
                                TextButton(onClick = { showFrequencyMenu = true }) {
                                    Text(backupFrequency, color = colors.textLink)
                                    Icon(Icons.Default.ArrowDropDown, null, tint = colors.textLink)
                                }
                                DropdownMenu(
                                    expanded = showFrequencyMenu,
                                    onDismissRequest = { showFrequencyMenu = false },
                                    modifier = Modifier.background(colors.card)
                                ) {
                                    frequencies.forEach { freq ->
                                        DropdownMenuItem(
                                            text = { Text(freq, color = colors.textPrimary) },
                                            onClick = {
                                                onBackupFrequencyChange(freq)
                                                showFrequencyMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Rétention", color = colors.textPrimary)
                                Text("${retentionDays.toInt()} jours", color = colors.textMuted, fontSize = 12.sp)
                            }
                            Slider(
                                value = retentionDays,
                                onValueChange = onRetentionDaysChange,
                                valueRange = 7f..90f,
                                steps = 83,
                                colors = SliderDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.primary,
                                    activeTrackColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }
            }
        }

        item {
            CardContainer(containerColor = colors.card) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Maintenance & Données", fontWeight = FontWeight.Bold, color = colors.textPrimary)
                    
                    SystemActionRow(
                        title = "Sauvegarder immédiatement",
                        icon = Icons.Default.Backup,
                        color = Color(0xFF10B981),
                        colors = colors
                    )
                    
                    SystemActionRow(
                        title = "Restaurer une sauvegarde DB",
                        icon = Icons.Default.Restore,
                        color = Color(0xFF3B82F6),
                        colors = colors
                    )

                    SystemActionRow(
                        title = "Exporter les données (CSV/PDF)",
                        icon = Icons.Default.FileDownload,
                        color = Color(0xFFF59E0B),
                        colors = colors
                    )
                }
            }
        }

        item {
            CardContainer(containerColor = colors.card) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("À propos du Système", fontWeight = FontWeight.Bold, color = colors.textPrimary)
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.background)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.VpnKey, null, tint = colors.textLink)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Licence Professionnelle", fontWeight = FontWeight.Bold, color = colors.textPrimary)
                            Text("État : Active (Cabinet ACTe)", style = MaterialTheme.typography.bodySmall, color = colors.textMuted)
                        }
                        TextButton(onClick = {}) { Text("Vérifier") }
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("ÉcoliX Erp Education", fontWeight = FontWeight.Bold, color = colors.textPrimary)
                        Text("Version 2.5.0-stable", style = MaterialTheme.typography.bodySmall, color = colors.textMuted)
                        Text("Développé pour l'excellence académique", style = MaterialTheme.typography.labelSmall, color = colors.textLink)
                    }
                }
            }
        }
    }
}

@Composable
private fun SystemActionRow(title: String, icon: ImageVector, color: Color, colors: DashboardColors) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, modifier = Modifier.weight(1f), color = colors.textPrimary)
        Icon(Icons.Default.ChevronRight, null, tint = colors.textMuted)
    }
}

@Composable
private fun PreferencesSettingsTab(
    colors: DashboardColors,
    userDisplayName: String, onDisplayNameChange: (String) -> Unit,
    userRole: String, onUserRoleChange: (String) -> Unit,
    userAvatar: String?, onAvatarChange: (String?) -> Unit,
    isDarkMode: Boolean, onDarkModeChange: (Boolean) -> Unit,
    reducedAnimations: Boolean, onReducedAnimationsChange: (Boolean) -> Unit,
    realTimeNotifications: Boolean, onRealTimeNotificationsChange: (Boolean) -> Unit,
    selectedLanguage: String, onLanguageChange: (String) -> Unit,
    is2FAEnabled: Boolean, on2FAChange: (Boolean) -> Unit,
    onShowAuditHistory: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            CardContainer(containerColor = colors.card) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Identité de l'Utilisateur", fontWeight = FontWeight.Bold, color = colors.textPrimary)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(colors.background)
                                .clickable { /* Gallery picker */ },
                            contentAlignment = Alignment.Center
                        ) {
                            if (userAvatar != null) {
                                // Fallback to icon for now
                                Icon(Icons.Default.Person, null, modifier = Modifier.size(40.dp), tint = colors.textMuted)
                            } else {
                                Icon(Icons.Default.AddAPhoto, null, modifier = Modifier.size(24.dp), tint = colors.textMuted)
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(20.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(userDisplayName, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colors.textPrimary)
                            Surface(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    userRole,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        
                        IconButton(onClick = { /* Edit mode */ }) {
                            Icon(Icons.Default.Edit, "Modifier le profil", tint = colors.textMuted)
                        }
                    }

                    CustomSettingsField(
                        value = userDisplayName,
                        onValueChange = onDisplayNameChange,
                        label = "Nom complet affiché",
                        colors = colors,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        item {
            CardContainer(containerColor = colors.card) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Sécurité du Compte", fontWeight = FontWeight.Bold, color = colors.textPrimary)
                    
                    SettingsToggleRow(
                        label = "Double Authentification (2FA)",
                        checked = is2FAEnabled,
                        onCheckedChange = on2FAChange,
                        colors = colors
                    )

                    SystemActionRow(
                        title = "Journaux d'Audit & Historique",
                        icon = Icons.Default.History,
                        color = Color(0xFF6366F1),
                        colors = colors
                    )

                    Button(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.background,
                            contentColor = colors.textPrimary
                        )
                    ) {
                        Icon(Icons.Default.LockReset, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Modifier le mot de passe")
                    }
                }
            }
        }

        item {
            CardContainer(containerColor = colors.card) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Préférences d'Affichage", fontWeight = FontWeight.Bold, color = colors.textPrimary)
                    
                    SettingsToggleRow("Mode Sombre Dynamique", isDarkMode, onDarkModeChange, colors)
                    SettingsToggleRow("Optimisation des Animations", !reducedAnimations, { onReducedAnimationsChange(!it) }, colors)
                    SettingsToggleRow("Notifications Temps Réel", realTimeNotifications, onRealTimeNotificationsChange, colors)

                    var showLangMenu by remember { mutableStateOf(false) }
                    val languages = listOf("Français", "English", "Español")

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Langue du Système", color = colors.textPrimary)
                        Box {
                            TextButton(onClick = { showLangMenu = true }) {
                                Text(selectedLanguage, color = colors.textLink)
                                Icon(Icons.Default.Translate, null, modifier = Modifier.size(16.dp).padding(start = 4.dp), tint = colors.textLink)
                            }
                            DropdownMenu(
                                expanded = showLangMenu,
                                onDismissRequest = { showLangMenu = false },
                                modifier = Modifier.background(colors.card)
                            ) {
                                languages.forEach { lang ->
                                    DropdownMenuItem(
                                        text = { Text(lang, color = colors.textPrimary) },
                                        onClick = {
                                            onLanguageChange(lang)
                                            showLangMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NewAcademicYearDialog(
    colors: DashboardColors,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var year by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.card,
        title = {
            Text("Nouvelle Année Académique", fontWeight = FontWeight.Bold, color = colors.textPrimary)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                CustomSettingsField(
                    value = year,
                    onValueChange = { year = it },
                    label = "Année (ex: 2025-2026)",
                    colors = colors,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    "L'application archivera les données de l'année actuelle avant de passer à la nouvelle.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (year.isNotEmpty()) onConfirm(year) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Text("Confirmer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = colors.textMuted)
            }
        }
    )
}

@Composable
private fun ArchiveAcademicYearDialog(
    colors: DashboardColors,
    academicYear: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.card,
        title = {
            Text("Archiver l'Année Académique", fontWeight = FontWeight.Bold, color = colors.textPrimary)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFFFEF2F2)).padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.WarningAmber, null, tint = Color(0xFFEF4444), modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Attention : Action Irréversible",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF991B1B)
                        )
                    }
                }
                Text(
                    "Vous êtes sur le point d'archiver l'année scolaire $academicYear. " +
                    "Toutes les données de cette année seront figées et transférées dans les dossiers d'archives.",
                    color = colors.textPrimary
                )
                Text(
                    "Une nouvelle année scolaire devra être initialisée pour continuer les opérations.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEF4444),
                    contentColor = Color.White
                )
            ) {
                Text("Archiver Définitivement")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = colors.textMuted)
            }
        }
    )
}

@Composable
private fun DirectorProfileField(
    label: String,
    civility: String,
    onCivilityChange: (String) -> Unit,
    name: String,
    onNameChange: (String) -> Unit,
    colors: DashboardColors
) {
    val civilities = listOf("M.", "Mme", "Mlle", "Dr", "Pr", "Rév", "Abbé", "Frère", "Sœur", "Me", "Ing.")
    var showCivilityMenu by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = colors.textMuted)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box {
                OutlinedButton(
                    onClick = { showCivilityMenu = true },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(56.dp).width(80.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.divider),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.textPrimary)
                ) {
                    Text(civility, fontSize = 14.sp)
                    Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(16.dp))
                }
                DropdownMenu(
                    expanded = showCivilityMenu,
                    onDismissRequest = { showCivilityMenu = false },
                    modifier = Modifier.background(colors.card)
                ) {
                    civilities.forEach { civ ->
                        DropdownMenuItem(
                            text = { Text(civ, color = colors.textPrimary) },
                            onClick = {
                                onCivilityChange(civ)
                                showCivilityMenu = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            CustomSettingsField(
                value = name,
                onValueChange = onNameChange,
                label = "Nom du Responsable",
                colors = colors,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SettingsToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, colors: DashboardColors) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onCheckedChange(!checked) }.padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = colors.textPrimary, style = MaterialTheme.typography.bodyLarge)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedTrackColor = colors.textLink)
        )
    }
}
