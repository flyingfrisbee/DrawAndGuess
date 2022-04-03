package com.giovann.drawandguess.activity.game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.giovann.drawandguess.activity.game.fragment.CanvasFragment
import com.giovann.drawandguess.R
import com.giovann.drawandguess.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var canvasFragment: CanvasFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeCanvasFragment()

        binding.apply {
            btnReset.setOnClickListener {
                canvasFragment.clearCanvas()
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