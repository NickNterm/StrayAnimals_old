package com.iqsoft.strayanimals.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.iqsoft.strayanimals.Constants
import com.iqsoft.strayanimals.R
import kotlinx.android.synthetic.main.login_activity.*

class LoginActivity : AppCompatActivity() {
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        setContentView(R.layout.login_activity)
        val token = sharedPref.getString(Constants.LoginToken, "")
        if (token.isNullOrEmpty()) {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(Constants.IntentToken, token)
        }
        val gso: GoogleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        sign_in_button.setOnClickListener { signIn() }
    }

    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, Constants.SignInCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.SignInCode) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun updateUI(acc: GoogleSignInAccount) {
        Log.i("GotLogin", acc.email.toString())
        Toast.makeText(this, "name: ${acc.displayName}", Toast.LENGTH_LONG).show()
        /*val set = DriverManager.getConnection("jdbc:mariadb://192.168.50.29/StrayDogs", "nick", "iqsoft").createStatement().executeQuery("SELECT * FROM login")
        if (set.next()) {
            Log.i("GotMariaDB",set.getString(3))
            //your logic...
        }*/


        sign_in_button.visibility = View.GONE
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            if (account != null) {
                updateUI(account)
            }
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("GotLogin", "signInResult:failed code=" + e.statusCode)
        }
    }
}