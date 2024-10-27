package com.app.softwarehousemanager.constkeys

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

object CurrentDateAndTime {


    fun getFormattedDateTime(): String {
        val currentDateTime = Date()  // Get current date and time
        val dateFormat = SimpleDateFormat("dd-MM-yyyy, hh:mm:ss a", Locale.US)
        return dateFormat.format(currentDateTime)  // Format the date and time
    }
}