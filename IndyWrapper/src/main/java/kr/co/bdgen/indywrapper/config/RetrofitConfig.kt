package kr.co.bdgen.indywrapper.config

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.time.Duration


object RetrofitConfig {
    val issuerBaseUrl = "http://211.37.24.246:8080/"

    private val retrofitBuilder: Retrofit.Builder = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .client(
            OkHttpClient.Builder()
                .addInterceptor(
                    HttpLoggingInterceptor()
                        .apply {
                            setLevel(HttpLoggingInterceptor.Level.BODY)
                        }
                )
                .readTimeout(Duration.ofSeconds(30))
                .build()
        )

    fun <T> createApi(baseUrl: String, api: Class<T>): T = retrofitBuilder
        .baseUrl(baseUrl)
        .build()
        .create(api)
}