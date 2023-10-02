package com.rjschulz

import com.vaadin.flow.component.dependency.JsModule
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.page.AppShellConfigurator
import com.vaadin.flow.component.page.Push
import com.vaadin.flow.router.RouterLayout
import com.vaadin.flow.shared.ui.Transport
import okhttp3.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component


/**
 * classes that are more or less boilerplate for Vaadin to work
 * along with global Spring Configuration classes
 */

@Push(transport = Transport.LONG_POLLING)
class AppShell : AppShellConfigurator

class MainLayout : Div(), RouterLayout

@Configuration
class AppConfig {

    @Bean
    fun okHttpClient(
        apiKeyInterceptor: ApiKeyInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .build()
    }

}

/**
 * This component adds the api key as a query parameter to each
 * call made by the http client
 */
@Component
class ApiKeyInterceptor(
    @Value("\${api-key}") val apiKey: String) : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            val original: Request = chain.request()
            val originalHttpUrl: HttpUrl = original.url

            val url = originalHttpUrl.newBuilder()
                .addQueryParameter("api_key", apiKey)
                .build()

            val requestBuilder: Request.Builder = original.newBuilder()
                .url(url)

            val request: Request = requestBuilder.build()
            return chain.proceed(request)
    }

}