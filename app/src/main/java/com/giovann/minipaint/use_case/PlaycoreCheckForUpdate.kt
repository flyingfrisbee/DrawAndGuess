package com.giovann.minipaint.use_case

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import com.giovann.minipaint.model.enumerate.PlaycoreUpdateStatus
import com.giovann.minipaint.utils.Constants
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

class PlaycoreCheckForUpdate(private val context: Context) {

    private val appUpdateManager = AppUpdateManagerFactory.create(context)
    private var playcoreUpdateStatus: PlaycoreUpdateStatus = PlaycoreUpdateStatus.None

    operator fun invoke(
        isMandatory: Boolean,
    ) {
        if (isMandatory) {
            playcoreUpdateStatus = PlaycoreUpdateStatus.Mandatory
        } else {
            playcoreUpdateStatus = PlaycoreUpdateStatus.Optional
        }

        appUpdateManager.appUpdateInfo.addOnSuccessListener { result ->
            try {
                appUpdateManager.startUpdateFlowForResult(
                    result,
                    AppUpdateType.IMMEDIATE,
                    (context as Activity),
                    Constants.PLAYCORE_APP_UPDATE
                )
            } catch (e: IntentSender.SendIntentException) {
                e.printStackTrace()
            }
        }

//        AlertDialog.Builder(ctx).apply {
//            setCancelable(false)
//            setTitle("New version available")
//            when (playcoreUpdateStatus) {
//                is PlaycoreUpdateStatus.Mandatory -> {
//                    setMessage("Do you want to download the newest version? (mandatory)")
//                }
//
//                is PlaycoreUpdateStatus.Optional -> {
//                    setMessage("Do you want to download the newest version? (optional)")
//                }
//            }
//            setNegativeButton("No") { _, _ ->
//                onFailure()
//            }
//            setPositiveButton("Yes") { _, _ ->
//                val intent = Intent(Intent.ACTION_VIEW)
//                intent.setData(Uri.parse(Constants.SCRIBBLER_PLAYSTORE_URL))
//                onSuccess(intent)
//            }
//            create()
//            show()
//        }
    }

    fun restoreUIForUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { result ->
            if (result.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        result,
                        AppUpdateType.IMMEDIATE,
                        (context as Activity),
                        Constants.PLAYCORE_APP_UPDATE
                    )
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun getUpdateStatus(): PlaycoreUpdateStatus {
        return playcoreUpdateStatus
    }
}