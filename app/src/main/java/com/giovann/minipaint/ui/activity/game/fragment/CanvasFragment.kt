package com.giovann.minipaint.ui.activity.game.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.giovann.minipaint.R
import com.giovann.minipaint.model.MovementCoordinate
import com.giovann.minipaint.model.game.GameStatusUpdate
import com.giovann.minipaint.ui.activity.game.PlayerAdapter
import com.giovann.minipaint.utils.Constants.WEBSOCKET_URL
import com.giovann.minipaint.view_model.GameViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import timber.log.Timber

class CanvasFragment : Fragment(), CanvasView.CanvasListener {
    private lateinit var canvasView: CanvasView
    private lateinit var ws: WebSocket
    private val viewModel: GameViewModel by activityViewModels()
    private val sharedPref: SharedPreferences by lazy {
        requireContext().getSharedPreferences("Scribbler", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startWebsocket()
        canvasView = CanvasView.newInstance(this, requireContext())
//        canvasView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        canvasView.contentDescription = getString(R.string.app_description)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return canvasView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.apply {
            getHeightAndWidthOfCanvas()

            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                delay(90000L)
                sharedPref.edit().putBoolean("have_finished_game", true).apply()
            }

            val gameStatusUpdateOnce: LiveData<GameStatusUpdate> = gameStatusUpdate
            gameStatusUpdateOnce.observe(viewLifecycleOwner, object : Observer<GameStatusUpdate> {
                override fun onChanged(t: GameStatusUpdate?) {
                    if (t?.players?.size == 1) {
                        pingEveryDuration()
                    }

                    gameStatusUpdateOnce.removeObserver(this)
                }
            })
        }
    }

    override fun onDestroyView() {
        ws.close(1000, null)
        super.onDestroyView()
    }

    override fun sendMovementData(data: MutableList<MovementCoordinate>) {
        viewModel.apply {
            sendMessageToWebsocket("1;$playerUID;w=$widthPixel;h=$heightPixel;${Gson().toJson(data)}")
        }
    }

    private fun getHeightAndWidthOfCanvas() {
        viewModel.apply {
            viewLifecycleOwner.lifecycleScope.launch {
                delay(1000L)
                heightPixel = canvasView.height
                widthPixel = canvasView.width
                if (isDrawingTurn) {
                    CanvasView.drawingEnabled = true
                }
            }
        }
    }

    private fun startWebsocket() {
        val client = OkHttpClient()

        val url = "$WEBSOCKET_URL$roomName/$playerName"

        val request = Request.Builder().url(url).build()
        val listener = EchoWebSocketListener()
        ws = client.newWebSocket(request, listener)

        client.dispatcher.executorService.shutdown()
    }

    private fun pingEveryDuration() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            delay(16000L)
            ws.send("e")
            pingEveryDuration()
        }
    }

    fun sendMessageToWebsocket(text: String) {
        ws.send(text)
    }

    fun clearCanvas() {
        canvasView.clearCanvas()
    }

    inner class EchoWebSocketListener : WebSocketListener() {
        override fun onMessage(webSocket: WebSocket, text: String) {
            viewModel.apply {
                when (text[0]) {
                    '{' -> {
                        val typeToken = object : TypeToken<GameStatusUpdate>() {}.type
                        val resp = Gson().fromJson<GameStatusUpdate>(text, typeToken)
                        updateStatus(resp)
                    }

                    '[' -> {
                        val textList = text.substring(1).split(";")
                        val widthScale = widthPixel.toFloat() / textList[0].toInt()
                        val heightScale = heightPixel.toFloat() / textList[1].toInt()

                        val typeToken = object : TypeToken<List<MovementCoordinate>>() {}.type
                        val resp = Gson().fromJson<List<MovementCoordinate>>(textList[2], typeToken)
                        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                            resp.forEach {
                                it.currentX = it.currentX * widthScale
                                it.endX = it.endX * widthScale
                                it.currentY = it.currentY * heightScale
                                it.endY = it.endY * heightScale
                            }
                            canvasView.syncDrawing(resp)
                        }
                    }

                    '0' -> {
                        clearCanvas()
                    }

                    '4' -> {
                        PlayerAdapter.gameIsFinished = true
                    }

                    else -> {}
                }
            }
            super.onMessage(webSocket, text)
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
//            Timber.i("onClosing: ${reason}")
            viewModel.sendCloseGameSignal()
            super.onClosing(webSocket, code, reason)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
//            Timber.i("onFailure: ${response?.message}. ${response}")
            viewModel.sendCloseGameSignal()
            super.onFailure(webSocket, t, response)
        }
    }

    companion object {
        var roomName = ""
        var playerName = ""
        fun newInstance(room: String, player: String): CanvasFragment {
            roomName = room
            playerName = player
            return CanvasFragment()
        }
    }
}