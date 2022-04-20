package com.giovann.minipaint.ui.activity.game

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.giovann.minipaint.R
import com.giovann.minipaint.databinding.ItemPlayerBinding
import com.giovann.minipaint.model.game.Player
import com.giovann.minipaint.utils.Helpers.hideView
import com.giovann.minipaint.utils.Helpers.showView
import timber.log.Timber

class PlayerAdapter(private val context: Context) : RecyclerView.Adapter<PlayerAdapter.ViewHolder>() {

    private val listContainer = mutableListOf<Player>()
//    private val selfUID by lazy {
//        listContainer[listContainer.size - 1].uid
//    }
    private var turn = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listContainer[position])
    }

    override fun getItemCount(): Int {
        return listContainer.size
    }

    fun populateData(input: List<Player>, currentlyDrawing: Int) {
        turn = currentlyDrawing
        listContainer.clear()
        listContainer.addAll(input)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemPlayerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(player: Player) {
            binding.apply {
                if (player.uid == turn) {
                    tvDrawingStatus.showView()
                    cvPlayer.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_player_pink))
                } else {
                    tvDrawingStatus.hideView()
                    if (player.hasAnswered) {
                        cvPlayer.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_player_green))
                    } else {
                        cvPlayer.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_player_orange))
                    }
                }
                tvPlayerScore.text = "Score: ${player.score}"
                tvPlayerName.text = player.name

                if (gameIsFinished) {
                    if (tvDrawingStatus.visibility == View.VISIBLE) {
                        tvDrawingStatus.hideView()
                    }
                    tvRank.showView()
                    when (player.rank) {
                        1 -> {
                            tvRank.text = "1st"
                        }

                        2 -> {
                            tvRank.text = "2nd"
                        }

                        3 -> {
                            tvRank.text = "3rd"
                        }

                        4 -> {
                            tvRank.text = "4th"
                        }
                    }
                } else {
                    tvRank.hideView()
                }
            }
        }
    }

    companion object {
        var gameIsFinished = false
    }
}