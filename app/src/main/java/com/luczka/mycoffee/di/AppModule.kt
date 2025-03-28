package com.luczka.mycoffee.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.luczka.mycoffee.data.local.ImageManager
import com.luczka.mycoffee.data.local.MyCoffeeDao
import com.luczka.mycoffee.data.local.MyCoffeeDatabase
import com.luczka.mycoffee.data.remote.FirebaseService
import com.luczka.mycoffee.data.repository.FirebaseRepositoryImpl
import com.luczka.mycoffee.data.repository.MyCoffeeDatabaseRepositoryImpl
import com.luczka.mycoffee.domain.repositories.FirebaseRepository
import com.luczka.mycoffee.domain.repositories.MyCoffeeDatabaseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMyCoffeeDatabase(@ApplicationContext context: Context): MyCoffeeDatabase {
        return Room.databaseBuilder(
            context,
            MyCoffeeDatabase::class.java,
            "coffee_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun providesMyCoffeeDao(myCoffeeDatabase: MyCoffeeDatabase): MyCoffeeDao {
        return myCoffeeDatabase.myCoffeeDao()
    }

    @Provides
    @Singleton
    fun providesImageManager(@ApplicationContext context: Context): ImageManager {
        return ImageManager(context)
    }

    @Provides
    @Singleton
    fun providesMyCoffeeDatabaseRepository(
        @ApplicationContext context: Context,
        myCoffeeDao: MyCoffeeDao,
        imageManager: ImageManager
    ): MyCoffeeDatabaseRepository {
        return MyCoffeeDatabaseRepositoryImpl(myCoffeeDao, imageManager)
    }

    @Provides
    @Singleton
    fun providesFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance(Firebase.app)
    }

    @Provides
    @Singleton
    fun providesFirebaseService(
        firebaseFirestore: FirebaseFirestore
    ): FirebaseService {
        return FirebaseService(firebaseFirestore)
    }

    @Provides
    fun providesFirebaseRepository(
        @ApplicationContext context: Context,
        firebaseService: FirebaseService
    ): FirebaseRepository {
        return FirebaseRepositoryImpl(context, firebaseService)
    }
}