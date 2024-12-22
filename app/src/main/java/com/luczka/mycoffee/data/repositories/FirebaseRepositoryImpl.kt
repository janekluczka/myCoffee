package com.luczka.mycoffee.data.repositories

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.luczka.mycoffee.data.mappers.toModel
import com.luczka.mycoffee.data.remote.dto.MethodsDto
import com.luczka.mycoffee.data.remote.dto.RecipeDto
import com.luczka.mycoffee.domain.models.MethodModel
import com.luczka.mycoffee.domain.models.RecipeModel
import com.luczka.mycoffee.domain.repositories.FirebaseRepository
import kotlinx.coroutines.tasks.await

class FirebaseRepositoryImpl(
    context: Context,
    private val firebaseFirestore: FirebaseFirestore
) : FirebaseRepository {

    companion object {
        private const val TAG = "OnlineFirebaseRepository"

        private const val COLLECTION_RECIPES = "Recipes"
        private const val COLLECTION_CONFIG = "config"

        private const val DOCUMENT_METHODS = "methods"

        private const val FIELD_METHOD = "methodId"
        private const val FIELD_YOUTUBE_ID = "youtubeId"
    }

    private val localeCode: String = context.resources.configuration.locales[0].language

    override suspend fun getMethods(): Result<List<MethodModel>> {
        return try {
            val configDocumentSnapshot = firebaseFirestore
                .collection(COLLECTION_CONFIG)
                .document(DOCUMENT_METHODS)
                .get()
                .await()

            val methodsDto = configDocumentSnapshot.toObject<MethodsDto>()
            val methodDtoList = methodsDto?.list ?: emptyList()
            val methodModelList = methodDtoList
                .map { it.toModel(localeCode) }
                .sortedBy { it.name }

            Result.success(methodModelList)
        } catch (exception: Exception) {
            exception.message?.let { Log.d(TAG, it) }
            Result.failure(exception)
        }
    }

    override fun getRecipes(
        methodId: String,
        onSuccess: (List<RecipeModel>) -> Unit,
        onError: (String) -> Unit
    ) {
        firebaseFirestore
            .collection(COLLECTION_RECIPES)
            .whereEqualTo(FIELD_METHOD, methodId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.documents
                    .mapNotNull { documentSnapshot -> documentSnapshot.toObject<RecipeDto>() }
                    .map { recipeDto -> recipeDto.toModel() }
                    .let(onSuccess)
            }
            .addOnFailureListener { exception ->
                exception.message?.let { Log.d(TAG, it) }
                onError("Error: " + exception.message.toString())
            }
    }

    override fun getRecipe(
        youtubeId: String,
        onSuccess: (RecipeModel) -> Unit
    ) {
        firebaseFirestore
            .collection(COLLECTION_RECIPES)
            .whereEqualTo(FIELD_YOUTUBE_ID, youtubeId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.documents
                    .firstOrNull()
                    ?.toObject<RecipeDto>()
                    ?.toModel()
                    ?.let(onSuccess)
            }
            .addOnFailureListener { exception ->
                exception.message?.let { Log.d(TAG, it) }
            }
    }

}