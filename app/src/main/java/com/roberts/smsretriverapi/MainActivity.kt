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
    private lateinit var  binding: ActivityMainBinding
    private lateinit var smsClient: SmsRetrieverClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        smsClient = SmsRetriever.getClient(this)


        val appSignatureHelper = AppSignatureHelper(this)
        Log.d("SIGNATURE",appSignatureHelper.appSignature.toString())

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
                Toast.makeText(this, "Waiting for sms message",
                    Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { failure ->
                Toast.makeText(this, failure.localizedMessage,
                    Toast.LENGTH_SHORT).show()
            }
    }

    @Subscribe
    fun onReceiveSms(smsRetrievedEvent: SmsRetrievedEvent){
        val code: String = StringUtils.substringAfterLast(smsRetrievedEvent.message, "is").replace(":", "")
            .trim().substring(0, 4)

        runOnUiThread {
            if(!smsRetrievedEvent.timedOut){
                binding.editText.setText(code)
            }
            else{
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
        }
        initSmsListener()

    }
}