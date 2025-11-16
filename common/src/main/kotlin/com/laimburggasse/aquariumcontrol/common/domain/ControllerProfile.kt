package com.laimburggasse.aquariumcontrol.common.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.laimburggasse.aquariumcontrol.common.data.ControllerProfileDatabase
import java.io.Serializable

@Entity(tableName = ControllerProfileDatabase.Companion.TABLE_NAME_CONTROLLER_LIST)
data class ControllerProfile(
    // This class represents a controller profile.
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0,

    @ColumnInfo(name = "name")
    var name: String = "null",

    @ColumnInfo(name = "address")
    var address: String = "null",

    @ColumnInfo(name = "port")
    var port: Int = 80,

    @ColumnInfo(name = "user")
    var user: String = "null",

    @ColumnInfo(name = "password")
    var password: String = "null",
) : Serializable