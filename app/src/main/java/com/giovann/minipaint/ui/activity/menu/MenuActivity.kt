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
import com.giovann.minipaint.model.enumerate.Resource
import com.giovann.minipaint.ui.activity.game.GameActivity
import com.giovann.minipaint.view_model.MenuViewModel
import com.google.android.material.snackbar.Snackbar
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
                    // TODO: show dialog to insert name (tar save ke viewmodel nama roomny), then call api
                    // executeCreateRoom("anjim")
                }

                btnJoin.setOnClickListener {
                    // TODO: show dialog to insert name (tar save ke viewmodel nama roomny), then call api
                    // executeJoinRoom("anjim")
                }

                ivEdit.setOnClickListener {
                    //(pertimbangin limit jd berapa karakter? takutnya makan tempat di game activity)
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
                                    startActivity(Intent(this@MenuActivity, GameActivity::class.java))
                                    return@observe
                                }
                                Snackbar.make(root, "room with name ${roomName} already taken", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    }
                })

                joinRoomResp.observe(this@MenuActivity, { resource ->
                    when (resource) {
                        is Resource.Failed -> {
                            Snackbar.make(root, resource.msg!!, Snackbar.LENGTH_SHORT).show()
                        }

                        is Resource.Ok -> {
                            resource.data?.let { data ->
                                if (data.success) {
                                    startActivity(Intent(this@MenuActivity, GameActivity::class.java))
                                    return@observe
                                }
                                Snackbar.make(root, "room with name ${roomName} does not exist", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    }
                })
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
                    Snackbar.make(binding.root, "Name cannot be empty", Snackbar.LENGTH_SHORT).show()
                } else {
                    sharedPref.edit().putString("user_name", etName.text.toString()).apply()
                    onSuccess()
                    dialog.dismiss()
                }
            }
        }
    }
}