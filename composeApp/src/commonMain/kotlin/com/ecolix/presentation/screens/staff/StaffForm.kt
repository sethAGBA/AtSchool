package com.ecolix.presentation.screens.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecolix.data.models.DashboardColors
import com.ecolix.data.models.Staff
import com.ecolix.data.models.StaffRole
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffForm(
    staff: Staff? = null,
    colors: DashboardColors,
    isCompact: Boolean = false,
    onBack: () -> Unit,
    onSave: (Staff) -> Unit
) {
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = colors.textPrimary,
        unfocusedTextColor = colors.textPrimary,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = colors.divider.copy(alpha = if (colors.textPrimary == Color(0xFF1E293B)) 0.8f else 0.5f),
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = colors.textMuted,
        focusedPlaceholderColor = colors.textMuted,
        unfocusedPlaceholderColor = colors.textMuted.copy(alpha = 0.7f)
    )

    // Identity & Civil Status
    var firstName by remember { mutableStateOf(staff?.firstName ?: "") }
    var lastName by remember { mutableStateOf(staff?.lastName ?: "") }
    var gender by remember { mutableStateOf(staff?.gender ?: "M") }
    var birthDate by remember { mutableStateOf(staff?.birthDate ?: "") }
    var birthPlace by remember { mutableStateOf(staff?.birthPlace ?: "") }
    var nationality by remember { mutableStateOf(staff?.nationality ?: "Ivoirienne") }
    var maritalStatus by remember { mutableStateOf(staff?.maritalStatus ?: "Célibataire") }
    var numberOfChildren by remember { mutableStateOf(staff?.numberOfChildren?.toString() ?: "0") }
    
    // Administrative Identifiers
    var matricule by remember { mutableStateOf(staff?.matricule ?: "P-${Random.nextInt(100, 999)}") }
    var idNumber by remember { mutableStateOf(staff?.idNumber ?: "") }
    var socialSecurityNumber by remember { mutableStateOf(staff?.socialSecurityNumber ?: "") }
    
    // Contact
    var email by remember { mutableStateOf(staff?.email ?: "") }
    var phone by remember { mutableStateOf(staff?.phone ?: "") }
    var address by remember { mutableStateOf(staff?.address ?: "") }
    
    // Professional Information
    var selectedRole by remember { mutableStateOf(staff?.role ?: StaffRole.TEACHER) }
    var selectedDepartment by remember { mutableStateOf(staff?.department ?: "Administration") }
    var specialty by remember { mutableStateOf(staff?.specialty ?: "") }
    var qualifications by remember { mutableStateOf(staff?.qualifications ?: "") }
    var highestDegree by remember { mutableStateOf(staff?.highestDegree ?: "") }
    var experienceYears by remember { mutableStateOf(staff?.experienceYears?.toString() ?: "0") }
    var previousInstitution by remember { mutableStateOf(staff?.previousInstitution ?: "") }
    var region by remember { mutableStateOf(staff?.region ?: "") }
    var supervisor by remember { mutableStateOf(staff?.supervisor ?: "") }
    
    // Contract & History
    var status by remember { mutableStateOf(staff?.status ?: "Actif") }
    var contractType by remember { mutableStateOf(staff?.contractType ?: "CDI") }
    var baseSalary by remember { mutableStateOf(staff?.baseSalary?.toString() ?: "") }
    var weeklyHours by remember { mutableStateOf(staff?.weeklyHours?.toString() ?: "0") }
    var joinDate by remember { mutableStateOf(staff?.joinDate ?: "26/01/2026") }
    var retirementDate by remember { mutableStateOf(staff?.retirementDate ?: "") }

    // Dropdown states
    var roleExpanded by remember { mutableStateOf(false) }
    var deptExpanded by remember { mutableStateOf(false) }
    var maritalExpanded by remember { mutableStateOf(false) }
    var contractExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    // Validation State
    var showErrors by remember { mutableStateOf(false) }
    val isFirstNameValid = firstName.isNotBlank()
    val isLastNameValid = lastName.isNotBlank()
    val isEmailValid = email.contains("@")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(if (isCompact) 16.dp else 24.dp),
        verticalArrangement = Arrangement.spacedBy(if (isCompact) 16.dp else 24.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colors.textPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (staff == null) "Ajouter un Personnel" else "Modifier: ${staff.firstName}",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // IDENTITY SECTION
            item {
                SectionHeader("Identité & État Civil", colors)
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    if (isCompact) {
                        FormTextField("Nom *", lastName, { lastName = it }, colors, isError = showErrors && !isLastNameValid)
                        FormTextField("Prénom(s) *", firstName, { firstName = it }, colors, isError = showErrors && !isFirstNameValid)
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            FormTextField("Nom *", lastName, { lastName = it }, colors, Modifier.weight(1f), isError = showErrors && !isLastNameValid)
                            FormTextField("Prénom(s) *", firstName, { firstName = it }, colors, Modifier.weight(1f), isError = showErrors && !isFirstNameValid)
                        }
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Sexe: ", color = colors.textPrimary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(16.dp))
                        GenderOption("M", "Masculin", gender == "M", { gender = it }, colors)
                        Spacer(modifier = Modifier.width(24.dp))
                        GenderOption("F", "Féminin", gender == "F", { gender = it }, colors)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        FormTextField("Date de Naissance", birthDate, { birthDate = it }, colors, Modifier.weight(1f), placeholder = "JJ/MM/AAAA", icon = Icons.Default.CalendarToday)
                        FormTextField("Lieu de Naissance", birthPlace, { birthPlace = it }, colors, Modifier.weight(1f))
                    }
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        FormTextField("Nationalité", nationality, { nationality = it }, colors, Modifier.weight(1f))
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = maritalStatus,
                                onValueChange = {},
                                label = { Text("État Civil") },
                                modifier = Modifier.fillMaxWidth(),
                                readOnly = true,
                                trailingIcon = { IconButton(onClick = { maritalExpanded = true }) { Icon(Icons.Default.ArrowDropDown, null) } },
                                shape = RoundedCornerShape(12.dp),
                                colors = fieldColors
                            )
                            DropdownMenu(expanded = maritalExpanded, onDismissRequest = { maritalExpanded = false }, modifier = Modifier.fillMaxWidth(0.9f).background(colors.card)) {
                                listOf("Célibataire", "Marié(e)", "Divorcé(e)", "Veuf(ve)").forEach { s ->
                                    DropdownMenuItem(text = { Text(s, color = colors.textPrimary) }, onClick = { maritalStatus = s; maritalExpanded = false })
                                }
                            }
                        }
                    }
                    FormTextField("Nombre d'enfants", numberOfChildren, { numberOfChildren = it }, colors, keyboardType = KeyboardType.Number)
                }
            }

            // ADMINISTRATIVE IDENTIFIERS
            item {
                SectionHeader("Identifiants Administratifs", colors)
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    FormTextField("Matricule", matricule, { matricule = it }, colors)
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        FormTextField("N° CNI / Passeport", idNumber, { idNumber = it }, colors, Modifier.weight(1f))
                        FormTextField("N° Sécurité Sociale", socialSecurityNumber, { socialSecurityNumber = it }, colors, Modifier.weight(1f))
                    }
                }
            }

            // CONTACT SECTION
            item {
                SectionHeader("Contact", colors)
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        FormTextField("Email *", email, { email = it }, colors, Modifier.weight(1f), keyboardType = KeyboardType.Email, isError = showErrors && !isEmailValid)
                        FormTextField("Téléphone", phone, { phone = it }, colors, Modifier.weight(1f), keyboardType = KeyboardType.Phone)
                    }
                    FormTextField("Adresse Résidentielle", address, { address = it }, colors, icon = Icons.Default.LocationOn)
                }
            }

            // PROFESSIONAL INFO
            item {
                SectionHeader("Parcours Professionnel", colors)
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedRole.label,
                            onValueChange = {},
                            label = { Text("Rôle / Fonction") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            trailingIcon = { IconButton(onClick = { roleExpanded = true }) { Icon(Icons.Default.ArrowDropDown, null) } },
                            shape = RoundedCornerShape(12.dp),
                            colors = fieldColors
                        )
                        DropdownMenu(expanded = roleExpanded, onDismissRequest = { roleExpanded = false }, modifier = Modifier.fillMaxWidth(0.9f).background(colors.card)) {
                            StaffRole.entries.forEach { role ->
                                DropdownMenuItem(text = { Text(role.label, color = colors.textPrimary) }, onClick = { selectedRole = role; roleExpanded = false })
                            }
                        }
                    }

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedDepartment,
                            onValueChange = {},
                            label = { Text("Département") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            trailingIcon = { IconButton(onClick = { deptExpanded = true }) { Icon(Icons.Default.ArrowDropDown, null) } },
                            shape = RoundedCornerShape(12.dp),
                            colors = fieldColors
                        )
                        DropdownMenu(expanded = deptExpanded, onDismissRequest = { deptExpanded = false }, modifier = Modifier.fillMaxWidth(0.9f).background(colors.card)) {
                            listOf("Administration", "Vie Scolaire", "Secrétariat", "Comptabilité", "Scientifique", "Littéraire", "Sport", "Arts", "Service Technique").forEach { dept ->
                                DropdownMenuItem(text = { Text(dept, color = colors.textPrimary) }, onClick = { selectedDepartment = dept; deptExpanded = false })
                            }
                        }
                    }

                    if (selectedRole == StaffRole.TEACHER) {
                        FormTextField("Spécialité / Matière", specialty, { specialty = it }, colors, placeholder = "Ex: Mathématiques, Histoire...")
                    }
                    
                    FormTextField("Diplôme le plus élevé", highestDegree, { highestDegree = it }, colors)
                    FormTextField("Qualifications / Certifications", qualifications, { qualifications = it }, colors, placeholder = "Séparez par des virgules")
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        FormTextField("Années d'expérience", experienceYears, { experienceYears = it }, colors, Modifier.weight(1f), keyboardType = KeyboardType.Number)
                        FormTextField("Dernier établissement", previousInstitution, { previousInstitution = it }, colors, Modifier.weight(1f))
                    }
                    FormTextField("Région d'affectation", region, { region = it }, colors)
                }
            }
            
            // CONTRACT & FINANCE
            item {
                SectionHeader("Contrat & Situation Salariale", colors)
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = contractType,
                                onValueChange = {},
                                label = { Text("Type de Contrat") },
                                modifier = Modifier.fillMaxWidth(),
                                readOnly = true,
                                trailingIcon = { IconButton(onClick = { contractExpanded = true }) { Icon(Icons.Default.ArrowDropDown, null) } },
                                shape = RoundedCornerShape(12.dp),
                                colors = fieldColors
                            )
                            DropdownMenu(expanded = contractExpanded, onDismissRequest = { contractExpanded = false }, modifier = Modifier.fillMaxWidth(0.9f).background(colors.card)) {
                                listOf("CDI", "CDD", "Vacataire", "Stagiaire").forEach { s ->
                                    DropdownMenuItem(text = { Text(s, color = colors.textPrimary) }, onClick = { contractType = s; contractExpanded = false })
                                }
                            }
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = status,
                                onValueChange = {},
                                label = { Text("Statut Actuel") },
                                modifier = Modifier.fillMaxWidth(),
                                readOnly = true,
                                trailingIcon = { IconButton(onClick = { statusExpanded = true }) { Icon(Icons.Default.ArrowDropDown, null) } },
                                shape = RoundedCornerShape(12.dp),
                                colors = fieldColors
                            )
                            DropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }, modifier = Modifier.fillMaxWidth(0.9f).background(colors.card)) {
                                listOf("Actif", "En congé", "Inactif").forEach { s ->
                                    DropdownMenuItem(text = { Text(s, color = colors.textPrimary) }, onClick = { status = s; statusExpanded = false })
                                }
                            }
                        }
                    }
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        FormTextField("Salaire de Base", baseSalary, { baseSalary = it }, colors, Modifier.weight(1f), keyboardType = KeyboardType.Decimal, icon = Icons.Default.Payments)
                        FormTextField("Heures Lib. / Semaine", weeklyHours, { weeklyHours = it }, colors, Modifier.weight(1f), keyboardType = KeyboardType.Number, icon = Icons.Default.Schedule)
                    }
                    
                    FormTextField("Responsable Hiérarchique", supervisor, { supervisor = it }, colors, icon = Icons.Default.Person)
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        FormTextField("Date d'intégration", joinDate, { joinDate = it }, colors, Modifier.weight(1f), icon = Icons.Default.Login)
                        FormTextField("Date Retraite Prévue", retirementDate, { retirementDate = it }, colors, Modifier.weight(1f), icon = Icons.Default.Logout)
                    }
                }
            }
        }

        // Action Button
        Button(
            onClick = {
                if (isFirstNameValid && isLastNameValid && isEmailValid) {
                    val newStaff = Staff(
                        id = staff?.id ?: "STAFF_${Random.nextInt(1000, 9999)}",
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        phone = phone,
                        role = selectedRole,
                        department = selectedDepartment,
                        specialty = if (specialty.isNotEmpty()) specialty else null,
                        joinDate = joinDate,
                        matricule = matricule,
                        gender = gender,
                        status = status,
                        address = if (address.isNotEmpty()) address else null,
                        
                        // New fields
                        birthDate = if (birthDate.isNotEmpty()) birthDate else null,
                        birthPlace = if (birthPlace.isNotEmpty()) birthPlace else null,
                        nationality = nationality,
                        maritalStatus = maritalStatus,
                        numberOfChildren = numberOfChildren.toIntOrNull() ?: 0,
                        idNumber = if (idNumber.isNotEmpty()) idNumber else null,
                        socialSecurityNumber = if (socialSecurityNumber.isNotEmpty()) socialSecurityNumber else null,
                        qualifications = qualifications,
                        highestDegree = if (highestDegree.isNotEmpty()) highestDegree else null,
                        experienceYears = experienceYears.toIntOrNull() ?: 0,
                        previousInstitution = if (previousInstitution.isNotEmpty()) previousInstitution else null,
                        region = if (region.isNotEmpty()) region else null,
                        contractType = contractType,
                        baseSalary = baseSalary.toDoubleOrNull(),
                        weeklyHours = weeklyHours.toIntOrNull() ?: 0,
                        supervisor = if (supervisor.isNotEmpty()) supervisor else null,
                        retirementDate = if (retirementDate.isNotEmpty()) retirementDate else null
                    )
                    onSave(newStaff)
                } else {
                    showErrors = true
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colors.textLink, contentColor = Color.White)
        ) {
            Icon(Icons.Default.Save, contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))
            Text("Enregistrer le Personnel", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
private fun SectionHeader(title: String, colors: DashboardColors) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black, letterSpacing = 1.2.sp),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(colors.divider.copy(alpha = 0.5f)))
    }
}

@Composable
private fun FormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    colors: DashboardColors,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    icon: ImageVector? = null,
    isError: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { if (placeholder.isNotEmpty()) Text(placeholder) },
        modifier = modifier.fillMaxWidth(),
        isError = isError,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        trailingIcon = icon?.let { { Icon(it, null, modifier = Modifier.size(18.dp), tint = colors.textMuted) } },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = colors.textPrimary,
            unfocusedTextColor = colors.textPrimary,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = colors.divider.copy(alpha = if (colors.textPrimary == Color(0xFF1E293B)) 0.8f else 0.5f),
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = colors.textMuted,
            focusedPlaceholderColor = colors.textMuted,
            unfocusedPlaceholderColor = colors.textMuted.copy(alpha = 0.7f)
        )
    )
}

@Composable
private fun GenderOption(
    id: String,
    label: String,
    selected: Boolean,
    onSelect: (String) -> Unit,
    colors: DashboardColors
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onSelect(id) }) {
        RadioButton(selected = selected, onClick = { onSelect(id) }, colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary))
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, color = colors.textPrimary)
    }
}
