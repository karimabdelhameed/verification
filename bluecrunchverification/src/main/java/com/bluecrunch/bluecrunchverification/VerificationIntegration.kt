package com.bluecrunch.bluecrunchverification

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.TimeUnit

open class VerificationIntegration private constructor(
    private var context: FragmentActivity,
    builder: Builder
) {

    private var isFirebase = true
    private var isSendMethodGet = false
    private var isVerifyMethodGet = false
    private var mobileNumber = ""
    private var countryCode = ""
    private var sendRequestURL = ""
    private var fcmCallBack: FCMCallBack?
    private var webServiceCallBack: WebServiceCallBack?
    private var sendRequestBody = TreeMap<String, Any>()
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
        this.sendRequestBody = builder.sendRequestBody
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
                if (fcmCallBack != null && e.localizedMessage != null) {
                    fcmCallBack?.onFCMError(e.localizedMessage!!)
                }
            }

            override fun onCodeSent(
                id: String,
                forceResendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(id, forceResendingToken)
                resendToken = forceResendingToken
                verificationID = id
                if (fcmCallBack != null) {
                    fcmCallBack?.onFCMCodeSent()
                }
            }
        }

        verify(mobileNumber)
    }

    fun reSendFCMSms() {
        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (fcmCallBack != null && e.localizedMessage != null) {
                    fcmCallBack?.onFCMError(e.localizedMessage!!)
                }
            }

            override fun onCodeSent(
                id: String,
                forceResendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(id, forceResendingToken)
                resendToken = forceResendingToken
                verificationID = id
                if (fcmCallBack != null) {
                    fcmCallBack?.onFCMCodeSent()
                }
            }
        }

        verifyResend(mobileNumber, resendToken)
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

    private fun verifyResend(
        phoneNumber: String,
        resendToken: PhoneAuthProvider.ForceResendingToken
    ) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "${countryCode}${phoneNumber}",
            30,
            TimeUnit.SECONDS,
            context as Activity,
            mCallbacks,
            resendToken
        )
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(context as Activity)
        { task ->

            if (task.isSuccessful) {
                try {
                    if (fcmCallBack != null) {
                        fcmCallBack?.onFCMVerifiedSuccess()
                    }
                    FirebaseAuth.getInstance().signOut()
                } catch (ex: Exception) {
                    if (fcmCallBack != null) {
                        fcmCallBack?.onFCMError(ex.localizedMessage!!)
                    }
                }
            } else {
                if (fcmCallBack != null) {
                    fcmCallBack?.onFCMVerificationCodeError()
                }
            }
        }
    }


    fun verifyFCMPhoneNumberWithCode(verificationCode: String) {
        val credential = PhoneAuthProvider
            .getCredential(verificationID, verificationCode)

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

    fun verifySMSGET(verifyURL: String) {
        val webService = WebService.retrofit.create(WebService::class.java)
        val call = webService.verifySMSGET(verifyURL)
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

    fun verifySMSPOST(verifyURL: String, request: TreeMap<String, Any>) {
        val webService = WebService.retrofit.create(WebService::class.java)
        val call = webService.verifySMSPOST(verifyURL, request)
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

    class Builder(val context: FragmentActivity) {
        var isFirebase = true
            private set
        var mobileNumber = ""
            private set
        var countryCode = ""
            private set
        var sendRequestURL = ""
            private set
        var isSendMethodGet = false
            private set
        var isVerifyMethodGet = false
            private set
        var sendRequestBody = TreeMap<String, Any>()
            private set

        var fcmCallBack: FCMCallBack? = null
            private set
        var webServiceCallBack: WebServiceCallBack? = null
            private set

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

        fun setSendRequestBody(sendRequestBody: TreeMap<String, Any>) = apply {
            this.sendRequestBody = sendRequestBody
        }

        fun setMobileNumber(mobileNumber: String) = apply { this.mobileNumber = mobileNumber }

        fun setCountryCode(countryCode: String) = apply { this.countryCode = countryCode }

        fun build(): VerificationIntegration {
            val integration = VerificationIntegration(context, this)
            if (isFirebase) {
                if (integration.countryCode.isEmpty())
                    throw RuntimeException("Country code must be set!")
                if (integration.mobileNumber.isEmpty())
                    throw RuntimeException("Mobile number must be set!")
                if (integration.fcmCallBack == null)
                    throw RuntimeException("Firebase callback must be set!")
            }
            check(!isFirebase && integration.webServiceCallBack == null)
            { "Web service callback must be set!" }

            check(isSendMethodGet && integration.sendRequestURL.isEmpty())
            { "Request URL must be set!" }

            check(!isSendMethodGet && integration.sendRequestBody.isNullOrEmpty()) {
                { "Body request must be set!" }
            }
            return VerificationIntegration(context, this)
        }
    }
}

interface FCMCallBack {
    fun onFCMVerifiedSuccess()
    fun onFCMCodeSent()
    fun onFCMError(error: String)
    fun onFCMVerificationCodeError()
}

interface WebServiceCallBack {
    fun onWebServiceVerifiedSuccess(responseBody: ResponseBody?)
    fun onWebServiceFailedWithErrorBody(errorBody: ResponseBody)
    fun onWebServiceError(error: String)
}