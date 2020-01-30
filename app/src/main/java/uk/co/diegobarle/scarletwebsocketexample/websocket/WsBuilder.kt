package uk.co.diegobarle.scarletwebsocketexample.websocket

import android.app.Application
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle
import com.tinder.scarlet.messageadapter.gson.GsonMessageAdapter
import com.tinder.scarlet.retry.ExponentialWithJitterBackoffStrategy
import com.tinder.scarlet.websocket.ShutdownReason
import com.tinder.scarlet.websocket.okhttp.OkHttpWebSocket
import com.tinder.streamadapter.coroutines.CoroutinesStreamAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class WsBuilder(application: Application){
    companion object{
        const val SOCKET_BASE_URL = "wss://ws-feed-public.sandbox.pro.coinbase.com"
    }

    val wsService: WsService

    init {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()

        val protocol = OkHttpWebSocket(
            okHttpClient,
            OkHttpWebSocket.SimpleRequestFactory(
                { Request.Builder().url(SOCKET_BASE_URL).build() },
                { ShutdownReason.GRACEFUL }
            )
        )

        val backoffStrategy = ExponentialWithJitterBackoffStrategy(5000, 5000)
        val lifecycle = AndroidLifecycle.ofApplicationForeground(application)

        val configuration = Scarlet.Configuration(
            lifecycle = lifecycle,
            backoffStrategy = backoffStrategy,
            messageAdapterFactories = listOf(GsonMessageAdapter.Factory()),
            streamAdapterFactories = listOf(CoroutinesStreamAdapterFactory())
        )
        val scarletInstance = Scarlet(protocol, configuration)
        wsService = scarletInstance.create()
    }
}