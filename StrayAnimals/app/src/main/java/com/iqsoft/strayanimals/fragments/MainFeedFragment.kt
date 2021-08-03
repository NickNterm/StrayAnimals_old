package com.iqsoft.strayanimals.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.iqsoft.strayanimals.Constants
import com.iqsoft.strayanimals.R
import com.iqsoft.strayanimals.activities.MainActivity
import com.iqsoft.strayanimals.adapters.MainFeedRecycleViewAdapter
import com.iqsoft.strayanimals.adapters.MariaDB
import com.iqsoft.strayanimals.adapters.MariaDBInterface
import com.iqsoft.strayanimals.models.Account
import com.iqsoft.strayanimals.models.Post
import kotlinx.android.synthetic.main.main_feed_fragment.view.*
import java.lang.Exception
import java.sql.Struct

class MainFeedFragment(private val con: Context) : Fragment(), MariaDBInterface {
    // TODO: Rename and change types of parameters
    private var posts: ArrayList<Post> = ArrayList()
    private var accounts: ArrayList<Account> = ArrayList()
    private var account: Account? = null
    private var recyclerViewAdapter: MainFeedRecycleViewAdapter? = null
    private lateinit var mariaDB: MariaDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            account = it.getParcelable(Constants.IntentAccount)
            posts = it.getParcelableArrayList(Constants.IntentPosts)!!
            accounts = it.getParcelableArrayList(Constants.IntentAccountArray)!!
        }
        mariaDB = MariaDB(con)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        recyclerViewAdapter = MainFeedRecycleViewAdapter(con,posts,accounts)
        val lf = requireActivity().layoutInflater
        val v: View = lf.inflate(R.layout.main_feed_fragment, container, false)
        v.MainFeedFragmentRecycleView.layoutManager = LinearLayoutManager(context)
        v.MainFeedFragmentRecycleView.adapter = recyclerViewAdapter
        v.MainFeedFragmentSwipeToRefresh.setProgressBackgroundColorSchemeColor(resources.getColor(R.color.backgroundGray))
        v.MainFeedFragmentSwipeToRefresh.setColorSchemeColors(
            ContextCompat.getColor(con, R.color.accentColor),
            ContextCompat.getColor(con, R.color.white)
        )
        v.MainFeedFragmentSwipeToRefresh.setOnRefreshListener {
            mariaDB.getPosts(this)
        }

        return v
    }

    override fun postsReadCallback(result: ArrayList<Post>) {
        posts = result
        val accs = ArrayList<String>()
        for (i in result) {
            accs.add(i.account_id!!.toString())
        }
        var lastAccounts: ArrayList<String>
        try {
            lastAccounts = accs.distinct() as ArrayList<String>
        }catch (e: Exception){
            lastAccounts = ArrayList()
            lastAccounts.add(accs[0])
        }
        mariaDB.getAccountById(lastAccounts, this)
    }

    override fun accountReadCallback(result: ArrayList<Account>) {
        accounts = result
        recyclerViewAdapter = MainFeedRecycleViewAdapter(con,posts,accounts)
        (activity as MainActivity).postsList = posts
        (activity as MainActivity).accountsList = accounts
        view?.MainFeedFragmentRecycleView?.adapter = recyclerViewAdapter
        view?.MainFeedFragmentSwipeToRefresh?.isRefreshing = false
    }

    companion object {
        @JvmStatic
        fun newInstance(context: Context, acc: Account?, postList: ArrayList<Post>, accList: ArrayList<Account>) =
            MainFeedFragment(context).apply {
                arguments = Bundle().apply {
                    putParcelable(Constants.IntentAccount, acc)
                    putParcelableArrayList(Constants.IntentPosts, postList)
                    putParcelableArrayList(Constants.IntentAccountArray, accList)
                }
            }
    }
}