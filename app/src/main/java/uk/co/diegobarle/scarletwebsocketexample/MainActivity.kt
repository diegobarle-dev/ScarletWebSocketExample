package uk.co.diegobarle.scarletwebsocketexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.tinder.scarlet.websocket.WebSocketEvent
import kotlinx.coroutines.*
import uk.co.diegobarle.scarletwebsocketexample.websocket.*
import kotlin.coroutines.CoroutineContext

/**
 * Eventually we would like to have the Ws defined in a Service and may be use RxBus to handle the
 * updates/data sent by the server.
 */
@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity(), CoroutineScope {

    companion object{
        const val TAG = "MainActivity"
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    lateinit var wsBuilder: WsBuilder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        wsBuilder = WsBuilder(application)

        observeWebSocketEvents()
        observeTicker()
    }

    private fun observeWebSocketEvents(){
        callLiveData {
            wsBuilder.wsService.observeWebSocketEvent()
        }.observe(this, Observer {
            channelLiveData(it).observe(this, Observer { webSocketEvent ->
                handleWebSocketEvent(webSocketEvent)
            })
        })
    }

    private fun handleWebSocketEvent(webSocketEvent: WebSocketEvent){
        when(webSocketEvent){
            is WebSocketEvent.OnConnectionOpened -> {
                Log.d(TAG, "OnConnectionOpened")
                wsBuilder.wsService.sendSubscribe(
                    SubscribeAction(
                        productIds = listOf("ETH-BTC"),
                        channels = listOf(TickerRequest(productIds = listOf("ETH-BTC")))
                    ))
            }
            is WebSocketEvent.OnConnectionClosed -> Log.d(TAG, "OnConnectionClosed")
            is WebSocketEvent.OnConnectionClosing -> Log.d(TAG, "OnConnectionClosing")
            is WebSocketEvent.OnConnectionFailed -> Log.d(TAG, "OnConnectionFailed")
            is WebSocketEvent.OnMessageReceived -> Log.d(TAG, "OnMessageReceived")
        }

    }

    private fun observeTicker(){
        callLiveData {
            wsBuilder.wsService.observeTicker()
        }.observe(this, Observer {
            channelLiveData(it).observe(this, Observer { ticker ->
                Log.d(TAG, "Bitcoin price is ${ticker.price} for ${ticker.time}")
            })
        })
    }
}
