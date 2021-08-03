package com.iqsoft.strayanimals.adapters

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import android.util.Base64
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
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import javax.net.ssl.HttpsURLConnection


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
                            accountArray.add(acc)
                        }
                        callback.accountReadCallback(accountArray)
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

    fun editAccount(account: Account, callback: MariaDBInterface) {
        val gson = Gson()
        val json: String = gson.toJson(account)
        val url =
            "http://${Constants.mariaDbIp}/${Constants.mariaDbName}/${Constants.accountPHPFile}?${Constants.PHPGetTask}=${Constants.EditAccount}&${Constants.PHPGetValue}=$json"
        Log.i("PHP", url)
        AsyncHttpClient().get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                val rs = String(responseBody!!)
                callback.editAccountCallback()
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
                            postArray.add(post)
                        }
                        callback.postsReadCallback(postArray)
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

    fun getPostsFromToken(token: String, callback: MariaDBInterface) {
        val url =
            "http://${Constants.mariaDbIp}/${Constants.mariaDbName}/${Constants.postPHPFile}?${Constants.PHPGetTask}=${Constants.ReadPostsFromToken}&${Constants.PHPGetValue}=$token"
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
                        callback.postsNotFound()
                    } else {
                        val arrayPosts = JSONArray(rs)
                        val postArray = ArrayList<Post>()
                        for (i in 0 until arrayPosts.length()) {
                            val post = Post(
                                arrayPosts.getJSONObject(i).getString("accountId"),
                                arrayPosts.getJSONObject(i).getString("photoLocation"),
                                arrayPosts.getJSONObject(i).getString("moreInfo")
                            )
                            postArray.add(post)
                        }
                        callback.postsReadFromTokenCallback(postArray)
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

    private var progressDialog: ProgressDialog? = null
    private var ServerUploadPath =
        "http://${Constants.mariaDbIp}/${Constants.mariaDbName}/${Constants.postPHPFile}"

    fun imageUploadToServerFunction(bitmap: Bitmap, description: String, token: String) {
        val byteArrayOutputStreamObject = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject)
        val byteArrayVar = byteArrayOutputStreamObject.toByteArray()
        val ConvertImage: String = Base64.encodeToString(byteArrayVar, Base64.DEFAULT)

        class AsyncTaskUploadClass :
            AsyncTask<Void?, Void?, String?>() {
            override fun onPreExecute() {
                super.onPreExecute()
                progressDialog = ProgressDialog.show(
                    context,
                    "Image is Uploading",
                    "Please Wait",
                    false,
                    false
                )
            }

            override fun onPostExecute(string1: String?) {
                super.onPostExecute(string1)
                progressDialog?.dismiss()
                Toast.makeText(context, string1, Toast.LENGTH_LONG).show()
            }

            override fun doInBackground(vararg params: Void?): String {
                val imageProcessClass = ImageProcessClass()
                val HashMapParams =
                    HashMap<String, String>()
                HashMapParams[Constants.UploadPhotoPostToken] = token
                HashMapParams[Constants.UploadPhotoPostDescription] = description
                HashMapParams[Constants.UploadPhotoPostImage] = ConvertImage
                return imageProcessClass.ImageHttpRequest(ServerUploadPath, HashMapParams)
            }
        }

        val AsyncTaskUploadClassOBJ = AsyncTaskUploadClass()
        AsyncTaskUploadClassOBJ.execute()
    }

    class ImageProcessClass {
        var myBoolean = true
        fun ImageHttpRequest(requestURL: String?, PData: HashMap<String, String>): String {
            var stringBuilder = StringBuilder()
            try {
                val bufferedReaderObject: BufferedReader
                val url = URL(requestURL)
                val httpURLConnectionObject = url.openConnection() as HttpURLConnection
                httpURLConnectionObject.readTimeout = 19000
                httpURLConnectionObject.connectTimeout = 19000
                httpURLConnectionObject.requestMethod = "POST"
                httpURLConnectionObject.doInput = true
                httpURLConnectionObject.doOutput = true
                try {
                    val outPutStream = httpURLConnectionObject.outputStream
                    val bufferedWriterObject = BufferedWriter(
                        OutputStreamWriter(outPutStream, "UTF-8")
                    )
                    bufferedWriterObject.write(bufferedWriterDataFN(PData))
                    bufferedWriterObject.flush()
                    bufferedWriterObject.close()
                    outPutStream.close()
                } catch (e: Exception) {
                    Log.e("Upload", e.toString())
                }

                val RC: Int = httpURLConnectionObject.responseCode
                if (RC == HttpsURLConnection.HTTP_OK) {
                    bufferedReaderObject =
                        BufferedReader(InputStreamReader(httpURLConnectionObject.inputStream))
                    stringBuilder = StringBuilder()
                    var RC2: String?
                    while (bufferedReaderObject.readLine().also { RC2 = it } != null) {
                        stringBuilder.append(RC2)
                    }
                    Log.e("Upload", stringBuilder.toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return stringBuilder.toString()
        }

        @Throws(UnsupportedEncodingException::class)
        private fun bufferedWriterDataFN(HashMapParams: HashMap<String, String>): String {
            val stringBuilderObject: StringBuilder = StringBuilder()
            for ((key, value) in HashMapParams.entries) {
                if (myBoolean) {
                    myBoolean = false
                } else {
                    stringBuilderObject.append("&")
                }
                stringBuilderObject.append(URLEncoder.encode(key, "UTF-8"))
                stringBuilderObject.append("=")
                stringBuilderObject.append(URLEncoder.encode(value, "UTF-8"))
            }
            return stringBuilderObject.toString()
        }
    }
}