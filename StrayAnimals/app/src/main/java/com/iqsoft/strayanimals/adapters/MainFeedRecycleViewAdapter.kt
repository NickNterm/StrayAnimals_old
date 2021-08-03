package com.iqsoft.strayanimals.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.iqsoft.strayanimals.Constants
import com.iqsoft.strayanimals.R
import com.iqsoft.strayanimals.models.Account
import com.iqsoft.strayanimals.models.Post
import kotlinx.android.synthetic.main.post_item_layout.view.*
import java.lang.Exception
import java.net.URL


class MainFeedRecycleViewAdapter(
    private val context: Context,
    private val posts: ArrayList<Post>,
    private val accounts: ArrayList<Account>
) : RecyclerView.Adapter<MainFeedRecycleViewAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val postAccountNameText: TextView = view.PostAccountName
        val postMoreInfoTextView: TextView = view.PostMoreInfo
        val postImageView: ImageView = view.PostImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.post_item_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]
        holder.postAccountNameText.text =
            accounts.find { it.token == post.account_id.toString() }?.name
        holder.postMoreInfoTextView.text = post.more_info.toString()
        try {
            val url = URL("http://${Constants.mariaDbIp}/${Constants.mariaDbName}/${post.photo}")
            Glide.with(context).load(url).into(holder.postImageView)
        } catch (e: Exception) {
            Log.e("Load", e.toString())
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }
}