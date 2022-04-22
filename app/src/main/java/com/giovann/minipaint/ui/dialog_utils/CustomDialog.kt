package com.giovann.minipaint.ui.dialog_utils

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.giovann.minipaint.R

class CustomDialog(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.game_finished_dialog)
        val btnOk = findViewById<Button>(R.id.btnOk)
        val ivClose = findViewById<ImageView>(R.id.ivClose)
        val tvRankAnnouncement = findViewById<TextView>(R.id.tvRankAnnouncement)
        btnOk.setOnClickListener {
            fireOnBtnClicked()
        }
        ivClose.setOnClickListener {
            fireOnBtnClicked()
        }
        when (rank) {
            1 -> {
                tvRankAnnouncement.text = "Congratulations!\nYou ranked ${rank}st"
            }

            2 -> {
                tvRankAnnouncement.text = "Congratulations!\nYou ranked ${rank}nd"
            }

            3 -> {
                tvRankAnnouncement.text = "Congratulations!\nYou ranked ${rank}rd"
            }

            4 -> {
                tvRankAnnouncement.text = "Congratulations!\nYou ranked ${rank}th"
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        fireOnBtnClicked()
    }

    private fun fireOnBtnClicked() {
        dialogListener?.onBtnClicked()
        dialogListener = null
    }

    interface OnDialogClicked {
        fun onBtnClicked()
    }

    companion object {
        private var dialogListener: OnDialogClicked? = null
        private var rank: Int = 1
        fun newInstance(context: Context, onDialogClicked: OnDialogClicked, ranking: Int): CustomDialog {
            dialogListener = onDialogClicked
            rank = ranking
            return CustomDialog(context)
        }
    }
}