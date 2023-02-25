package com.sr.salesmanapp.data.di

import android.app.Application
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.sr.salesmanapp.BuildConfig
import com.sr.salesmanapp.data.network.api.RestApi
import com.sr.salesmanapp.utils.Constants
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
object FcmNetworkModule {

    @Provides
    @Singleton
    //fun provideFirebaseDb(): DatabaseReference = Firebase.database.reference.child(Constants.SHOP_MODEL)
    fun provideFirebaseDb(): DatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.SHOP_MODEL)



}