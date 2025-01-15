package com.example.aquariumcontroller.refill

import android.annotation.SuppressLint
import androidx.annotation.VisibleForTesting
import org.json.JSONException
import org.json.JSONObject
import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat

class RefillEvent {

    var timestamp: Timestamp? = null
    var duration: Int? = null
    var volume: Double? = null

    constructor(jsonObject: JSONObject?) {
        if (jsonObject != null) {
            try {
                timestamp = Timestamp.valueOf(jsonObject.getString("Timestamp"))
                duration = jsonObject.getInt("Duration")
                volume = jsonObject.getDouble("Volume")
                //int errorCode = jsonObject.getInt("errorCode");
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    @VisibleForTesting
    constructor(timestamp: Timestamp, duration: Int, volume: Double) {
        this.timestamp = timestamp
        this.duration = duration
        this.volume = volume
    }

    @SuppressLint("SimpleDateFormat")
    fun getTimestamp(): String {
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return df.format(timestamp)
    }

    fun getDuration(): String {
        return "$duration"
    }

    fun getVolume(): String {
        return "%.2f".format(volume)
    }
}