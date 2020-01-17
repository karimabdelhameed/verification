package com.bluecrunch.verification

import android.os.Bundle
import android.util.Log
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
        sendSMSAPI()
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
                    .setMobileNumber("01000290477")
                    .build()
            integration.sendFCMSms()
        }
    }

    private fun sendSMSAPI(){
        val mRequest = TestSMSRequest()
        mRequest.to = "201000290477"
        mRequest.message = "Hello , This is a test SMS!"
        send_button.setOnClickListener {
            integration =
                Integration
                    .Builder()
                    .setContext(this)
                    .setIsFirebase(false)
                    .setCountryCode("+2")
                    .setMobileNumber("01000290477")
                    .setIsSendMethodGet(false)
                    .setSendRequestBody(mRequest)
                    .setWebserviceCallBack(this)
                    .setSendRequestURL("http://192.241.234.75:9876/send-sms")
                    .build()

            integration.sendSMSPOST()
        }
    }

    private fun verifyCodeFCM(){
        verify_button.setOnClickListener {
            integration.verifyFCMPhoneNumberWithCode(verification_layout.verificationCodeText)
        }
    }

    override fun onFCMVerifiedSuccess() {
        Log.e("Success : ","FCM Verified success")
    }

    override fun onFCMCodeSent() {
        Log.e("Success : ","FCM Code Sent")
    }

    override fun onFCMError(error: String) {
        Log.e("Error : FCM",error)

    }

    override fun onWebServiceVerifiedSuccess() {
        Log.e("Success : ","API Success")
    }

    override fun onWebServiceFailedWithErrorBody(errorBody: ResponseBody) {
        Log.e("Error Body : ",errorBody.string())
    }

    override fun onWebServiceError(error: String) {
        Log.e("Error API : ",error)
    }

}
