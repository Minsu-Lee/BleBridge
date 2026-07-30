package com.jackson.blebridge.data.di

import com.jackson.blebridge.data.provider.AndroidAppInfoProvider
import com.jackson.blebridge.domain.provider.AppInfoProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProviderModule {
    @Binds
    @Singleton
    abstract fun bindAppInfoProvider(
        implementation: AndroidAppInfoProvider,
    ): AppInfoProvider
}
