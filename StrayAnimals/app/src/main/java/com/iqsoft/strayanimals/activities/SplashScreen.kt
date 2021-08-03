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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.iqsoft.strayanimals.Constants
import com.iqsoft.strayanimals.R
import com.iqsoft.strayanimals.adapters.MariaDB
import com.iqsoft.strayanimals.adapters.MariaDBInterface
import com.iqsoft.strayanimals.models.Account
import com.iqsoft.strayanimals.models.Post
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.splash_screen_activity.*
import java.lang.Exception


class SplashScreen : AppCompatActivity(), MariaDBInterface {
    private lateinit var mariaDB: MariaDB
    private lateinit var sharedPref: SharedPreferences
    private var myAccount: Account? = null
    private lateinit var loadedPosts: ArrayList<Post>
    private lateinit var loadedAccounts: ArrayList<Account>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen_activity)
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        checkConnection()
        SplashScreenNewAccountButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
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
                if (sharedPref.getString(Constants.sharedPrefToken, "") == "guest") {
                    if (!intent.getBooleanExtra("nowLogged", false)) {
                        MaterialAlertDialogBuilder(this)
                            .setTitle("Guest")
                            .setCancelable(false)
                            .setMessage("Do you still want to continue as guest?")
                            .setNegativeButton("Continue") { dialog, _ ->
                                mariaDB.getPosts(this)
                            }
                            .setPositiveButton("Log In") { _, _ ->
                                Constants.changeLocalTokenTo(this, "")
                                startActivity(Intent(this, SplashScreen::class.java))
                                finish()
                            }
                            .show()
                    }
                } else {
                    mariaDB.getAccount(sharedPref.getString(Constants.sharedPrefToken, "")!!, this)
                }
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            Toast.makeText(this, "No Connection", Toast.LENGTH_LONG).show()
            SplashScreenRefreshButton.visibility = View.VISIBLE
        }
    }

    override fun getAccountCallback(acc: Account) {
        myAccount = acc
        mariaDB.getPosts(this)
    }

    override fun postsReadCallback(posts: ArrayList<Post>) {
        loadedPosts = posts
        var accs = ArrayList<String>()
        for (i in posts) {
            accs.add(i.account_id!!.toString())
        }
        var lastAccounts: ArrayList<String>
        try {
            lastAccounts = accs.distinct() as ArrayList<String>
        } catch (e: Exception) {
            lastAccounts = ArrayList()
            lastAccounts.add(accs[0])
        }
        mariaDB.getAccountById(lastAccounts, this)
    }

    override fun accountReadCallback(accounts: ArrayList<Account>) {
        loadedAccounts = accounts
        val intent = Intent(this, MainActivity::class.java)
        if (myAccount != null) {
            intent.putExtra(Constants.IntentAccount, myAccount)
        }
        intent.putExtra(Constants.IntentPosts, loadedPosts)
        intent.putExtra(Constants.IntentAccountArray, loadedAccounts)
        startActivity(intent)
        finish()
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