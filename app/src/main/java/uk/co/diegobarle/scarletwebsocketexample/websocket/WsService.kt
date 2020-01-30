package uk.co.diegobarle.scarletwebsocketexample.websocket

import com.google.gson.annotations.SerializedName
import com.tinder.scarlet.websocket.WebSocketEvent
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import kotlinx.coroutines.channels.ReceiveChannel

interface WsService{

    @Send
    fun sendSubscribe(subscribe: SubscribeAction)

    @Receive
    fun observeWebSocketEvent(): ReceiveChannel<WebSocketEvent>

    @Receive
    fun observeTicker(): ReceiveChannel<TickerResponse>
}

data class SubscribeAction(
    @SerializedName("type") val type: String = "subscribe",
    @SerializedName("product_ids") val productIds: List<String>,
    @SerializedName("channels") val channels: List<TickerRequest>
)

data class TickerRequest(
    @SerializedName("name") val name: String = "ticker",
    @SerializedName("product_ids") val productIds: List<String>
)
data class TickerResponse(
    @SerializedName("price") val price: Double,
    @SerializedName("time") val time: String
)