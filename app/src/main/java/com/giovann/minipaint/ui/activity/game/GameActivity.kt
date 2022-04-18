package com.giovann.minipaint.ui.activity.game

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.giovann.minipaint.ui.activity.game.fragment.CanvasFragment
import com.giovann.minipaint.R
import com.giovann.minipaint.databinding.ActivityGameBinding
import com.giovann.minipaint.model.enumerate.Resource
import com.giovann.minipaint.model.game.GameStatusUpdate
import com.giovann.minipaint.view_model.GameViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class GameActivity : AppCompatActivity(), CanvasFragment.FragmentListener {

    private lateinit var binding: ActivityGameBinding
    private val viewModel: GameViewModel by viewModels()
    private val sharedPref: SharedPreferences by lazy {
        getSharedPreferences("Scribbler", Context.MODE_PRIVATE)
    }
    private val canvasFragment: CanvasFragment by lazy {
        CanvasFragment.newInstance(this, roomName, sharedPref.getString("user_name", null)!!)
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
                    canvasFragment.clearCanvas()
                }
            }
        }
    }

    override fun gameStatusUpdate(stat: GameStatusUpdate) {
        runOnUiThread {
            adapter.populateData(stat.players, stat.currentlyDrawing)
        }
    }

    override fun gameClosed() {
        finish()
    }

    private fun initializeCanvasFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, canvasFragment)
            .commit()
    }
}