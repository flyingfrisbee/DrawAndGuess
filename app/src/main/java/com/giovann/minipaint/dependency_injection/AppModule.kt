package com.giovann.minipaint.dependency_injection

import com.giovann.minipaint.api.ScribblerAPI
import com.giovann.minipaint.repository.ScribblerRepo
import com.giovann.minipaint.repository.ScribblerRepoImpl
import com.giovann.minipaint.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAPI(): ScribblerAPI = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ScribblerAPI::class.java)

    @Provides
    @Singleton
    fun provideRepository(
        api: ScribblerAPI
    ): ScribblerRepo = ScribblerRepoImpl(api)
}