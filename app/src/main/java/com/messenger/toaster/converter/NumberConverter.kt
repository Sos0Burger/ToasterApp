package com.messenger.toaster.converter

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