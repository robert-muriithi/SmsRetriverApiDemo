package com.roberts.smsretriverapi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import org.greenrobot.eventbus.EventBus

class MessageBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {
            val data = intent.extras
            if (data != null) {
                val status = data[SmsRetriever.EXTRA_STATUS] as Status
                var timedOut = false
                var otpCode: String? = null
                when (status.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        val appMessage = data[SmsRetriever.EXTRA_SMS_MESSAGE] as String
                        otpCode = appMessage
                    }
                    CommonStatusCodes.TIMEOUT -> {
                        timedOut = true
                    }

                }
                EventBus.getDefault().post(RetrievalEvent(timedOut, otpCode.toString()))
            }
        }
    }
}