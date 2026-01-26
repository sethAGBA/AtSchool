package com.ecolix.presentation.screens.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecolix.data.models.DashboardColors
import com.ecolix.data.models.Staff
import com.ecolix.presentation.components.CardContainer
import com.ecolix.presentation.components.TagPill

@Composable
fun StaffProfileScreen(
    staff: Staff,
    colors: DashboardColors,
    isCompact: Boolean = false,
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
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
                text = "Profil Personnel",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onEdit,
                colors = ButtonDefaults.buttonColors(containerColor = colors.textLink, contentColor = Color.White),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                if (!isCompact) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Modifier")
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Main Info Card
            item {
                CardContainer(containerColor = colors.card) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(if (isCompact) 80.dp else 100.dp)
                                .clip(CircleShape)
                                .background(staff.role.color.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                staff.role.icon, 
                                contentDescription = null, 
                                modifier = Modifier.size(if (isCompact) 40.dp else 50.dp), 
                                tint = staff.role.color
                            )
                        }
                        
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${staff.firstName} ${staff.lastName}",
                                style = if (isCompact) MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold) else MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = colors.textPrimary
                            )
                            Text(
                                text = "${staff.role.label} • ${staff.department}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.textMuted
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TagPill(staff.status, if (staff.status == "Actif") Color(0xFF10B981) else Color(0xFFF59E0B))
                                TagPill(staff.contractType ?: "CDI", Color(0xFF6366F1))
                            }
                        }
                    }
                }
            }

            // Identity & Personal Info
            item {
                CardContainer(containerColor = colors.card) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Identité & État Civil", fontWeight = FontWeight.Bold, color = colors.textPrimary)
                        HorizontalDivider(color = colors.divider)
                        
                        DetailRow(Icons.Default.Badge, "Matricule", staff.matricule ?: "N/A", colors)
                        DetailRow(Icons.Default.Fingerprint, "N° CNI / Passeport", staff.idNumber ?: "N/A", colors)
                        DetailRow(if (staff.gender == "M") Icons.Default.Male else Icons.Default.Female, "Sexe", if (staff.gender == "M") "Masculin" else "Féminin", colors)
                        DetailRow(Icons.Default.CalendarToday, "Date de Naissance", staff.birthDate ?: "N/A", colors)
                        DetailRow(Icons.Default.Place, "Lieu de Naissance", staff.birthPlace ?: "N/A", colors)
                        DetailRow(Icons.Default.Public, "Nationalité", staff.nationality ?: "Ivoirienne", colors)
                        DetailRow(Icons.Default.FamilyRestroom, "État Civil", staff.maritalStatus ?: "Célibataire", colors)
                        DetailRow(Icons.Default.ChildCare, "Nombre d'enfants", staff.numberOfChildren.toString(), colors)
                    }
                }
            }

            // Contact Info
            item {
                CardContainer(containerColor = colors.card) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Contact", fontWeight = FontWeight.Bold, color = colors.textPrimary)
                        HorizontalDivider(color = colors.divider)
                        
                        DetailRow(Icons.Default.Email, "Email", staff.email, colors)
                        DetailRow(Icons.Default.Phone, "Téléphone", staff.phone, colors)
                        DetailRow(Icons.Default.LocationOn, "Adresse", staff.address ?: "Non renseignée", colors)
                        DetailRow(Icons.Default.HealthAndSafety, "N° Sécurité Sociale", staff.socialSecurityNumber ?: "N/A", colors)
                    }
                }
            }

            // Professional Path
            item {
                CardContainer(containerColor = colors.card) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Parcours Professionnel", fontWeight = FontWeight.Bold, color = colors.textPrimary)
                        HorizontalDivider(color = colors.divider)
                        
                        DetailRow(Icons.Default.School, "Dernier Diplôme", staff.highestDegree ?: "N/A", colors)
                        DetailRow(Icons.Default.Description, "Qualifications", staff.qualifications, colors)
                        DetailRow(Icons.Default.Timeline, "Années d'Expérience", "${staff.experienceYears} ans", colors)
                        DetailRow(Icons.Default.History, "Dernier Établissement", staff.previousInstitution ?: "N/A", colors)
                        DetailRow(Icons.Default.Map, "Région d'Affectation", staff.region ?: "N/A", colors)
                    }
                }
            }

            // Contract & Finance
            item {
                CardContainer(containerColor = colors.card) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Contrat & Situation Salariale", fontWeight = FontWeight.Bold, color = colors.textPrimary)
                        HorizontalDivider(color = colors.divider)
                        
                        DetailRow(Icons.AutoMirrored.Filled.Login, "Date d'Intégration", staff.joinDate, colors)
                        DetailRow(Icons.Filled.HistoryEdu, "Type de Contrat", staff.contractType ?: "N/A", colors)
                        DetailRow(Icons.Filled.Payments, "Salaire de Base", staff.baseSalary?.let { "$it FCFA" } ?: "Non renseigné", colors)
                        DetailRow(Icons.Filled.Schedule, "Heures Lib. / Semaine", "${staff.weeklyHours}h", colors)
                        DetailRow(Icons.Filled.Person, "Responsable Hiérarchique", staff.supervisor ?: "N/A", colors)
                        DetailRow(Icons.AutoMirrored.Filled.Logout, "Date Retraite Prévue", staff.retirementDate ?: "N/A", colors)
                    }
                }
            }

            // Assigned Classes (if teacher)
            if (staff.role == com.ecolix.data.models.StaffRole.TEACHER && staff.assignedClasses.isNotEmpty()) {
                item {
                    CardContainer(containerColor = colors.card) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("Classes Assignées", fontWeight = FontWeight.Bold, color = colors.textPrimary)
                            HorizontalDivider(color = colors.divider)
                            
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                staff.assignedClasses.forEach { className ->
                                    Surface(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    ) {
                                        Text(
                                            text = className,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.primary
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
}

@Composable
private fun DetailRow(icon: ImageVector, label: String, value: String, colors: DashboardColors) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = colors.textMuted)
        Spacer(modifier = Modifier.width(12.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium, color = colors.textMuted, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = colors.textPrimary)
    }
}
