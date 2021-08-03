package com.iqsoft.strayanimals.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.iqsoft.strayanimals.Constants
import com.iqsoft.strayanimals.R
import com.iqsoft.strayanimals.adapters.MariaDB
import com.iqsoft.strayanimals.adapters.MariaDBInterface
import com.iqsoft.strayanimals.fragments.AccountFragment
import com.iqsoft.strayanimals.fragments.MainFeedFragment
import com.iqsoft.strayanimals.fragments.UploadFragment
import com.iqsoft.strayanimals.models.Account
import com.iqsoft.strayanimals.models.Post
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity(), MariaDBInterface {
    lateinit var accountsList: ArrayList<Account>
    lateinit var postsList: ArrayList<Post>
    var profilePostsList: ArrayList<Post> = ArrayList()
    var myAccount: Account? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (intent.getParcelableExtra<Account>(Constants.IntentAccount) != null) {
            myAccount = intent.getParcelableExtra(Constants.IntentAccount)
        }
        accountsList = intent.getParcelableArrayListExtra(Constants.IntentAccountArray)!!
        postsList = intent.getParcelableArrayListExtra(Constants.IntentPosts)!!
        setCurrentFragment(MainFeedFragment.newInstance(this, myAccount, postsList, accountsList))
        bottomNavigationView.selectedItemId = R.id.NavigationBarFeed
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.NavigationBarAccount -> {
                    if (myAccount != null) {
                        setCurrentFragment(
                            AccountFragment.newInstance(
                                this,
                                myAccount
                            )
                        )
                    } else {
                        showLoginDialog()
                    }
                }
                R.id.NavigationBarUpload -> {
                    if (myAccount != null) {
                        setCurrentFragment(
                            UploadFragment.newInstance(
                                this,
                                myAccount!!.token
                            )
                        )
                    } else {
                        showLoginDialog()
                    }
                }
                R.id.NavigationBarFeed ->
                    setCurrentFragment(
                        MainFeedFragment.newInstance(
                            this,
                            myAccount,
                            postsList,
                            accountsList
                        )
                    )
            }
            true
        }
    }


    private fun showLoginDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Log In")
            .setMessage("You have to Log In to have access to this feature")
            .setCancelable(false)
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
                bottomNavigationView.selectedItemId = R.id.NavigationBarFeed
            }
            .setPositiveButton("Log In") { dialog, which ->
                Constants.changeLocalTokenTo(this, "")
                val intent = Intent(this, SplashScreen::class.java)
                intent.putExtra("nowLogged", true)
                startActivity(intent)
                finish()
            }
            .show()
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.MainActivityFragment, fragment)
            commit()
        }
}