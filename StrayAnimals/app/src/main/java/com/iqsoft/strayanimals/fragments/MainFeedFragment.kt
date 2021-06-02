package com.iqsoft.strayanimals.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.iqsoft.strayanimals.Constants
import com.iqsoft.strayanimals.R
import com.iqsoft.strayanimals.adapters.MainFeedRecycleViewAdapter
import com.iqsoft.strayanimals.adapters.MariaDB
import com.iqsoft.strayanimals.adapters.MariaDBInterface
import com.iqsoft.strayanimals.models.Account
import com.iqsoft.strayanimals.models.Post
import kotlinx.android.synthetic.main.main_feed_fragment.view.*
import java.sql.Struct

class MainFeedFragment : Fragment(), MariaDBInterface {
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
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mariaDB = context?.let { MariaDB(it) }!!
        mariaDB.getPosts(this)
        recyclerViewAdapter = context?.let { MainFeedRecycleViewAdapter(it,posts,accounts) }
        val lf = requireActivity().layoutInflater
        val v: View = lf.inflate(R.layout.main_feed_fragment, container, false)
        v.MainFeedRecycleView.layoutManager = LinearLayoutManager(context)
        v.MainFeedRecycleView.adapter = recyclerViewAdapter
        return v
    }

    override fun postsRead(posts: ArrayList<Post>) {
        this.posts.clear()
        this.posts.addAll(posts)
        val accs = ArrayList<String>()
        for(i in this.posts){
            accs.add(i.accountId!!.toString())
        }
        accs.distinct()
        mariaDB.getAccountById(accs,this)
    }

    override fun accountRead(accounts: ArrayList<Account>) {
        this.accounts.clear()
        this.accounts.addAll(accounts)
        recyclerViewAdapter?.notifyDataSetChanged()
    }
    companion object {
        @JvmStatic
        fun newInstance(acc: Account) =
            MainFeedFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Constants.IntentAccount, acc)
                }
            }
    }
}