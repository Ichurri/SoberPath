package com.santiago.soberpath.presentation.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

/**
 * Formateo de fechas dependiente del idioma activo.
 *
 * Se usa [Locale.getDefault] (que [com.santiago.soberpath.util.LocaleHelper] mantiene
 * sincronizado con el idioma elegido en la app) para que el orden y los nombres de mes
 * cambien entre español e inglés. Los formatters se construyen en cada llamada a propósito,
 * para reflejar un cambio de idioma sin reiniciar el proceso.
 */
object DateFormatters {

    private val mediumFormatter: DateTimeFormatter
        get() = DateTimeFormatter
            .ofLocalizedDate(FormatStyle.MEDIUM)
            .withLocale(Locale.getDefault())

    private val shortFormatter: DateTimeFormatter
        get() = DateTimeFormatter
            .ofLocalizedDate(FormatStyle.SHORT)
            .withLocale(Locale.getDefault())

    /** Ej. ES: "9 jun 2026" · EN: "Jun 9, 2026". */
    fun mediumDate(date: LocalDate): String = date.format(mediumFormatter)

    fun mediumDate(dateTime: LocalDateTime): String = mediumDate(dateTime.toLocalDate())

    /** Ej. ES: "9/6/26" · EN: "6/9/26". */
    fun shortDate(date: LocalDate): String = date.format(shortFormatter)
}
