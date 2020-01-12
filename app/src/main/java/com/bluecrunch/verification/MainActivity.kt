package com.bluecrunch.verification

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bluecrunch.bluecrunchverification.FCMCallBack
import com.bluecrunch.bluecrunchverification.Integration
import com.bluecrunch.bluecrunchverification.WebServiceCallBack
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.ResponseBody

class MainActivity : AppCompatActivity(), FCMCallBack, WebServiceCallBack {


    private lateinit var integration : Integration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sendCodeFCM()
        verifyCodeFCM()
    }

    private fun sendCodeFCM() {
        send_button.setOnClickListener {
            integration =
                Integration
                    .Builder()
                    .setContext(this)
                    .setIsFirebase(true)
                    .setCountryCode("+2")
                    .setFCMCallBack(this)
                    .setMobileNumber("01155520332")
                    .build()
            integration.sendFCMSms()
        }
    }

    private fun sendSMSAPI(){
        send_button.setOnClickListener {
            integration =
                Integration
                    .Builder()
                    .setContext(this)
                    .setIsFirebase(false)
                    .setCountryCode("+2")
                    .setMobileNumber("01000290477")
                    .setIsSendMethodGet(true)
                    .setWebserviceCallBack(this)
                    .setSendRequestURL("")
                    .build()
        }
    }

    private fun verifyCodeFCM(){
        verify_button.setOnClickListener {
            integration.verifyFCMPhoneNumberWithCode(verification_layout.verificationCodeText)
        }
    }

    override fun onFCMVerifiedSuccess() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onFCMCodeSent() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onFCMError(error: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onWebServiceVerifiedSuccess() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onWebServiceFailedWithErrorBody(errorBody: ResponseBody) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onWebServiceError(error: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
