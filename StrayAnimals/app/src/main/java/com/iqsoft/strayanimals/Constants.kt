package com.iqsoft.strayanimals

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

object Constants {
    const val mariaDbIp = "94.70.212.147:3336"
    //const val mariaDbIp = "192.168.50.9"
    const val mariaDbName = "StrayAnimals"

    const val accountPHPFile = "Account.php"
    const val ReadAccountByToken = "ReadAccountByToken"
    const val ReadAccountById = "ReadAccountById"
    const val CreateAccount = "SaveAccount"
    const val EditAccount = "EditAccount"

    const val postPHPFile = "Post.php"
    const val ReadPosts = "ReadPosts"
    const val ReadPostsFromToken = "ReadPostsFromToken"
    const val CreatePost = "CreatePost"

    const val PHPGetTask = "task"
    const val PHPGetValue = "value"

    const val UploadPhotoPostToken = "token"
    const val UploadPhotoPostDescription = "description"
    const val UploadPhotoPostImage = "image"

    const val sharedPrefToken = "token"

    const val AccountToken = "token"
    const val AccountName = "name"
    const val AccountPhone = "phone"
    const val AccountEmail = "email"
    const val AccountMoreInfo = "MoreInfo"
    const val AccountBanned = "banned"

    const val IntentToken = "token"
    const val IntentAccount = "account"
    const val IntentAccountArray = "accountList"
    const val IntentPosts = "postsArray"

    const val SignInCode = 1
    const val SelectPhotoCode = 2
    const val EditAccountCode = 3

    fun changeLocalTokenTo(context: Context, token: String){
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(sharedPrefToken,  token)
        editor.apply()
    }
}