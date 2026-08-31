package com.sitevisit.app.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Formatters {
    private val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    fun currency(amount: Double): String = currencyFormat.format(amount)
    fun date(millis: Long): String = dateFormat.format(Date(millis))
    fun dateTime(millis: Long): String = dateTimeFormat.format(Date(millis))
    fun time(millis: Long): String = timeFormat.format(Date(millis))
}
