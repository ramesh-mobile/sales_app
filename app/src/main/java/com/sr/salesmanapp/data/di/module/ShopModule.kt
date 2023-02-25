package com.sr.salesmanapp.data.di.module

import com.sr.salesmanapp.data.repository.ShopRepository
import com.sr.salesmanapp.data.repository.ShopRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module(includes = [ShopSource::class])
@InstallIn(SingletonComponent::class)
object ShopModule {

   /* @Provides
    @Singleton
    fun provideShopApiService(){

    }*/
}

@Module()
@InstallIn(SingletonComponent::class)
abstract class ShopSource{

    @Singleton
    @Binds
    abstract fun provideShopRepository(impl: ShopRepositoryImpl) : ShopRepository
}