package com.example.hotgists.hilt.modules

import com.example.hotgists.api.GistRetrofitApi
import com.google.gson.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type


@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    fun providesContentApi(
        okHttp: OkHttpClient.Builder,
        retrofit: Retrofit.Builder
    ): GistRetrofitApi =
        retrofit
            .client(okHttp.build())
            .build()
            .create(GistRetrofitApi::class.java)

    @Provides
    fun providesOkHttpBuilder(): OkHttpClient.Builder =
        OkHttpClient.Builder()
            .retryOnConnectionFailure(true)

    @Provides
    fun providesRetrofit(gson: Gson): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl(GistRetrofitApi.url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    }

    @Provides
    fun providesGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(DateTime::class.java, DateTimeTypeAdapter())
            .create()
    }
}

class DateTimeTypeAdapter : JsonDeserializer<DateTime> {

    private val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): DateTime? =
        json.asString.let(formatter::parseDateTime)

}