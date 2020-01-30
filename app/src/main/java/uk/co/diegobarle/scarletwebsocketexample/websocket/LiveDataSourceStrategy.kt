package uk.co.diegobarle.scarletwebsocketexample.websocket

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach

const val TAG = "LiveDataSourceStrategy"

/**
 * Handles the network call and notifies the results in the LiveData
 */
fun <A> callLiveData(networkCall: suspend () -> A): LiveData<A> =
    liveData(Dispatchers.IO) {
        try {
            val response = networkCall.invoke()
            emit(response)
        }catch (ex: Exception){
            Log.e(TAG, ex.toString())
        }
    }

/**
 * Gets the value from the channel and notifies using LiveData
 */
@ExperimentalCoroutinesApi
fun <A> channelLiveData(receiveChannel: ReceiveChannel<A>): LiveData<A> =
    liveData(Dispatchers.IO) {
        try {
            receiveChannel.consumeEach { ticker ->
                emit(ticker)
            }
        }catch (ex: Exception){
            Log.e(TAG, ex.toString())
        }
    }
