package com.example.finalprojectresturant.utils

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

var countries = emptyList<String>()

suspend fun getCountries(): List<String> {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("http://api.geonames.org/countryInfoJSON?username=tomer")
        .build()

    if(countries.size > 0) {
        return countries
    }
    return withContext(Dispatchers.IO) {
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Unexpected code $response")

            val jsonString = response.body?.string() ?: throw Exception("Empty response body")
            val jsonObject = JSONObject(jsonString)
            val geonamesArray = jsonObject.getJSONArray("geonames")
            countries = List(geonamesArray.length()) { index ->
                geonamesArray.getJSONObject(index)
            }.filter { jsonObject ->
                val countryName = jsonObject.getString("countryName")
                val population = jsonObject.optLong("population", 0)
                countryName == "Israel" || population > 25_000_000
            }.map { jsonObject ->
                jsonObject.getString("countryName")
            }

            countries
        }
    }

}