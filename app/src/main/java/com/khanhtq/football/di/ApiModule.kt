package com.khanhtq.football.di

import com.khanhtq.football.BuildConfig
import com.khanhtq.football.data.remote.api.MatchApi
import com.khanhtq.football.data.remote.api.TeamApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    private const val BASE_URL = "https://jmde6xvjr4.execute-api.us-east-1.amazonaws.com"

    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        if (BuildConfig.DEBUG) {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            setLevel(HttpLoggingInterceptor.Level.NONE)
        }
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Singleton
    @Provides
    fun provideMoshiConverter(moshi: Moshi): MoshiConverterFactory = MoshiConverterFactory.create(moshi)

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        moshiConverterFactory: MoshiConverterFactory
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(moshiConverterFactory)
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun provideTeamApi(retrofit: Retrofit): TeamApi = retrofit.create(TeamApi::class.java)

    @Singleton
    @Provides
    fun provideMatchApi(retrofit: Retrofit): MatchApi = retrofit.create(MatchApi::class.java)

    @Provides
    fun provideDispatcherIO(): CoroutineDispatcher = Dispatchers.IO
}