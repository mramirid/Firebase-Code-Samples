package com.mramirid.firestoreexampleproject

import com.google.firebase.firestore.Exclude

data class Note(
    val title: String = "",
    val description: String = "",

    // Exclude karena kita tidak ingin properti ini dimasukan ke database
    @get:Exclude
    var documentId: String? = null
)