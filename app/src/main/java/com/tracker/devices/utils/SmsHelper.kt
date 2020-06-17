package com.tracker.devices.utils

import android.content.Context
import android.net.Uri
import com.tracker.devices.data.SmsLogs
import java.util.*
import kotlin.collections.ArrayList

object SmsHelper {

    fun requestSMS(context: Context): ArrayList<SmsLogs> {
        val smsList = ArrayList<SmsLogs>()

        val cursor = context.contentResolver.query(
            Uri.parse("content://sms/"),
            null,
            null,
            null,
            null
        )

        if (cursor!!.moveToFirst()) {
            val nameID = cursor.getColumnIndex("address")
            val messageID = cursor.getColumnIndex("body")
            val dateID = cursor.getColumnIndex("date")

            do {
                val dateString = cursor.getString(dateID)
                smsList.add(
                    SmsLogs(
                        cursor.getString(nameID),
                        Date(dateString.toLong()).toString(),
                        cursor.getString(messageID)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return smsList
    }
}