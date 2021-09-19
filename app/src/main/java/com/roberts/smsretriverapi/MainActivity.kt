package com.roberts.smsretriverapi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetrieverClient
import com.roberts.smsretriverapi.databinding.ActivityMainBinding
import org.apache.commons.lang3.StringUtils
import org.greenrobot.eventbus.Subscribe

class MainActivity : AppCompatActivity() {
    private lateinit var  binding: ActivityMainBinding
    private lateinit var smsClient: SmsRetrieverClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        smsClient = SmsRetriever.getClient(this)


        /*val appSignatureHelper = AppSignatureHelper(this)
        Log.d("SIGNATURE",appSignatureHelper.appSignatures.toString())*/

        initSmsListener()

    }

    @Subscribe
    fun onReceiveSms(smsRetrievedEvent: SmsRetrievedEvent){
        val code: String = StringUtils.substringAfterLast(smsRetrievedEvent.message, "is").replace(":", "")
            .trim().substring(0, 4)

        runOnUiThread {

            if(!smsRetrievedEvent.timedOut){
                binding.firstEditText.addTextChangedListener(GenericTextWatcher(binding.firstEditText, binding.secondEditText))
            }

            binding.secondEditText.addTextChangedListener(GenericTextWatcher(binding.secondEditText,binding.thirdEditText))
            binding.thirdEditText.addTextChangedListener(GenericTextWatcher(binding.thirdEditText, binding.fourthEditText))
            binding.fourthEditText.addTextChangedListener(GenericTextWatcher(binding.fourthEditText,null))

        }

    }

    class GenericTextWatcher( val currentView: View, val nextView: View?) :
        TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            val text = s.toString()
            when(currentView.id){
                R.id.firstEditText -> if (text.length == 1) nextView!!.requestFocus()
                R.id.secondEditText -> if (text.length == 1) nextView!!.requestFocus()
                R.id.thirdEditText -> if (text.length == 1) nextView!!.requestFocus()

            }
        }

    }


    private fun initSmsListener() {
        smsClient.startSmsRetriever()
            .addOnSuccessListener {
                Toast.makeText(this, "Starting Retriever",
                    Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { failure ->
                failure.printStackTrace()
                Toast.makeText(this, failure.localizedMessage,
                    Toast.LENGTH_SHORT).show()
            }
    }

}