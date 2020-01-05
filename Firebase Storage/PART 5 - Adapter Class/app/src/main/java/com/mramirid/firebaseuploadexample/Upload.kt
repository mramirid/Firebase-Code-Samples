package com.mramirid.firebaseuploadexample

import java.io.Serializable

/*
* Kelas model untuk file yang kita upload
* */
data class Upload(
    var name: String = "No name",
    var imageUrl: String = "No url"
) : Serializable
