package com.iqsoft.strayanimals.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import com.iqsoft.strayanimals.Constants
import com.iqsoft.strayanimals.R
import com.iqsoft.strayanimals.adapters.MariaDB
import com.iqsoft.strayanimals.adapters.MariaDBInterface
import com.iqsoft.strayanimals.models.Account
import kotlinx.android.synthetic.main.sign_up_activity.*

class SignUpActivity : AppCompatActivity(), MariaDBInterface {
    private lateinit var mariaDB: MariaDB
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        setContentView(R.layout.sign_up_activity)
        setSupportActionBar(SignUpToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        SignUpToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        SignUpButton.setOnClickListener {
            if (NameTextInput.editText?.text.isNullOrEmpty()) {
                NameTextInput.error = "required!"
            } else if (PhoneTextInput.editText?.text.isNullOrEmpty()) {
                PhoneTextInput.error = "required!"
            } else if (EmailTextInput.editText?.text.isNullOrEmpty()) {
                EmailTextInput.error = "required!"
            }else{
                val editor: SharedPreferences.Editor = sharedPref.edit()
                editor.putString(Constants.sharedPrefToken,  intent.getStringExtra(Constants.IntentToken))
                editor.apply()
                val createdAccount = Account(
                    intent.getStringExtra(Constants.IntentToken),
                    NameTextInput.editText?.text.toString(),
                    PhoneTextInput.editText?.text.toString(),
                    EmailTextInput.editText?.text.toString(),
                    null
                )
                mariaDB = MariaDB(this)
                mariaDB.createAccount(createdAccount,this)
            }
        }
    }

    override fun createAccountCallback() {
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}