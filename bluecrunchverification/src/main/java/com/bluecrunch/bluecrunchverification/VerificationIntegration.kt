package com.bluecrunch.bluecrunchverification

import android.app.Activity
import android.util.Log
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

    /**
     * This method is used to send SMS using firebase
     */
    fun sendFCMSms() {
        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential)
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

    /**
     * This method is used to re-send SMS using firebase
     */
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

    /**
     * @param phoneNumber: phone-number used in verify process using firebase
     * This method is used to verify phone number using firebase
     */
    private fun verify(phoneNumber: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "${countryCode}${phoneNumber}",
            30,
            TimeUnit.SECONDS,
            context as Activity,
            mCallbacks
        )
    }

    /**
     * This method is used upon verifying mobile number after resend code using firebase
     */
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

    /**
     * @param credential : PhoneAuthCredential instance to be used in sign-in to firebase
     * This method is used to sign-in with phone credentials using firebase
     */
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(context as Activity)
        { task ->

            if (task.isSuccessful) {
                try {
                    getUserAuthId()
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

    /**
     * This method is used to get idToken after successful login with firebase
     */
    private fun getUserAuthId() {
        val mUser = FirebaseAuth.getInstance().currentUser ?: return
        mUser.getIdToken(true)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val idToken = task.result!!.token
                    if (fcmCallBack != null) {
                        fcmCallBack?.onFCMVerifiedSuccess(idToken)
                    }
                    FirebaseAuth.getInstance().signOut()
                } else {
                    Log.e("error", "error getting auth id")
                }
            }
    }
    
    /**
     * @param verificationCode : verification code number to be verified using firebase
     * This method is used to verify mobile number with verification code using firebase
     */
    fun verifyFCMPhoneNumberWithCode(verificationCode: String) {
        try{
            val credential = PhoneAuthProvider
                .getCredential(verificationID, verificationCode)

            signInWithPhoneAuthCredential(credential)
        }catch (e:Exception){
            Log.e("Exception => ", e.message.toString())
        }
    }

    /**
     * This method is used to send SMS using API Webservice @GET Method
     */
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

    /**
     * This method is used to send SMS using API Webservice @POST Method
     */
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

    /**
     * @param verifyURL : url for the verify sms web service end point @GET Method
     * @sample verifyURL: https://www.myAwesomeWebServiceVerifyGetMethod?mobile=000000000&code=123456
     * This method is used to verify SMS using API Webservice @GET Method
     */
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

    /**
     * @param verifyURL : url for the verify sms web service end point @POST Method
     * @param request : TreeMap<String, Any>() holds your verify post request
     * @sample verifyURL : https://www.myAwesomeWebServiceVerifyPOSTMethod
     * @sample request :
     * val mRequest = TreeMap<String, Any>()
     * mRequest["to"] = "201005140750"
     * mRequest["message"] = "Welcome :D"
     * This method is used to verify SMS using API Webservice @POST Method
     */
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

    /**
     * @param context : Activity context used in builder pattern
     */
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
            else {
                if (integration.webServiceCallBack == null)
                    throw RuntimeException("Web service callback must be set!")

                if (isSendMethodGet && integration.sendRequestURL.isEmpty())
                    throw RuntimeException("Request URL must be set!")

                if (!isSendMethodGet && integration.sendRequestBody.isNullOrEmpty()) {
                    throw RuntimeException("Body request must be set!")
                }
            }
            return VerificationIntegration(context, this)
        }
    }
}

/**
 * This interface is used while using FCM Send & Verification Methods
 */
interface FCMCallBack {
    fun onFCMVerifiedSuccess(idToken:String?)
    fun onFCMCodeSent()
    fun onFCMError(error: String)
    fun onFCMVerificationCodeError()
}

/**
 * This interface is used while using Webservice APIs Send & Verification Methods
 */
interface WebServiceCallBack {
    fun onWebServiceVerifiedSuccess(responseBody: ResponseBody?)
    fun onWebServiceFailedWithErrorBody(errorBody: ResponseBody)
    fun onWebServiceError(error: String)
}