package com.bluecrunch.verification

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bluecrunch.bluecrunchverification.FCMCallBack
import com.bluecrunch.bluecrunchverification.VerificationIntegration
import com.bluecrunch.bluecrunchverification.WebServiceCallBack
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.ResponseBody
import java.util.*

class MainActivity : AppCompatActivity(), FCMCallBack, WebServiceCallBack {


    private lateinit var verificationIntegration: VerificationIntegration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sendCodeFCM()
//        verifyCodeFCM()
    }

    private fun sendCodeFCM() {
        send_button.setOnClickListener {
            verificationIntegration =
                VerificationIntegration
                    .Builder(this)
                    .setIsFirebase(true)
                    .setCountryCode("+2")
                    .setFCMCallBack(this)
                    .setMobileNumber("01000290477")
                    .build()
            verificationIntegration.sendFCMSms()
        }
    }

    private fun sendSMSAPI() {
        val mRequest = TreeMap<String, Any>()
        mRequest["to"] = "201005140750"
        mRequest["message"] = "Welcome :D"
        send_button.setOnClickListener {
            verificationIntegration =
                VerificationIntegration
                    .Builder(this)
                    .setIsFirebase(false)
                    .setIsSendMethodGet(false)
                    .setSendRequestBody(mRequest)
                    .setWebserviceCallBack(this)
                    .setSendRequestURL("http://192.241.234.75:9876/send-sms")
                    .build()
            verificationIntegration.sendSMSPOST()
        }
    }

    private fun verifyCodeFCM() {
        verify_button.setOnClickListener {
            verificationIntegration.verifyFCMPhoneNumberWithCode(verification_layout.verificationCodeText)
        }
    }

    override fun onFCMVerifiedSuccess() {
        Log.e("Success : ", "FCM Verified success")
    }

    override fun onFCMCodeSent() {
        Log.e("Success : ", "FCM Code Sent")
    }

    override fun onFCMError(error: String) {
        Log.e("Error : FCM", error)

    }

    override fun onWebServiceVerifiedSuccess(responseBody: ResponseBody?) {
        Log.e("Success : ", "API Success")
    }

    override fun onWebServiceFailedWithErrorBody(errorBody: ResponseBody) {
        Log.e("Error Body : ", errorBody.string())
    }

    override fun onWebServiceError(error: String) {
        Log.e("Error API : ", error)
    }

    override fun onFCMVerificationCodeError() {
        Log.e("FCM Code Error : ", "Wrong verification code")
    }

}
