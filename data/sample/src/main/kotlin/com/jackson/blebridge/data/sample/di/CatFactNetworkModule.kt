package com.jackson.blebridge.data.sample.di

import com.jackson.blebridge.data.sample.BuildConfig
import com.jackson.blebridge.data.sample.api.CatFactApi
import com.jackson.blebridge.data.sample.repository.CatFactRepositoryImpl
import com.jackson.blebridge.domain.sample.repository.CatFactRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class CatFactRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindCatFactRepository(
        implementation: CatFactRepositoryImpl,
    ): CatFactRepository
}

@Module
@InstallIn(SingletonComponent::class)
internal object CatFactNetworkModule {
    private const val BASE_URL = "https://catfact.ninja/"
    private const val TIMEOUT_SECONDS = 30L

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideCatFactApi(retrofit: Retrofit): CatFactApi =
        retrofit.create(CatFactApi::class.java)
}
