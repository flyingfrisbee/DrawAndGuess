package com.giovann.minipaint.ui.activity.game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.giovann.minipaint.ui.activity.game.fragment.CanvasFragment
import com.giovann.minipaint.R
import com.giovann.minipaint.databinding.ActivityGameBinding
import com.giovann.minipaint.model.enumerate.Resource
import com.giovann.minipaint.view_model.GameViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var canvasFragment: CanvasFragment
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeCanvasFragment()

        binding.apply {
            viewModel.apply {
                btnReset.setOnClickListener {
                    canvasFragment.clearCanvas()
                }

                executeCreateRoom("anjim")
                executeJoinRoom("anjim")

                createRoomResp.observe(this@GameActivity, { resource ->
                    when (resource) {
                        is Resource.Failed -> {
                            Snackbar.make(root, resource.msg!!, Snackbar.LENGTH_SHORT).show()
                        }

                        is Resource.Ok -> {
                            Timber.i("create: ${resource.data}")
                        }
                    }
                })

                joinRoomResp.observe(this@GameActivity, { resource ->
                    when (resource) {
                        is Resource.Failed -> {
                            Snackbar.make(root, resource.msg!!, Snackbar.LENGTH_SHORT).show()
                        }

                        is Resource.Ok -> {
                            Timber.i("join: ${resource.data}")
                        }
                    }
                })
            }
        }
    }

    private fun initializeCanvasFragment() {
        canvasFragment = CanvasFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, canvasFragment)
            .commit()
    }
}