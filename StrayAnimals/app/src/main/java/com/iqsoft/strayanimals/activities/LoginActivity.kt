package com.iqsoft.strayanimals.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.iqsoft.strayanimals.Constants
import com.iqsoft.strayanimals.R
import com.iqsoft.strayanimals.adapters.MariaDB
import com.iqsoft.strayanimals.adapters.MariaDBInterface
import com.iqsoft.strayanimals.models.Account
import kotlinx.android.synthetic.main.login_activity.*


class LoginActivity : AppCompatActivity(), MariaDBInterface {
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mariaDB: MariaDB
    private lateinit var token: String
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        setContentView(R.layout.login_activity)
        mariaDB = MariaDB(this)
        val token = sharedPref.getString(Constants.sharedPrefToken, "")
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
        TestSave.setOnClickListener{


            // generate random token for testing
            val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
            this.token = "hrm3v8YYAH4DSGT"
                //(1..15)
                //.map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
                //.map(charPool::get)
                //.joinToString("")
            mariaDB.getAccount(this.token, this)

        }
    }

    override fun accountNullCallback() {
        val intent = Intent(this, SignUpActivity::class.java)
        intent.putExtra(Constants.IntentToken, this.token)
        startActivity(intent)
        finish()
    }

    override fun accountBannedCallback(code: Int) {
        Toast.makeText(
            this,
            "Your account has been Banned. Code: $code\nContact: ncript.developer@gmail.com",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun getAccountCallback(acc: Account) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(Constants.sharedPrefToken,  acc.token)
        editor.apply()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(Constants.IntentAccount, acc)
        startActivity(intent)
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
        this.token = acc.id.toString()
        mariaDB.getAccount(this.token, this)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            if (account != null) {
                Log.w("GotLogin", account.id.toString())
                updateUI(account)
            }
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("GotLogin", "signInResult:failed code=" + e.statusCode)
        }
    }
}