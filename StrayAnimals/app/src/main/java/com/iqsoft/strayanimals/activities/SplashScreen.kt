package com.iqsoft.strayanimals.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceDataStore
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.iqsoft.strayanimals.Constants
import com.iqsoft.strayanimals.R
import com.iqsoft.strayanimals.adapters.MariaDB
import com.iqsoft.strayanimals.adapters.MariaDBInterface
import com.iqsoft.strayanimals.models.Account
import kotlinx.android.synthetic.main.splash_screen_activity.*


class SplashScreen : AppCompatActivity(), MariaDBInterface {
    private lateinit var mariaDB: MariaDB
    private lateinit var sharedPref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen_activity)
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        checkConnection()
        SplashScreenNewAccountButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        SplashScreenRefreshButton.setOnClickListener {
            checkConnection()
        }
    }

    private fun checkConnection() {
        SplashScreenRefreshButton.visibility = View.GONE
        SplashScreenNewAccountButton.visibility = View.GONE
        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        val window: Window = this.window
        window.statusBarColor = this.resources.getColor(R.color.backgroundDark)
        if (isConnected) {
            mariaDB = MariaDB(this)
            if (!sharedPref.getString(Constants.sharedPrefToken, "").isNullOrEmpty()) {
                mariaDB.getAccount(sharedPref.getString(Constants.sharedPrefToken, "")!!, this)
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        } else {
            Toast.makeText(this, "No Connection", Toast.LENGTH_LONG).show()
            SplashScreenRefreshButton.visibility = View.VISIBLE
        }
    }

    override fun getAccountCallback(acc: Account) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(Constants.IntentAccount, acc)
        startActivity(intent)
    }

    override fun accountBannedCallback(code: Int) {
        Toast.makeText(
            this,
            "Your account has been Banned. Code: $code\nContact: ncript.developer@gmail.com",
            Toast.LENGTH_LONG
        ).show()
        SplashScreenNewAccountButton.visibility = View.VISIBLE
    }

    override fun accountNullCallback() {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(Constants.sharedPrefToken, "")
        editor.apply()
        checkConnection()
    }
}