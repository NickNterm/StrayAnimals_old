package com.iqsoft.strayanimals.adapters

import android.os.AsyncTask
import android.util.Log
import java.sql.DriverManager

class MariaDB(): AsyncTask<String, Void, String>() {
    private fun isValidToken(token:String): String {
        val set =
            DriverManager.getConnection("jdbc:mariadb://192.168.50.29/StrayDogs", "nick", "iqsoft")
                .createStatement().executeQuery("SELECT * FROM login")

        if (set.next()) {
            Log.i("GotMariaDB", set.getString(3))
            //your logic...
        }
        return set.getString(3)
    }

    override fun doInBackground(vararg params: String?): String {

        return isValidToken(params.toString())
    }
}