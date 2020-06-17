package com.tracker.devices.utils

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.provider.CallLog
import com.tracker.devices.data.CallLogs

object CallLogsHelper {

    @SuppressLint("MissingPermission")
    fun getCallLogs(context: Context): ArrayList<CallLogs> {
        val callLogs = ArrayList<CallLogs>()

        val cursor: Cursor? = context.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null, null, null, CallLog.Calls.DATE + " DESC"
        )

        if (cursor != null) {
            val number = cursor.getColumnIndex(CallLog.Calls.NUMBER)
            val type = cursor.getColumnIndex(CallLog.Calls.TYPE)
            val date = cursor.getColumnIndex(CallLog.Calls.DATE)
            val duration = cursor.getColumnIndex(CallLog.Calls.DURATION)

            while (cursor.moveToNext()) {
                val phNumber = cursor.getString(number)
                val callType = cursor.getString(type)
                val callDate = cursor.getString(date)
                val callDuration = cursor.getString(duration)

                var dir: String? = null
                val dircode = callType.toInt()

                when (dircode) {
                    CallLog.Calls.OUTGOING_TYPE -> dir = "OUTGOING"
                    CallLog.Calls.INCOMING_TYPE -> dir = "INCOMING"
                    CallLog.Calls.MISSED_TYPE -> dir = "MISSED"
                }
                callLogs.add(
                    CallLogs(
                        phNumber,
                        dir,
                        callDate,
                        callDuration
                    )
                )
            }
            cursor.close()
        }
        return callLogs
    }
}