package com.roberts.smsretriverapi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetrieverClient
import com.roberts.smsretriverapi.databinding.ActivityMainBinding
import org.apache.commons.lang3.StringUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var smsClient: SmsRetrieverClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        smsClient = SmsRetriever.getClient(this)

        //uncomment this to generate your app hash string. You can view the hash string on your log cat when you run the app
        /*val appSignatureHelper = SignatureHelper(this)
        Log.d("SIGNATURE",appSignatureHelper.appSignature.toString())*/

        initSmsListener()

    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    private fun initSmsListener() {
        smsClient.startSmsRetriever()
            .addOnSuccessListener {
                Toast.makeText(
                    this, "Waiting for sms message",
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnFailureListener { failure ->
                Toast.makeText(
                    this, failure.localizedMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    @Subscribe
    fun onReceiveSms(retrievalEvent: RetrievalEvent) {
        val code: String =
            StringUtils.substringAfterLast(retrievalEvent.message, "is").replace(":", "")
                .trim().substring(0, 4)

        runOnUiThread {
            if (!retrievalEvent.timedOut) {
                binding.editText.setText(code)
            } else {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
        }
        initSmsListener()

    }
}