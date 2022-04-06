package com.giovann.drawandguess.activity.game.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.giovann.drawandguess.R
import com.giovann.drawandguess.model.DummyResponse
import com.giovann.drawandguess.model.MovementCoordinate
import com.giovann.drawandguess.utils.Constants.WEBSOCKET_URL
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*

class CanvasFragment : Fragment(), CanvasView.CanvasListener {

    private lateinit var canvasView: CanvasView
    private lateinit var ws: WebSocket

    inner class EchoWebSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
//            webSocket.send("Hello world!")
//            webSocket.close(1000, "Goodbye!")
            super.onOpen(webSocket, response)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            val typeToken = object : TypeToken<List<MovementCoordinate>>() {}.type
            val resp = Gson().fromJson<List<MovementCoordinate>>(text, typeToken)
            Log.i("onMessage", "${resp}")
            super.onMessage(webSocket, text)
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.i("onClose", "${code}, ${reason}")
            super.onClosing(webSocket, code, reason)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.i("onFailure", "error: ${t.message}")
            super.onFailure(webSocket, t, response)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startWebsocket()
        canvasView = CanvasView.newInstance(this, requireContext())
        canvasView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        canvasView.contentDescription = getString(R.string.app_description)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return canvasView
    }

    override fun sendMovementData(data: MutableList<MovementCoordinate>) {
        Log.i("CanvasFragment", "$data")
        Log.i("CanvasFragment", "${ws.send(Gson().toJson(data))}")
    }

    private fun startWebsocket() {
        val client = OkHttpClient()

        val request = Request.Builder().url(WEBSOCKET_URL).build()
        val listener = EchoWebSocketListener()
        ws = client.newWebSocket(request, listener)

        client.dispatcher.executorService.shutdown()
    }

    fun clearCanvas() {
        canvasView.clearCanvas()
    }
}