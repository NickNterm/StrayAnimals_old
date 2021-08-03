package com.iqsoft.strayanimals.fragments

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.iqsoft.strayanimals.Constants
import com.iqsoft.strayanimals.R
import com.iqsoft.strayanimals.activities.EditAccountInfo
import com.iqsoft.strayanimals.activities.MainActivity
import com.iqsoft.strayanimals.activities.SplashScreen
import com.iqsoft.strayanimals.adapters.MainFeedRecycleViewAdapter
import com.iqsoft.strayanimals.adapters.MariaDB
import com.iqsoft.strayanimals.adapters.MariaDBInterface
import com.iqsoft.strayanimals.models.Account
import com.iqsoft.strayanimals.models.Post
import kotlinx.android.synthetic.main.account_fragment.view.*


class AccountFragment(private val con: Context) : Fragment(), MariaDBInterface {
    private lateinit var mariaDB: MariaDB

    private var account: Account? = null
    private var postList: ArrayList<Post> = ArrayList()

    private lateinit var accountRecycleViewAdapter: MainFeedRecycleViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            account = it.getParcelable(Constants.IntentAccount)
        }

        mariaDB = MariaDB(con)
        if ((activity as MainActivity).profilePostsList.isEmpty()) {
            mariaDB.getPostsFromToken(account!!.token!!, this)
        } else {
            postList = (activity as MainActivity).profilePostsList
        }
        accountRecycleViewAdapter = MainFeedRecycleViewAdapter(
            con,
            postList,
            arrayListOf(account!!)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val lf = requireActivity().layoutInflater
        val v: View = lf.inflate(R.layout.account_fragment, container, false)
        v.AccountFragmentEmail.text = account?.email
        v.AccountFragmentPhone.text = account?.phone
        v.AccountFragmentToolBar.title = account?.name
        v.AccountFragmentRecycleView.layoutManager = LinearLayoutManager(con)
        v.AccountFragmentRecycleView.adapter = accountRecycleViewAdapter
        v.AccountFragmentSettingsButton.setOnClickListener {
            showMenu(it, R.menu.account_dropdown_menu)
        }
        v.AccountFragmentSwipeToRefresh.setProgressBackgroundColorSchemeColor(resources.getColor(R.color.backgroundGray))
        v.AccountFragmentSwipeToRefresh.setColorSchemeColors(
            ContextCompat.getColor(con, R.color.accentColor)
        )
        v.AccountFragmentSwipeToRefresh.setOnRefreshListener {
            mariaDB.getPostsFromToken(account!!.token!!, this)
        }
        if (postList.isNotEmpty()) {
            v.AccountFragmentLoading.visibility = View.GONE
            v.AccountFragmentRecycleView.visibility = View.VISIBLE
        }
        return v
    }

    override fun postsReadFromTokenCallback(posts: ArrayList<Post>) {
        view?.AccountFragmentLoading?.visibility = View.GONE
        view?.AccountFragmentRecycleView?.visibility = View.VISIBLE
        postList.clear()
        for (post in posts) {
            postList.add(post)
        }
        (activity as MainActivity).profilePostsList = postList
        view?.AccountFragmentRecycleView?.adapter = accountRecycleViewAdapter
        view?.AccountFragmentSwipeToRefresh?.isRefreshing = false
    }

    override fun postsNotFound() {
        view?.AccountFragmentSwipeToRefresh?.isRefreshing = false
        view?.AccountFragmentLoading?.text = "No Posts Found"
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(requireContext(), v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.AccountFragmentEditOption -> {
                    val intent = Intent(con, EditAccountInfo::class.java)
                    intent.putExtra(Constants.IntentAccount, account)
                    startActivityForResult(intent, Constants.EditAccountCode)
                }
                R.id.AccountFragmentLogOutOption -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Log Out")
                        .setMessage("Are you sure you want to Log Out of your account?")
                        .setNegativeButton("Cancel") { dialog, which ->
                            dialog.dismiss()
                        }
                        .setPositiveButton("Log Out") { dialog, which ->
                            Constants.changeLocalTokenTo(con, "")
                            startActivity(Intent(activity, SplashScreen::class.java))
                            activity?.finish()
                        }
                        .show()
                }
                R.id.AccountFragmentMoreInfoOption -> {
                    Log.i("log", "More Info")
                }
            }
            true
        }
        popup.setOnDismissListener {
            // Respond to popup being dismissed.
        }
        // Show the popup menu.
        popup.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.EditAccountCode) {
            if (data?.getParcelableExtra<Account>(Constants.IntentAccount) != null) {
                account = data.getParcelableExtra(Constants.IntentAccount)
                (con as MainActivity).myAccount = account
                mariaDB.editAccount(account!!, this)
                view?.AccountFragmentEmail?.text = account?.email
                view?.AccountFragmentPhone?.text = account?.phone
                view?.AccountFragmentToolBar?.title = account?.name
            }
        }
    }

    override fun editAccountCallback() {
        Toast.makeText(con, "Account Saved Successfully", Toast.LENGTH_SHORT).show()
    }

    companion object {
        @JvmStatic
        fun newInstance(context: Context, account: Account?) =
            AccountFragment(context).apply {
                arguments = Bundle().apply {
                    putParcelable(Constants.IntentAccount, account)
                }
            }
    }
}