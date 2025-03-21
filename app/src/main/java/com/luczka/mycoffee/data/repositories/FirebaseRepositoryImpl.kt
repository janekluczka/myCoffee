package com.luczka.mycoffee.data.repositories

import android.content.Context
import android.util.Log
import com.luczka.mycoffee.data.mappers.toModel
import com.luczka.mycoffee.data.remote.FirebaseService
import com.luczka.mycoffee.domain.models.CategoryModel
import com.luczka.mycoffee.domain.models.RecipeModel
import com.luczka.mycoffee.domain.repositories.FirebaseRepository

class FirebaseRepositoryImpl(
    context: Context,
    private val firebaseService: FirebaseService
) : FirebaseRepository {

    companion object {
        private const val TAG = "FirebaseRepositoryImpl"
    }

    private val localeCode: String = context.resources.configuration.locales[0].language

    override suspend fun getCategories(): Result<List<CategoryModel>> {
        return try {
            val categories = firebaseService
                .getCategories()
                .toModel(localeCode)
            Result.success(categories)
        } catch (exception: Exception) {
            exception.message?.let { Log.d(TAG, it) }
            Result.failure(exception)
        }
    }

    override suspend fun getRecipes(methodId: String): Result<List<RecipeModel>> {
        return try {
            val recipes = firebaseService
                .getRecipesDto(methodId)
                .toModel()
            Result.success(recipes)
        } catch (exception: Exception) {
            exception.message?.let { Log.d(TAG, it) }
            Result.failure(exception)
        }
    }
}