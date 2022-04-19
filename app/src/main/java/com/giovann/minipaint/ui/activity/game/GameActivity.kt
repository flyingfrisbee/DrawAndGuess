package com.giovann.minipaint.ui.activity.game

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.giovann.minipaint.ui.activity.game.fragment.CanvasFragment
import com.giovann.minipaint.R
import com.giovann.minipaint.databinding.ActivityGameBinding
import com.giovann.minipaint.model.enumerate.Resource
import com.giovann.minipaint.model.game.GameStatusUpdate
import com.giovann.minipaint.ui.activity.game.fragment.CanvasView
import com.giovann.minipaint.utils.Helpers.disable
import com.giovann.minipaint.utils.Helpers.enable
import com.giovann.minipaint.view_model.GameViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private val viewModel: GameViewModel by viewModels()
    private val sharedPref: SharedPreferences by lazy {
        getSharedPreferences("Scribbler", Context.MODE_PRIVATE)
    }
    private val canvasFragment: CanvasFragment by lazy {
        CanvasFragment.newInstance(roomName, sharedPref.getString("user_name", null)!!)
    }
    private val adapter by lazy {
        PlayerAdapter(this)
    }
    private val roomName by lazy {
        intent.getStringExtra("room_name")!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeCanvasFragment()

        binding.apply {
            viewModel.apply {
                tvRoomName.text = "Room name: $roomName"

                rvPlayers.adapter = adapter
                rvPlayers.setHasFixedSize(true)
                rvPlayers.layoutManager = GridLayoutManager(this@GameActivity, 4)

                btnReset.setOnClickListener {
                    if (currentTurn == playerUID) {
                        canvasFragment.clearCanvas()
                        canvasFragment.sendMessageToWebsocket("0;${viewModel.playerUID}")
                    }
                }

                btnAction.setOnClickListener {
                    if (etGuess.text.isNullOrBlank()) {
                        tilGuess.error = "Answer cannot be empty!"
                        return@setOnClickListener
                    }

                    tilGuess.error = null
                    if (currentTurn == playerUID) {
                        //send "2" to server to continue turn
                        canvasFragment.sendMessageToWebsocket("2")
                        canvasFragment.clearCanvas()
                        canvasFragment.sendMessageToWebsocket("0;${viewModel.playerUID}")
                    } else {
                        //send "3,UID,answer" to server to submit answer
                        canvasFragment.sendMessageToWebsocket("3;${playerUID};${etGuess.text.toString()}")
                    }
                    etGuess.text = null
                    clRoot.requestFocus()
                    currentFocus?.let { view ->
                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                }

                gameStatusUpdate.observe(this@GameActivity, { stat ->
                    getPlayerUIDForFirstTime(stat)
                    currentTurn = stat.currentlyDrawing
                    adapter.populateData(stat.players, currentTurn)

                    if (currentTurn == playerUID) {
                        CanvasView.drawingEnabled = true
                        etGuess.setText(stat.answer)
                        btnAction.text = "Next"
                        stat.players.forEach { eachPlayer ->
                            if (eachPlayer.uid == playerUID) {
                                return@forEach
                            }
                            if (!eachPlayer.hasAnswered) {
                                btnAction.disable()
                                return@observe
                            }
                        }
                        btnAction.enable()
                    } else {
                        CanvasView.drawingEnabled = false
                        btnAction.text = "Submit"
                        btnAction.enable()
                    }
                })

                closeGameSignal.observe(this@GameActivity, { closeGame ->
                    if (closeGame) { finish() }
                })
            }
        }
    }

    private fun getPlayerUIDForFirstTime(stat: GameStatusUpdate) {
        viewModel.apply {
            if (playerUID == -1) {
                playerUID = stat.players[stat.players.size - 1].uid
            }
        }
    }

    private fun initializeCanvasFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, canvasFragment)
            .commit()
    }
}