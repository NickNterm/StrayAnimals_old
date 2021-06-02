package com.iqsoft.strayanimals.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.iqsoft.strayanimals.Constants
import com.iqsoft.strayanimals.R
import com.iqsoft.strayanimals.adapters.MariaDB
import com.iqsoft.strayanimals.adapters.MariaDBInterface
import com.iqsoft.strayanimals.fragments.AccountFragment
import com.iqsoft.strayanimals.fragments.MainFeedFragment
import com.iqsoft.strayanimals.models.Account
import com.iqsoft.strayanimals.models.Post
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity(), MariaDBInterface {
    private lateinit var mariaDB: MariaDB
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val acc = intent.getParcelableExtra<Account>(Constants.IntentAccount)
        setCurrentFragment(AccountFragment.newInstance(acc!!))
        mariaDB = MariaDB(this)
        mariaDB.getPosts(this)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.NavigationBarAccount->setCurrentFragment(AccountFragment.newInstance(acc))
                R.id.NavigationBarUpload->setCurrentFragment(AccountFragment.newInstance(acc))
                R.id.NavigationBarFeed->setCurrentFragment(MainFeedFragment.newInstance(acc))
            }
            true
        }
    }


    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.MainActivityFragment,fragment)
            commit()
        }
}