package com.ecolix.atschool.ui.students.components

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
import com.ecolix.atschool.ui.dashboard.models.DashboardColors
import com.ecolix.atschool.ui.students.models.Student
import com.ecolix.atschool.ui.students.models.StudentDocument
import kotlin.random.Random
import kotlinx.datetime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentForm(
    student: Student? = null,
    classrooms: List<String>,
    currentAcademicYear: String,
    colors: DashboardColors,
    onBack: () -> Unit,
    onSave: (Student) -> Unit
) {
    // Helper to generate matricule
    fun generateMatricule() = (1..6).joinToString("") { Random.nextInt(10).toString() }

    // Form State
    var firstName by remember { mutableStateOf(student?.firstName ?: "") }
    var lastName by remember { mutableStateOf(student?.lastName ?: "") }
    var matricule by remember { mutableStateOf(student?.matricule ?: generateMatricule()) }
    var dateOfBirth by remember { mutableStateOf(student?.dateOfBirth ?: "") }
    var placeOfBirth by remember { mutableStateOf(student?.placeOfBirth ?: "") }
    var address by remember { mutableStateOf(student?.address ?: "") }
    var nationality by remember { mutableStateOf(student?.nationality ?: "") }
    var gender by remember { mutableStateOf(student?.gender ?: "M") }
    
    var contactNumber by remember { mutableStateOf(student?.contactNumber ?: "") }
    var email by remember { mutableStateOf(student?.email ?: "") }
    var emergencyContact by remember { mutableStateOf(student?.emergencyContact ?: "") }
    
    var guardianName by remember { mutableStateOf(student?.guardianName ?: "") }
    var guardianContact by remember { mutableStateOf(student?.guardianContact ?: "") }
    
    var selectedClassroom by remember { mutableStateOf(student?.classroom ?: classrooms.firstOrNull() ?: "") }
    var enrollmentDate by remember { mutableStateOf(student?.enrollmentDate ?: "24/01/2026") }
    var statusLabel by remember { mutableStateOf(student?.status ?: "Nouveau") }
    
    var medicalInfo by remember { mutableStateOf(student?.medicalInfo ?: "") }
    var bloodGroup by remember { mutableStateOf(student?.bloodGroup ?: "") }
    var remarks by remember { mutableStateOf(student?.remarks ?: "") }
    var photoUrl by remember { mutableStateOf(student?.photoUrl) }
    var documents by remember { mutableStateOf(student?.documents ?: emptyList()) }

    // Date Picker States
    var showDobPicker by remember { mutableStateOf(false) }
    var showEnrollmentPicker by remember { mutableStateOf(false) }

    // Validation State
    var showErrors by remember { mutableStateOf(false) }
    val isFirstNameValid = firstName.isNotBlank()
    val isLastNameValid = lastName.isNotBlank()
    val isDateOfBirthValid = dateOfBirth.isNotBlank()

    if (showDobPicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDobPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val instant = Instant.fromEpochMilliseconds(it)
                        val date = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
                        dateOfBirth = "${date.dayOfMonth.toString().padStart(2, '0')}/${date.monthNumber.toString().padStart(2, '0')}/${date.year}"
                    }
                    showDobPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDobPicker = false }) { Text("Annuler") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showEnrollmentPicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showEnrollmentPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val instant = Instant.fromEpochMilliseconds(it)
                        val date = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
                        enrollmentDate = "${date.dayOfMonth.toString().padStart(2, '0')}/${date.monthNumber.toString().padStart(2, '0')}/${date.year}"
                    }
                    showEnrollmentPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showEnrollmentPicker = false }) { Text("Annuler") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colors.textPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (student == null) "Nouvelle Inscription" else "Modifier: ${student.firstName} ${student.lastName}",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(28.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // PHOTO SECTION
            item {
                SectionHeader("Photo de l'Eleve", colors)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.card)
                        .border(1.dp, colors.divider, RoundedCornerShape(12.dp))
                        .clickable { /* Pick photo */ },
                    contentAlignment = Alignment.Center
                ) {
                    if (photoUrl == null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = colors.textMuted, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Ajouter une photo", color = colors.textMuted, style = MaterialTheme.typography.labelMedium)
                        }
                    } else {
                        Text("Photo selected", color = colors.textPrimary)
                    }
                }
            }

            // IDENTITY SECTION
            item {
                SectionHeader("Identite & Etat Civil", colors)
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        // SWAPPED: Nom comes before Prenom
                        FormTextField("Nom *", lastName, { lastName = it }, colors, Modifier.weight(1f), isError = showErrors && !isLastNameValid)
                        FormTextField("Prenom(s) *", firstName, { firstName = it }, colors, Modifier.weight(1f), isError = showErrors && !isFirstNameValid)
                    }
                    FormTextField("ID / Matricule (Auto)", matricule, { matricule = it }, colors)
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        FormTextField(
                            label = "Date de Naissance *", 
                            value = dateOfBirth, 
                            onValueChange = { }, 
                            colors = colors, 
                            modifier = Modifier.weight(1f).clickable { showDobPicker = true }, 
                            icon = Icons.Default.CalendarToday, 
                            isError = showErrors && !isDateOfBirthValid,
                            readOnly = true,
                            onIconClick = { showDobPicker = true }
                        )
                        FormTextField("Lieu de Naissance", placeOfBirth, { placeOfBirth = it }, colors, Modifier.weight(1f))
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        FormTextField("Nationalite", nationality, { nationality = it }, colors, Modifier.weight(1f))
                        FormTextField("Groupe Sanguin", bloodGroup, { bloodGroup = it }, colors, Modifier.weight(1f), placeholder = "Ex: O+, A-...")
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                        Text("Sexe: ", color = colors.textPrimary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(16.dp))
                        GenderOption("M", "Masculin", gender == "M", { gender = it }, colors)
                        Spacer(modifier = Modifier.width(24.dp))
                        GenderOption("F", "Feminin", gender == "F", { gender = it }, colors)
                    }
                }
            }

            // CONTACT & GUARDIAN
            item {
                SectionHeader("Contacts & Tuteur", colors)
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    FormTextField("Adresse de residence", address, { address = it }, colors)
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        FormTextField("Telephone", contactNumber, { contactNumber = it }, colors, Modifier.weight(1f), keyboardType = KeyboardType.Phone)
                        FormTextField("Email", email, { email = it }, colors, Modifier.weight(1f), keyboardType = KeyboardType.Email)
                    }
                    FormTextField("Nom complet du Tuteur", guardianName, { guardianName = it }, colors)
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        FormTextField("Contact Tuteur", guardianContact, { guardianContact = it }, colors, Modifier.weight(1f), keyboardType = KeyboardType.Phone)
                        FormTextField("Contact Urgence", emergencyContact, { emergencyContact = it }, colors, Modifier.weight(1f), keyboardType = KeyboardType.Phone)
                    }
                }
            }

            // ACADEMIC SECTION
            item {
                SectionHeader("Inscription Academique", colors)
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    FormClassDropdown(selectedClassroom, classrooms, { selectedClassroom = it }, colors)
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        FormTextField("Annee Scolaire", currentAcademicYear, {}, colors, Modifier.weight(1f), readOnly = true)
                        FormTextField(
                            label = "Date d'Inscription", 
                            value = enrollmentDate, 
                            onValueChange = { }, 
                            colors = colors, 
                            modifier = Modifier.weight(1f).clickable { showEnrollmentPicker = true }, 
                            icon = Icons.Default.Today,
                            readOnly = true,
                            onIconClick = { showEnrollmentPicker = true }
                        )
                    }
                    FormTextField("Statut Inscription", statusLabel, { statusLabel = it }, colors, placeholder = "Ex: Nouveau, Redoublant, Transfert...")
                }
            }

            // MEDICAL SECTION
            item {
                SectionHeader("Informations Médicales", colors)
                FormTextField("Informations medicales / Allergies", medicalInfo, { medicalInfo = it }, colors, lines = 3)
            }

            // REMARKS SECTION
            item {
                SectionHeader("Observations Générales", colors)
                FormTextField("Remarques / Appréciations", remarks, { remarks = it }, colors, lines = 3)
            }

            // DOCUMENTS SECTION
            item {
                SectionHeader("Pieces Jointes", colors)
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.background,
                                contentColor = colors.textPrimary
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, colors.divider),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(18.dp), tint = colors.textPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Joindre Fichier", color = colors.textPrimary)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("${documents.size} document(s) joint(s)", color = colors.textMuted, style = MaterialTheme.typography.labelMedium)
                    }
                    if (documents.isEmpty()) {
                        Text(
                            "Aucun document joint. Formats supportes: PDF, JPG, PNG.",
                            color = colors.textMuted,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }

        // Bottom Actions
        Button(
            onClick = { 
                if (isFirstNameValid && isLastNameValid && isDateOfBirthValid) {
                    val newStudent = Student(
                        id = student?.id ?: "STU_${Random.nextInt(1000, 9999)}",
                        firstName = firstName,
                        lastName = lastName,
                        gender = gender,
                        classroom = selectedClassroom,
                        academicYear = currentAcademicYear,
                        enrollmentDate = enrollmentDate,
                        status = statusLabel,
                        matricule = matricule,
                        dateOfBirth = dateOfBirth,
                        placeOfBirth = placeOfBirth,
                        address = address,
                        contactNumber = contactNumber,
                        email = email,
                        emergencyContact = emergencyContact,
                        guardianName = guardianName,
                        guardianContact = guardianContact,
                        medicalInfo = if (medicalInfo.isNotEmpty()) medicalInfo else null,
                        bloodGroup = if (bloodGroup.isNotEmpty()) bloodGroup else null,
                        remarks = if (remarks.isNotEmpty()) remarks else null,
                        nationality = if (nationality.isNotEmpty()) nationality else null,
                        photoUrl = photoUrl,
                        documents = documents
                    )
                    onSave(newStudent)
                } else {
                    showErrors = true
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.textLink,
                contentColor = Color.White
            )
        ) {
            Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(12.dp))
            Text("Valider l'Inscription", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
        }
    }
}

@Composable
private fun SectionHeader(title: String, colors: DashboardColors) {
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
private fun FormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    colors: DashboardColors,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    icon: ImageVector? = null,
    readOnly: Boolean = false,
    isError: Boolean = false,
    lines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
    onIconClick: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { if (placeholder.isNotEmpty()) Text(placeholder) },
        modifier = modifier.fillMaxWidth(),
        readOnly = readOnly,
        isError = isError,
        maxLines = lines,
        minLines = lines,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        trailingIcon = icon?.let { 
            { 
               IconButton(onClick = { onIconClick?.invoke() }, enabled = onIconClick != null) {
                   Icon(it, contentDescription = null, modifier = Modifier.size(18.dp), tint = if (isError) MaterialTheme.colorScheme.error else colors.textMuted) 
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
}

@Composable
private fun FormClassDropdown(
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    colors: DashboardColors
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            label = { Text("Classe Affectee") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = { 
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = colors.textMuted) 
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = colors.textPrimary,
                unfocusedTextColor = colors.textPrimary,
                unfocusedBorderColor = colors.divider
            )
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f).background(colors.card)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = colors.textPrimary) },
                    onClick = { onSelect(option); expanded = false }
                )
            }
        }
    }
}

@Composable
private fun GenderOption(
    id: String,
    label: String,
    selected: Boolean,
    onSelect: (String) -> Unit,
    colors: DashboardColors
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onSelect(id) }
    ) {
        RadioButton(
            selected = selected, 
            onClick = { onSelect(id) },
            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, color = if (selected) MaterialTheme.colorScheme.primary else colors.textPrimary, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
    }
}
