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
    private val viewModel: GameViewModel by viewModels()
    private val canvasFragment: CanvasFragment by lazy {
        CanvasFragment.newInstance()
    }

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
            }
        }
    }

    private fun initializeCanvasFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, canvasFragment)
            .commit()
    }
}