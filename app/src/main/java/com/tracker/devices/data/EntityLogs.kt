package com.tracker.devices.data

import android.location.Location

data class EntityLogs(var location: Location?, var sms: ArrayList<SmsLogs>?, var wifi: String?, var callLogs: ArrayList<CallLogs>?)