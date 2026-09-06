package com.sabbirsamol.allahorpoth

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class ProfileSettingsActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var txtUserInfo: TextView
    private lateinit var btnAuthAction: Button

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { token ->
                firebaseAuthWithGoogle(token)
            }
        } catch (e: ApiException) {
            Toast.makeText(this, "গুগল সাইন-ইন ব্যর্থ হয়েছে: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#F5F5F5"))
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }

        val title = TextView(this).apply {
            text = "👤 প্রোফাইল ও সেটিংস"
            textSize = 20f
            setTextColor(Color.parseColor("#1E293B"))
            setPadding(0, 0, 0, dp(16))
        }
        root.addView(title)

        val cardUser = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.WHITE)
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }

        val userTitle = TextView(this).apply {
            text = "👤 ইউজার অ্যাকাউন্ট"
            textSize = 16f
            setTextColor(Color.parseColor("#0D9488"))
            setPadding(0, 0, 0, dp(8))
        }
        cardUser.addView(userTitle)

        txtUserInfo = TextView(this).apply {
            textSize = 14f
            setTextColor(Color.parseColor("#333333"))
            setPadding(0, 0, 0, dp(12))
        }
        cardUser.addView(txtUserInfo)

        btnAuthAction = Button(this).apply {
            setOnClickListener {
                val currentUser = auth.currentUser
                if (currentUser != null && !currentUser.isAnonymous) {
                    auth.signOut()
                    googleSignInClient.signOut().addOnCompleteListener {
                        updateUI()
                        Toast.makeText(this@ProfileSettingsActivity, "লগ আউট সফল হয়েছে", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val signInIntent = googleSignInClient.signInIntent
                    googleSignInLauncher.launch(signInIntent)
                }
            }
        }
        cardUser.addView(btnAuthAction, LinearLayout.LayoutParams(-1, -2))
        root.addView(cardUser, LinearLayout.LayoutParams(-1, -2))

        setContentView(root)
        updateUI()
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "সফলভাবে গুগল অ্যাকাউন্ট যুক্ত হয়েছে!", Toast.LENGTH_SHORT).show()
                    updateUI()
                } else {
                    Toast.makeText(this, "অথেন্টিকেশন ফেইল করেছে।", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateUI() {
        val currentUser = auth.currentUser
        if (currentUser != null && !currentUser.isAnonymous) {
            txtUserInfo.text = "আইডি/ইমেল: ${currentUser.email ?: currentUser.uid}"
            btnAuthAction.text = "লগ আউট / সাইন আউট"
            btnAuthAction.setBackgroundColor(Color.parseColor("#EF4444"))
        } else {
            if (currentUser == null) {
                auth.signInAnonymously()
            }
            txtUserInfo.text = "আইডি/ইমেল: অতিথি ব্যবহারকারী (Anonymous)"
            btnAuthAction.text = "গুগল দিয়ে লগইন করুন"
            btnAuthAction.setBackgroundColor(Color.parseColor("#0D9488"))
        }
    }
}
