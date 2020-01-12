package com.bluecrunch.bluecrunchverification

import android.util.Log
import com.google.firebase.auth.FirebaseAuth

object FirebaseUtils {

    fun getUserAuthId() {
        val mUser = FirebaseAuth.getInstance().currentUser ?: return
        mUser.getIdToken(true)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val idToken = task.result!!.token

                } else {
                    Log.e("error", "error getting auth id")
                }
            }
    }
}