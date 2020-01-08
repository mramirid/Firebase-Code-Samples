package com.mramirid.firestoreexampleproject

import com.google.firebase.firestore.Exclude

data class Note(
    val title: String? = null,
    val description: String? = null,
    val priority: Int? = null,
    val tags: Map<String, Boolean>? = null, // Sebagai nested objects di Firestore

    // Exclude karena kita tidak ingin properti ini dimasukan ke database
    @get:Exclude
    var documentId: String? = null
)