package com.giovann.minipaint.ui.activity.menu

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.giovann.minipaint.databinding.ActivityMenuBinding
import com.giovann.minipaint.databinding.EnterNameDialogBinding
import com.giovann.minipaint.databinding.EnterRoomNameDialogBinding
import com.giovann.minipaint.model.enumerate.Resource
import com.giovann.minipaint.ui.activity.game.GameActivity
import com.giovann.minipaint.utils.Helpers.disable
import com.giovann.minipaint.utils.Helpers.enable
import com.giovann.minipaint.view_model.MenuViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private val viewModel: MenuViewModel by viewModels()
    private val sharedPref: SharedPreferences by lazy {
        getSharedPreferences("Scribbler", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            viewModel.apply {
                val username = sharedPref.getString("user_name", null)
                if (username == null) {
                    createEnterNameDialog {
                        sharedPref.getString("user_name", null).let { newUsername ->
                            tvName.text = "Welcome, $newUsername"
                        }
                    }
                } else {
                    tvName.text = "Welcome, $username"
                }

                btnCreate.setOnClickListener {
                    createEnterRoomNameDialog {
                        executeCreateRoom(roomName)
                    }
                }

                btnJoin.setOnClickListener {
                    createEnterRoomNameDialog {
                        executeJoinRoom(roomName)
                    }
                }

                ivEdit.setOnClickListener {
                    createEnterNameDialog {
                        sharedPref.getString("user_name", null).let { newUsername ->
                            tvName.text = "Welcome, $newUsername"
                        }
                    }
                }

                createRoomResp.observe(this@MenuActivity, { resource ->
                    when (resource) {
                        is Resource.Failed -> {
                            Snackbar.make(root, resource.msg!!, Snackbar.LENGTH_SHORT).show()
                        }

                        is Resource.Ok -> {
                            resource.data?.let { data ->
                                if (data.success) {
                                    val intent = Intent(this@MenuActivity, GameActivity::class.java)
                                    intent.putExtra("room_name", viewModel.roomName)
                                    startActivity(intent)
                                    return@observe
                                }
                                Snackbar.make(root, "room with name ${roomName} already taken", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    }
                    changeButtonState(true)
                })

                joinRoomResp.observe(this@MenuActivity, { resource ->
                    when (resource) {
                        is Resource.Failed -> {
                            Snackbar.make(root, resource.msg!!, Snackbar.LENGTH_SHORT).show()
                        }

                        is Resource.Ok -> {
                            resource.data?.let { data ->
                                if (data.success) {
                                    val intent = Intent(this@MenuActivity, GameActivity::class.java)
                                    intent.putExtra("room_name", viewModel.roomName)
                                    startActivity(intent)
                                    return@observe
                                }
                                Snackbar.make(root, "room with name ${roomName} does not exist", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    }
                    changeButtonState(true)
                })
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (sharedPref.getBoolean("have_finished_game", false)) {
            startInAppReviewFlow()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPref.edit().putBoolean("have_finished_game", false).apply()
    }

    private fun startInAppReviewFlow() {
        val manager = ReviewManagerFactory.create(this)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result

                val flow = manager.launchReviewFlow(this, reviewInfo)
                flow.addOnCompleteListener { _ ->
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                }
            } else {
                // There was some problem, log or handle the error code.
                // @ReviewErrorCode val reviewErrorCode = (task.getException() as TaskException).errorCode
            }
        }
    }

    private fun createEnterNameDialog(onSuccess: () -> Unit) {
        val layoutDialog = EnterNameDialogBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this)
        builder.setView(layoutDialog.root).setCancelable(false)
        val dialog = builder.create()
        dialog.show()

        layoutDialog.apply {
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            btnSubmit.setOnClickListener {
                if (etName.text.isNullOrBlank()) {
                    tilName.error = "Name cannot be empty"
                    return@setOnClickListener
                }

                sharedPref.edit().putString("user_name", etName.text.toString()).apply()
                onSuccess()
                dialog.dismiss()
            }
        }
    }

    private fun createEnterRoomNameDialog(onSuccess: () -> Unit) {
        val layoutDialog = EnterRoomNameDialogBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this)
        builder.setView(layoutDialog.root).setCancelable(false)
        val dialog = builder.create()
        dialog.show()

        layoutDialog.apply {
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            btnSubmit.setOnClickListener {
                if (etRoomName.text.isNullOrBlank()) {
                    tilRoomName.error = "Room name cannot be empty"
                    return@setOnClickListener
                }

                viewModel.roomName = etRoomName.text.toString()
                onSuccess()
                changeButtonState(false)
                dialog.dismiss()
            }
        }
    }

    private fun changeButtonState(isEnabled: Boolean) {
        binding.apply {
            if (isEnabled) {
                btnCreate.enable()
                btnJoin.enable()
                return
            }
            btnCreate.disable()
            btnJoin.disable()
        }
    }
}