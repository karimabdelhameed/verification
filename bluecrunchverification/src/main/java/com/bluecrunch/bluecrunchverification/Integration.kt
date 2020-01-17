package com.bluecrunch.bluecrunchverification

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

open class Integration private constructor(
    private var context: FragmentActivity,
    builder: Builder
) {

    private var isFirebase = true
    private var isSendMethodGet = false
    private var isVerifyMethodGet = false
    private var mobileNumber = ""
    private var countryCode = ""
    private var sendRequestURL = ""
    private var verifyRequestURL = ""
    private var fcmCallBack: FCMCallBack?
    private var webServiceCallBack: WebServiceCallBack?
    private var sendRequestBody = JsonObject()
    private var verifyRequestBody = JsonObject()
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private var verificationID = ""
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    init {
        this.context = builder.context
        this.isFirebase = builder.isFirebase
        this.mobileNumber = builder.mobileNumber
        this.countryCode = builder.countryCode
        this.isSendMethodGet = builder.isSendMethodGet
        this.isVerifyMethodGet = builder.isVerifyMethodGet
        this.sendRequestURL = builder.sendRequestURL
        this.verifyRequestURL = builder.verifyRequestURL
        this.sendRequestBody = builder.sendRequestBody
        this.verifyRequestBody = builder.verifyRequestBody
        this.fcmCallBack = builder.fcmCallBack
        this.webServiceCallBack = builder.webServiceCallBack
    }

    fun sendFCMSms() {
        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential)
                FirebaseUtils.getUserAuthId()
            }

            override fun onVerificationFailed(e: FirebaseException) {
//                hideProgressDialog()
//                showPopUp(e.message, android.R.string.ok, false)
                if (fcmCallBack != null && e.localizedMessage != null) {
                    fcmCallBack?.onFCMError(e.localizedMessage!!)
                }
            }

            override fun onCodeSent(
                id: String,
                forceResendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(id, forceResendingToken)
//                hideProgressDialog()
                resendToken = forceResendingToken
                verificationID = id
                if (fcmCallBack != null) {
                    fcmCallBack?.onFCMCodeSent()
                }
            }
        }

        verify(mobileNumber)

//        showProgressDialog(R.string.sending_code)
    }

    private fun verify(phoneNumber: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "${countryCode}${phoneNumber}",
            30,
            TimeUnit.SECONDS,
            context as Activity,
            mCallbacks
        )
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(context as Activity)
        { task ->

            //            hideProgressDialog()
            if (task.isSuccessful) {
                try {
                    if (fcmCallBack != null) {
                        fcmCallBack?.onFCMVerifiedSuccess()
                    }
                   // FirebaseAuth.getInstance().signOut()
                } catch (ex: Exception) {
                    if (fcmCallBack != null) {
                        fcmCallBack?.onFCMError(ex.localizedMessage!!)
                    }
//                    showPopUp(
//                        ex.localizedMessage, android.R.string.ok,
//                        false
//                    )
                }
            } else {
//                showPopUp(R.string.invalid_code, android.R.string.ok,
//                    DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() }, true
//                )
            }
        }
    }


    fun verifyFCMPhoneNumberWithCode(verificationCode: String) {
        val credential = PhoneAuthProvider
            .getCredential(verificationID, verificationCode)

//        showProgressDialog(R.string.please_wait)
        signInWithPhoneAuthCredential(credential)
    }

    fun sendSMSGET() {
        val webService = WebService.retrofit.create(WebService::class.java)
        val call = webService.sendSMSGET(sendRequestURL)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response != null) {
                    if (webServiceCallBack != null) {
                        if (response.code() == 200) {
                            webServiceCallBack?.onWebServiceVerifiedSuccess(response.body())
                        } else
                            webServiceCallBack?.onWebServiceFailedWithErrorBody(response.errorBody()!!)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                webServiceCallBack?.onWebServiceError(t.localizedMessage)
            }
        })
    }

    fun sendSMSPOST() {
        val webService = WebService.retrofit.create(WebService::class.java)
        val call = webService.sendSMSPOST(sendRequestURL, sendRequestBody)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response != null) {
                    if (webServiceCallBack != null) {
                        if (response.code() == 200) {
                            webServiceCallBack?.onWebServiceVerifiedSuccess(response.body())
                        } else
                            webServiceCallBack?.onWebServiceFailedWithErrorBody(response.errorBody()!!)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                webServiceCallBack?.onWebServiceError(t.localizedMessage)
            }
        })
    }

    fun verifySMSGET() {
        val webService = WebService.retrofit.create(WebService::class.java)
        val call = webService.verifySMSGET(verifyRequestURL)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response != null) {
                    if (webServiceCallBack != null) {
                        if (response.code() == 200) {
                            webServiceCallBack?.onWebServiceVerifiedSuccess(response.body())
                        } else
                            webServiceCallBack?.onWebServiceFailedWithErrorBody(response.errorBody()!!)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                webServiceCallBack?.onWebServiceError(t.localizedMessage)
            }
        })
    }

    fun verifySMSPOST() {
        val webService = WebService.retrofit.create(WebService::class.java)
        val call = webService.verifySMSPOST(verifyRequestURL, verifyRequestBody)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response != null) {
                    if (webServiceCallBack != null) {
                        if (response.code() == 200) {
                            webServiceCallBack?.onWebServiceVerifiedSuccess(response.body())
                        } else
                            webServiceCallBack?.onWebServiceFailedWithErrorBody(response.errorBody()!!)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                webServiceCallBack?.onWebServiceError(t.localizedMessage)
            }
        })
    }

    class Builder {
        var isFirebase = true
            private set
        var mobileNumber = ""
            private set
        var countryCode = ""
            private set
        var sendRequestURL = ""
            private set
        var verifyRequestURL = ""
            private set
        var isSendMethodGet = false
            private set
        var isVerifyMethodGet = false
            private set
        var context = FragmentActivity()
            private set
        var sendRequestBody = JsonObject()
            private set
        var verifyRequestBody = JsonObject()
            private set

        var fcmCallBack: FCMCallBack? = null
            private set
        var webServiceCallBack: WebServiceCallBack? = null
            private set

        fun setContext(context: FragmentActivity) = apply { this.context = context }

        fun setIsFirebase(isFirebase: Boolean) = apply { this.isFirebase = isFirebase }

        fun setIsSendMethodGet(isSendMethodGet: Boolean) = apply {
            this.isSendMethodGet = isSendMethodGet
        }

        fun setFCMCallBack(fcmCallBack: FCMCallBack) = apply {
            this.fcmCallBack = fcmCallBack
        }

        fun setWebserviceCallBack(webServiceCallBack: WebServiceCallBack) = apply {
            this.webServiceCallBack = webServiceCallBack
        }

        fun setIsVerifyMethodGet(isVerifyMethodGet: Boolean) = apply {
            this.isVerifyMethodGet = isVerifyMethodGet
        }

        fun setSendRequestURL(sendRequestURL: String) = apply {
            this.sendRequestURL = sendRequestURL
        }

        fun setVerifyRequestURL(verifyRequestURL: String) = apply {
            this.verifyRequestURL = verifyRequestURL
        }

        fun setSendRequestBody(sendRequestBody: JsonObject) = apply {
            this.sendRequestBody = sendRequestBody
        }

        fun setVerifyRequestBody(verifyRequestBody: JsonObject) = apply {
            this.verifyRequestBody = verifyRequestBody
        }

        fun setMobileNumber(mobileNumber: String) = apply { this.mobileNumber = mobileNumber }

        fun setCountryCode(countryCode: String) = apply { this.countryCode = countryCode }

        fun build(): Integration {
            val integration = Integration(context, this)
            check(integration.mobileNumber.isNotEmpty()) { "Mobile number must be set!" }
            check(integration.countryCode.isNotEmpty()) { "Country code must be set!" }
//            check(integration.fcmCallBack == null) { "Callback isn't set!" }
            return Integration(context, this)
        }
    }
}

interface FCMCallBack {
    fun onFCMVerifiedSuccess()
    fun onFCMCodeSent()
    fun onFCMError(error: String)
}

interface WebServiceCallBack {
    fun onWebServiceVerifiedSuccess(responseBody: ResponseBody?)
    fun onWebServiceFailedWithErrorBody(errorBody: ResponseBody)
    fun onWebServiceError(error: String)
}