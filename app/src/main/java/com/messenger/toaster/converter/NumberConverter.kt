package com.messenger.toaster.converter

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

fun formatNumber(number: Int): String {
    return when {
        number in 1000..999999 -> {
            val kValue = number / 1000
            "${kValue}K"
        }
        number >= 1000000 -> {
            val mValue = number / 1000000
            "${mValue}M"
        }
        else -> {
            number.toString()
        }
    }
}
fun monthsBetweenUnixTimes(startTime: Long, endTime: Long): Long {
    val startInstant = Instant.ofEpochMilli(startTime)
    val endInstant = Instant.ofEpochMilli(endTime)

    // Преобразование в LocalDate или LocalDateTime
    val startDate = LocalDateTime.ofInstant(startInstant, ZoneOffset.UTC)
    val endDate = LocalDateTime.ofInstant(endInstant, ZoneOffset.UTC)

    // Вычисление разницы в месяцах
    return ChronoUnit.MONTHS.between(startDate, endDate)
}