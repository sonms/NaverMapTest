package com.example.navermaptest.data.di

import com.example.navermaptest.data.service.StaticMapService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    @Singleton
    fun providesStaticMapService(retrofit: Retrofit): StaticMapService =
        retrofit.create()
}