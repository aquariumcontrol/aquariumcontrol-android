package com.example.aquariumcontroller

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.aquariumcontroller.serverprofile.ControllerProfileDatabase
import java.io.Serializable

@Entity(tableName = ControllerProfileDatabase.TABLE_NAME_CONTROLLER_LIST)
data class ControllerProfile(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0,

    @ColumnInfo(name = "name")
    var controllerProfileName: String = "null",

    @ColumnInfo(name = "address")
    var controllerAddress: String = "null",

    @ColumnInfo(name = "user")
    var username: String = "null",

    @ColumnInfo(name = "password")
    var password: String = "null",

    @ColumnInfo(name = "isdefault")
    var isDefaultControllerProfile: Boolean? = null
) : Serializable {

    @Ignore
    private var userCredentialsVerified = false

    fun isUserCredentialsVerified(): Boolean {
        return userCredentialsVerified
    }

    fun setUserCredentialsVerified(userCredentialsVerified: Boolean) {
        this.userCredentialsVerified = userCredentialsVerified
    }
}