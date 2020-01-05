package com.mramirid.firebaseuploadexample

import com.google.firebase.database.Exclude
import java.io.Serializable

/*
* Kelas model untuk file yang kita upload
* */
data class Upload(
    var name: String = "No name",
    var imageUrl: String = "No url",

    // Exclude agar property key ini diabaikan oleh firebase
    @get:Exclude
    @set:Exclude
    var key: String? = null

) : Serializable