package com.iqsoft.strayanimals.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.iqsoft.strayanimals.Constants
import com.iqsoft.strayanimals.R
import com.iqsoft.strayanimals.adapters.MariaDB
import com.iqsoft.strayanimals.adapters.MariaDBInterface
import com.iqsoft.strayanimals.models.Account
import kotlinx.android.synthetic.main.edit_account_info_activity.*


class EditAccountInfo : AppCompatActivity(), MariaDBInterface {
    private lateinit var mariaDB: MariaDB
    private lateinit var account: Account
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_account_info_activity)
        mariaDB = MariaDB(this)
        val oldAccount: Account = intent.getParcelableExtra(Constants.IntentAccount)!!
        EditAccountNameTextInput.editText!!.setText(oldAccount.name)
        EditAccountPhoneTextInput.editText!!.setText(oldAccount.phone)
        EditAccountEmailTextInput.editText!!.setText(oldAccount.email)
        EditAccountSaveButton.setOnClickListener {
            if (EditAccountNameTextInput.editText!!.text.isEmpty()) {
                EditAccountNameTextInput.error = "required!"
            } else if (EditAccountEmailTextInput.editText!!.text.isEmpty()) {
                EditAccountEmailTextInput.error = "required!"
            } else if (EditAccountPhoneTextInput.editText!!.text.isEmpty()) {
                EditAccountPhoneTextInput.error = "required!"
            } else {
                account = Account(
                    oldAccount.token,
                    EditAccountNameTextInput.editText!!.text.toString(),
                    EditAccountPhoneTextInput.editText!!.text.toString(),
                    EditAccountEmailTextInput.editText!!.text.toString(),
                    ""
                )
                mariaDB.editAccount(account, this)
            }
        }
    }

    override fun editAccountCallback() {
        val intent = Intent()
        intent.putExtra(Constants.IntentAccount, account)
        setResult(Constants.EditAccountCode, intent)
        finish()
    }
}