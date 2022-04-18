package com.giovann.minipaint.ui.activity.game.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.giovann.minipaint.R
import com.giovann.minipaint.model.MovementCoordinate
import com.giovann.minipaint.model.game.GameStatusUpdate
import com.giovann.minipaint.utils.Constants.WEBSOCKET_URL
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import timber.log.Timber

class CanvasFragment : Fragment(), CanvasView.CanvasListener {

    interface FragmentListener {
        fun gameStatusUpdate(stat: GameStatusUpdate)
        fun gameClosed()
    }

    private lateinit var canvasView: CanvasView
    private lateinit var ws: WebSocket

    inner class EchoWebSocketListener : WebSocketListener() {
        override fun onMessage(webSocket: WebSocket, text: String) {
            when (text[0]) {
                '{' -> {
                    val typeToken = object : TypeToken<GameStatusUpdate>() {}.type
                    val resp = Gson().fromJson<GameStatusUpdate>(text, typeToken)
                    Timber.i("onMessage:: ${resp}")
                    fragmentListener?.gameStatusUpdate(resp)
                }

                else -> {
                    Timber.i("onMessage:: ${text}")
                }
            }
//            canvasView.syncDrawing(resp)
            super.onMessage(webSocket, text)
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Timber.i("onClose:: ${code}, ${reason}")
            fragmentListener?.gameClosed()
            super.onClosing(webSocket, code, reason)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Timber.i("onFailure:: error: ${t.message}")
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

    override fun onDestroyView() {
        ws.close(1000, null)
        super.onDestroyView()
    }

    override fun sendMovementData(data: MutableList<MovementCoordinate>) {
        Log.i("CanvasFragment", "$data")
        Log.i("CanvasFragment", "${ws.send(Gson().toJson(data))}")
    }

    private fun startWebsocket() {
        val client = OkHttpClient()

        val url = "$WEBSOCKET_URL$roomName/$playerName"
        Timber.i("$url")

        val request = Request.Builder().url(url).build()
        val listener = EchoWebSocketListener()
        ws = client.newWebSocket(request, listener)

        client.dispatcher.executorService.shutdown()
    }

    fun clearCanvas() {
        canvasView.clearCanvas()
    }

    companion object {
        var fragmentListener: FragmentListener? = null
        var roomName = ""
        var playerName = ""
        fun newInstance(listener: FragmentListener, room: String, player: String): CanvasFragment {
            roomName = room
            playerName = player
            fragmentListener = listener
            return CanvasFragment()
        }
    }
}