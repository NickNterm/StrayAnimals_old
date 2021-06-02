package com.iqsoft.strayanimals.adapters

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.iqsoft.strayanimals.Constants
import com.iqsoft.strayanimals.models.Account
import com.iqsoft.strayanimals.models.Post
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONObject


class MariaDB(val context: Context) {
    fun getAccount(token: String, callback: MariaDBInterface) {
        val url =
            "http://${Constants.mariaDbIp}/${Constants.mariaDbName}/${Constants.accountPHPFile}?${Constants.PHPGetTask}=${Constants.ReadAccountByToken}&${Constants.PHPGetValue}=$token"
        Log.i("PHP", url)
        AsyncHttpClient().get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                val rs = String(responseBody!!)
                try {
                    if (rs == "null") {
                        callback.accountNullCallback()
                    } else {
                        val obj = JSONObject(rs)
                        if (obj.getInt(Constants.AccountBanned) == 0) {
                            val account = Account(
                                obj.getString("token"),
                                obj.getString("name"),
                                obj.getString("phone"),
                                obj.getString("email"),
                                null
                            )
                            callback.getAccountCallback(account)
                        } else {
                            callback.accountBannedCallback(obj.getInt(Constants.AccountBanned))
                        }
                    }
                } catch (e: Exception) {

                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                Toast.makeText(context, "Server is down please try again later", Toast.LENGTH_LONG)
                    .show()
            }
        })

    }

    fun getAccountById(idList: ArrayList<String>, callback: MariaDBInterface) {
        val csvList = StringBuilder()
        csvList.append("(")
        for (s in idList) {
            csvList.append("'")
            csvList.append(s)
            csvList.append("',")
        }
        csvList.append("'0')")
        val url =
            "http://${Constants.mariaDbIp}/${Constants.mariaDbName}/${Constants.accountPHPFile}?${Constants.PHPGetTask}=${Constants.ReadAccountById}&${Constants.PHPGetValue}=$csvList"
        Log.i("PHP", url)
        AsyncHttpClient().get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                val rs = String(responseBody!!)
                try {
                    if (rs == "null") {
                        callback.accountNullCallback()
                    } else {
                        val arrayAccount = JSONArray(rs)
                        val accountArray = ArrayList<Account>()
                        for (i in 0 until arrayAccount.length()) {
                            val acc = Account(
                                arrayAccount.getJSONObject(i).getString("token"),
                                arrayAccount.getJSONObject(i).getString("name"),
                                arrayAccount.getJSONObject(i).getString("phone"),
                                arrayAccount.getJSONObject(i).getString("email"),
                                null
                            )
                            Log.i("PHP", "add Post")
                            accountArray.add(acc)
                        }
                        callback.accountRead(accountArray)
                        Log.i("PHP", "call")
                    }
                } catch (e: Exception) {

                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                Toast.makeText(context, "Server is down please try again later", Toast.LENGTH_LONG)
                    .show()
            }
        })

    }

    fun createAccount(account: Account, callback: MariaDBInterface) {
        val gson = Gson()
        val json: String = gson.toJson(account)
        val url =
            "http://${Constants.mariaDbIp}/${Constants.mariaDbName}/${Constants.accountPHPFile}?${Constants.PHPGetTask}=${Constants.CreateAccount}&${Constants.PHPGetValue}=$json"
        AsyncHttpClient().get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                val rs = String(responseBody!!)
                Log.i("PHP", rs)
                callback.createAccountCallback()
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                Toast.makeText(context, "Server is down please try again later", Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

    fun getPosts(callback: MariaDBInterface) {
        val url =
            "http://${Constants.mariaDbIp}/${Constants.mariaDbName}/${Constants.postPHPFile}?${Constants.PHPGetTask}=${Constants.ReadPosts}"
        Log.i("PHP", url)
        AsyncHttpClient().get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                val rs = String(responseBody!!)
                try {
                    Log.i("PHP", rs)
                    if (rs == "null") {
                        callback.accountNullCallback()
                    } else {
                        val arrayPosts = JSONArray(rs)
                        val postArray = ArrayList<Post>()
                        for (i in 0 until arrayPosts.length()) {
                            val post = Post(
                                arrayPosts.getJSONObject(i).getString("accountId"),
                                arrayPosts.getJSONObject(i).getString("photoLocation"),
                                arrayPosts.getJSONObject(i).getString("moreInfo")
                            )
                            Log.i("PHP", "add Post")
                            postArray.add(post)
                        }
                        callback.postsRead(postArray)
                        Log.i("PHP", "call")
                    }
                } catch (e: Exception) {

                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                Toast.makeText(context, "Server is down please try again later", Toast.LENGTH_LONG)
                    .show()
            }
        })

    }
}