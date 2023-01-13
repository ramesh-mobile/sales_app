package com.sr.salesmanapp.data.di

import android.app.Application
import com.google.gson.Gson
import com.sr.salesmanapp.BuildConfig
import com.sr.salesmanapp.data.network.api.RestApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    //@Named("BASE_URL")
    //val BASE_URL =  "https://jsonplaceholder.typicode.com/"

    @Singleton
    @Provides
    fun provideRetrofit(
        gsonConverterFactory : GsonConverterFactory,
        scalarsConverterFactory : ScalarsConverterFactory,
        client : OkHttpClient
    ) = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(gsonConverterFactory)
        .addConverterFactory(scalarsConverterFactory)
        .client(client)
        .build()


    @Singleton
    @Provides
    fun provideGson() = Gson()

    @Singleton
    @Provides
    fun provideGsonConverterFactory(gson: Gson) = GsonConverterFactory.create(gson)

    @Singleton
    @Provides
    fun provideScalarsConverterFactory() = ScalarsConverterFactory.create()

    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG)
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        else
            loggingInterceptor.level = HttpLoggingInterceptor.Level.NONE

        return  loggingInterceptor
    }


    @Singleton
    @Provides
    fun provideCache(context: Application): Cache{
        val cacheSize : Long = 10 * 1024 * 1024 //10 mb
        return Cache(context.cacheDir,cacheSize)
    }



    @Singleton
    @Provides
    fun provideOkHttpClient(
        cache: Cache,
        loggingInterceptor: HttpLoggingInterceptor
    ) = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .cache(cache)
        .connectTimeout(90,TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(90,TimeUnit.SECONDS)
        .build()

    @Singleton
    @Provides
    fun provideApis(retrofit: Retrofit) = retrofit.create(RestApi::class.java)

}