package com.ecolix.data.services.templates

import com.ecolix.data.models.ReportCard

/**
 * Génère le HTML du bulletin scolaire
 * Template basé sur la structure de ReportCardView.kt
 */
object HtmlBulletinTemplate {
    
    fun generateHtml(reportCard: ReportCard): String {
        return """
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8" />
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Bulletin - ${reportCard.studentName}</title>
    <style>
        @page {
            size: A4;
            margin: 8mm;
        }
        
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'DejaVu Sans', 'Arial', 'Helvetica', sans-serif;
            font-size: 9.5px;
            color: #1f2937;
            background: white;
        }
        
        .container {
            max-width: 100%;
            padding: 0;
        }
        
        .card {
            background: white;
            border: 1px solid #e5e7eb;
            border-radius: 4px;
            padding: 8px;
            margin-bottom: 6px;
        }
        
        .header-table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 6px;
        }
        
        .header-table td {
            width: 33.33%;
            text-align: center;
            vertical-align: top;
            padding: 0 4px;
            border: none;
        }
        
        .header-table h3 {
            font-size: 8px;
            font-weight: bold;
            margin-bottom: 2px;
        }
        
        .header-table p {
            font-size: 6.5px;
            color: #6b7280;
        }
        
        .logo {
            width: 35px;
            height: 35px;
            background: #dbeafe;
            border-radius: 4px;
            margin: 0 auto 2px;
            text-align: center;
            line-height: 35px;
            font-size: 20px;
        }
        
        .info-item {
            margin-bottom: 1px;
        }
        
        .info-item label {
            display: block;
            font-size: 7.5px;
            color: #6b7280;
            margin-bottom: 0px;
        }
        
        .info-item value {
            display: block;
            font-size: 9px;
            font-weight: 600;
        }
        
        hr {
            border: none;
            border-top: 1px solid #e5e7eb;
            margin: 6px 0;
        }
        
        table {
            width: 100%;
            border-collapse: collapse;
            margin: 6px 0;
        }
        
        th {
            background: #dbeafe;
            padding: 4px 2px;
            text-align: center;
            font-size: 8.5px;
            font-weight: bold;
            border: 1px solid #e5e7eb;
        }
        
        td {
            padding: 3px 2px;
            text-align: center;
            font-size: 8.5px;
            border: 1px solid #e5e7eb;
        }
        
        .subject-name {
            text-align: left !important;
            font-weight: 600;
        }
        
        .professor-name {
            text-align: left !important;
            font-size: 7.5px;
            color: #6b7280;
        }
        
        .category-header {
            background: #f3f4f6;
            font-weight: bold;
            text-align: left;
            padding: 3px 5px;
            font-size: 8.5px;
        }
        
        .totals-row {
            background: #f9fafb;
            font-weight: bold;
        }
        
        .signature-label {
            font-weight: bold;
            font-size: 8.5px;
            margin-bottom: 2px;
        }
        
        .badge {
            display: inline-block;
            background: #f3f4f6;
            border: 1px solid #e5e7eb;
            border-radius: 4px;
            padding: 1px 5px;
            margin: 1px;
            font-size: 8.5px;
        }
        
        .text-center {
            text-align: center;
        }
        
        .text-muted {
            color: #6b7280;
        }
        
        .font-bold {
            font-weight: bold;
        }
        
        .mb-2 {
            margin-bottom: 3px;
        }

        .duplicate-badge {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%) rotate(-45deg);
            font-size: 80px;
            color: rgba(239, 68, 68, 0.15);
            font-weight: bold;
            border: 10px solid rgba(239, 68, 68, 0.15);
            padding: 20px;
            z-index: -1;
            pointer-events: none;
            white-space: nowrap;
        }
    </style>
</head>
<body>
    <div class="container" style="position: relative;">
        ${if (reportCard.isDuplicate) """<div class="duplicate-badge">DUPLICATA</div>""" else ""}
        <!-- En-tête -->
        <div class="card">
            <table class="header-table">
                <tr>
                    <td>
                        <h3>MINISTERE DES ENSEIGNEMENTS<br />PRIMAIRE, SECONDAIRE, TECHNIQUE</h3>
                        <p>---</p>
                        <h3>DRE-MARITIME</h3>
                        <p>---</p>
                        <h3>IESG-VOGAN</h3>
                    </td>
                    <td>
                        <div class="logo">🎓</div>
                        <h3>GROUPE SCOLAIRE ECOLIX</h3>
                        <p>BP : 1234 Lomé</p>
                        <p>Tel: 22 22 22 22</p>
                        <p style="font-style: italic; font-size: 8px;">"Discipline - Travail - Succès"</p>
                    </td>
                    <td style="vertical-align: middle;">
                        <h3>RÉPUBLIQUE TOGOLAISE</h3>
                        <p>Travail - Liberté - Patrie</p>
                        <p style="margin-top: 10px; font-weight: bold;">Année Scolaire<br />${reportCard.academicYear}</p>
                    </td>
                </tr>
            </table>
        </div>
        
        <!-- Informations Élève -->
        <div class="card">
            <table style="width: 100%; border: none;">
                <tr>
                    <td style="border: none; padding: 4px; width: 25%;">
                        <label>Classe:</label>
                        <value>${reportCard.className}</value>
                    </td>
                    <td style="border: none; padding: 4px; width: 25%;">
                        <label>Série:</label>
                        <value>${reportCard.serie}</value>
                    </td>
                    <td style="border: none; padding: 4px; width: 25%;">
                        <label>Effectif:</label>
                        <value>${reportCard.totalStudents}</value>
                    </td>
                    <td style="border: none; padding: 4px; width: 25%;">
                        <label>Période:</label>
                        <value>${reportCard.period}</value>
                    </td>
                </tr>
            </table>
            <hr/>
            <table style="width: 100%; border: none;">
                <tr>
                    <td style="border: none; padding: 4px; width: 20%;">
                        <label>Elève:</label>
                        <value>${reportCard.studentName}</value>
                    </td>
                    <td style="border: none; padding: 4px; width: 20%;">
                        <label>Matricule:</label>
                        <value>${reportCard.matricule}</value>
                    </td>
                    <td style="border: none; padding: 4px; width: 20%;">
                        <label>Statut:</label>
                        <value>${if (reportCard.isRepeater) "Redoublant" else "Nouveau"}</value>
                    </td>
                    <td style="border: none; padding: 4px; width: 20%;">
                        <label>Né(e) le/à:</label>
                        <value>${reportCard.dateOfBirth}</value>
                    </td>
                    <td style="border: none; padding: 4px; width: 20%;">
                        <label>Sexe:</label>
                        <value>${reportCard.sex}</value>
                    </td>
                </tr>
            </table>
        </div>
        
        <!-- Table des Notes -->
        <div class="card">
            <table>
                <thead>
                    <tr>
                        <th>Matières</th>
                        <th>Dev</th>
                        <th>Comp</th>
                        <th>Moy</th>
                        <th>Coef</th>
                        <th>Total</th>
                        <th>Min</th>
                        <th>Max</th>
                        <th>Rng</th>
                        <th>Appréciations</th>
                        <th>Professeurs</th>
                    </tr>
                </thead>
                <tbody>
                    ${generateSubjectsHtml(reportCard)}
                    ${generateTotalsRow(reportCard)}
                </tbody>
            </table>
        </div>
        
        <!-- Moyennes & Statistiques -->
        <table style="width: 100%; border: none; margin: 16px 0;">
            <tr>
                <td style="width: 50%; vertical-align: top; border: none; padding-right: 8px;">
                    <div class="card">
                        <h4 class="mb-2">I-Moyennes</h4>
                        <table style="margin: 0;">
                            ${reportCard.historyAverages.mapIndexed { index, avg -> 
                                val labels = listOf("1er Trimestre", "2e Trimestre", "3e Trimestre")
                                "<tr><td style=\"text-align: left;\">${labels[index]}</td><td style=\"font-weight: bold;\">${avg ?: "-"}</td></tr>"
                            }.joinToString("")}
                        </table>
                        <p class="text-muted" style="font-size: 9px; margin-top: 8px;">
                            Présence: 98% | Abs. J: ${reportCard.absJustifiees} | Abs. I: ${reportCard.absInjustifiees} | Ret: ${reportCard.retards}
                        </p>
                    </div>
                </td>
                <td style="width: 50%; vertical-align: top; border: none; padding-left: 8px;">
                    <div class="card">
                        <div style="margin-bottom: 8px;">
                            <span class="badge">Mention: ${reportCard.appreciationGenerale}</span>
                            <span class="badge">Rang: ${reportCard.rank}/${reportCard.totalStudents}</span>
                        </div>
                        <table style="margin: 0;">
                            <tr>
                                <td>Moy. forte</td>
                                <td class="font-bold">${reportCard.maxAverage}</td>
                                <td>Moy. faible</td>
                                <td class="font-bold">${reportCard.minAverage}</td>
                            </tr>
                            <tr>
                                <td>Moy. classe</td>
                                <td class="font-bold">${reportCard.classAverage}</td>
                                <td>Moy. Gen</td>
                                <td class="font-bold">${reportCard.generalAverage}</td>
                            </tr>
                            ${if (reportCard.annualAverage != null) """
                            <tr>
                                <td>Moy. Annuelle</td>
                                <td class="font-bold">${reportCard.annualAverage}</td>
                                <td>Rang Annuel</td>
                                <td class="font-bold">${reportCard.rank}/${reportCard.totalStudents}</td>
                            </tr>
                            """ else ""}
                        </table>
                    </div>
                </td>
            </tr>
        </table>
        
        <!-- Conseil -->
        <div class="card">
            <h4 class="mb-2">II-Appréciations du conseil des professeurs</h4>
            <p style="margin-bottom: 4px;">Travail: ${sanitizeUserInput(reportCard.travail)} | Conduite: ${sanitizeUserInput(reportCard.conduite)}</p>
            <p style="margin-bottom: 4px; font-size: 9px;">
                Félicitations: ${if (reportCard.tableauFelicitations) "OUI" else "NON"} | 
                Encouragements: ${if (reportCard.tableauEncouragement) "OUI" else "NON"} | 
                Tableau d'honneur: ${if (reportCard.tableauHonneur) "OUI" else "NON"}
            </p>
            <hr style="margin: 4px 0;"/>
            <p class="font-bold">III- Décision du conseil: ${sanitizeUserInput(reportCard.decision)}</p>
        </div>
        
        <!-- Signatures -->
        <div class="card">
            <p class="mb-2">Fait à Lomé, le ${getCurrentDate()}</p>
            <table style="width: 100%; border: none;">
                <tr>
                    <td style="width: 50%; border: 1px solid #e5e7eb; padding: 4px; text-align: center; vertical-align: top; min-height: 40px;">
                        <div class="signature-label">Le Titulaire</div>
                        <p class="text-muted" style="font-size: 8px; margin-top: 15px;">${reportCard.teacherName}</p>
                    </td>
                    <td style="width: 50%; border: 1px solid #e5e7eb; padding: 4px; text-align: center; vertical-align: top; min-height: 40px;">
                        <div class="signature-label">Le Chef d'Etablissement</div>
                        <p class="text-muted" style="font-size: 8px; margin-top: 15px;">${reportCard.directorName}</p>
                    </td>
                </tr>
            </table>
        </div>
        
        ${if (reportCard.nb.isNotEmpty()) """
        <p class="text-center text-muted" style="font-style: italic; margin-top: 4px;">
            ${sanitizeUserInput(reportCard.nb)}
        </p>
        """ else ""}
    </div>
</body>
</html>
        """.trimIndent()
    }
    
    private fun generateSubjectsHtml(reportCard: ReportCard): String {
        val groupedSubjects = reportCard.subjects.groupBy { it.category }
        return groupedSubjects.entries.joinToString("") { (category, subjects) ->
            val categoryHeader = if (category.isNotEmpty() && category != "Général") {
                """<tr><td colspan="11" class="category-header">${category.uppercase()}</td></tr>"""
            } else ""
            
            val rows = subjects.joinToString("") { subject ->
                """
                <tr>
                    <td class="subject-name">${subject.name}</td>
                    <td>${subject.devoir ?: "-"}</td>
                    <td>${subject.composition ?: "-"}</td>
                    <td class="font-bold">${subject.average}</td>
                    <td>${subject.coefficient.toInt()}</td>
                    <td>${subject.total}</td>
                    <td class="text-muted">${subject.minAverage}</td>
                    <td class="text-muted">${subject.maxAverage}</td>
                    <td class="font-bold">${subject.rank}</td>
                    <td>${sanitizeUserInput(subject.appreciation)}</td>
                    <td class="professor-name">${sanitizeUserInput(subject.professor)}</td>
                </tr>
                """.trimIndent()
            }
            
            categoryHeader + rows
        }
    }
    
    private fun generateTotalsRow(reportCard: ReportCard): String {
        val totalCoef = reportCard.subjects.sumOf { it.coefficient.toDouble() }.toInt()
        val totalPoints = reportCard.subjects.sumOf { it.total.toDouble() }
        
        return """
            <tr class="totals-row">
                <td colspan="4" class="font-bold">TOTAUX</td>
                <td class="font-bold">$totalCoef</td>
                <td class="font-bold">$totalPoints</td>
                <td colspan="5"></td>
            </tr>
        """.trimIndent()
    }
    
    private fun sanitizeUserInput(text: String): String {
        // Fix malformed br tags and ensure XHTML compliance
        return text
            .replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), "<br/>")
            .replace(Regex("</br>", RegexOption.IGNORE_CASE), "")
            .replace(Regex("<hr\\s*/?>", RegexOption.IGNORE_CASE), "<hr/>")
            .replace(Regex("</hr>", RegexOption.IGNORE_CASE), "")
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            // Re-allow br and hr tags
            .replace("&lt;br/&gt;", "<br/>")
            .replace("&lt;hr/&gt;", "<hr/>")
    }
    
    private fun getCurrentDate(): String {
        // Simple date formatting - you can use kotlinx-datetime for better formatting
        return "24/01/2026"
    }
}
