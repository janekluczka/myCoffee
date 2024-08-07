package com.luczka.mycoffee.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.luczka.mycoffee.model.Method
import com.luczka.mycoffee.model.Recipe

class OnlineFirebaseRepository : FirebaseRepository {

    companion object {
        private const val TAG = "OnlineFirebaseRepository"
        private const val COLLECTION_METHODS = "Methods"
        private const val COLLECTION_RECIPES = "Recipes"
        private const val FIELD_METHOD = "methodId"
        private const val FIELD_YOUTUBE_ID = "youtubeId"
    }

    private val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance(Firebase.app)

    override fun getMethods(
        onSuccess: (List<Method>) -> Unit,
        onError: (String) -> Unit
    ) {
        firebaseFirestore.collection(COLLECTION_METHODS)
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.documents
                    .mapNotNull { it.toObject<Method>()?.copy(id = it.id) }
                    .let(onSuccess)
            }
            .addOnFailureListener { exception ->
                exception.message?.let { Log.d(TAG, it) }
                onError("Error: " + exception.message.toString())
            }
    }

    override fun getRecipes(
        methodId: String,
        onSuccess: (List<Recipe>) -> Unit,
        onError: (String) -> Unit
    ) {
        firebaseFirestore.collection(COLLECTION_RECIPES)
            .whereEqualTo(FIELD_METHOD, methodId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.documents
                    .mapNotNull { it.toObject<Recipe>() }
                    .let(onSuccess)
            }
            .addOnFailureListener { exception ->
                exception.message?.let { Log.d(TAG, it) }
                onError("Error: " + exception.message.toString())
            }
    }

    override fun getRecipe(youtubeId: String, onSuccess: (Recipe) -> Unit) {
        firebaseFirestore.collection(COLLECTION_RECIPES)
            .whereEqualTo(FIELD_YOUTUBE_ID, youtubeId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.documents.firstOrNull()?.toObject<Recipe>()?.let(onSuccess)
            }
            .addOnFailureListener { exception ->
                exception.message?.let { Log.d(TAG, it) }
            }
    }


}