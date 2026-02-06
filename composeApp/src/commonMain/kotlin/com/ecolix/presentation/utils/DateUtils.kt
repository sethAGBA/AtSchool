package com.ecolix.presentation.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.Clock

object DateUtils {
    fun validateDate(input: String): String? {
        if (input.isBlank()) return null
        val parts = input.trim().split(Regex("[\\-\\s/]+"))
        if (parts.size != 3) return "Format: JJ/MM/AAAA"
        
        // Support both YYYY-MM-DD and DD/MM/YYYY
        val (year, month, day) = if (parts[0].length == 4) {
            Triple(parts[0].toIntOrNull(), parts[1].toIntOrNull(), parts[2].toIntOrNull())
        } else {
            Triple(parts[2].toIntOrNull(), parts[1].toIntOrNull(), parts[0].toIntOrNull())
        }

        if (year == null || month == null || day == null) return "Date invalide"
        if (month !in 1..12) return "Mois (1-12)"
        if (year < 1900 || year > 2100) return "Année (1900-2100)"
        
        val maxDays = when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) 29 else 28
            else -> 0
        }
        
        if (day !in 1..maxDays) return "Jour (1-$maxDays)"
        
        return null
    }

    fun normalizeDate(input: String): String {
        val parts = input.trim().split(Regex("[\\-\\s/]+"))
        if (parts.size != 3) return input
        
        // If it starts with a year, assume it's already semi-normalized
        return if (parts[0].length == 4) {
            "${parts[0]}-${parts[1].padStart(2, '0')}-${parts[2].padStart(2, '0')}"
        } else {
            // Assume DD/MM/YYYY and convert to YYYY-MM-DD
            "${parts[2]}-${parts[1].padStart(2, '0')}-${parts[0].padStart(2, '0')}"
        }
    }

    fun getCurrentDateFormatted(): String {
        val now = Clock.System.now()
        val date = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
        return "${date.dayOfMonth.toString().padStart(2, '0')}/${date.monthNumber.toString().padStart(2, '0')}/${date.year}"
    }

    fun validateDateRange(start: String, end: String): String? {
        val s = try { LocalDate.parse(start) } catch (e: Exception) { return null }
        val e = try { LocalDate.parse(end) } catch (e: Exception) { return null }
        
        if (e <= s) return "La date de fin doit être après la date de début"
        return null
    }
}
