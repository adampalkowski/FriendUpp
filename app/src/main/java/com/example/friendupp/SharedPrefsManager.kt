package com.example.friendupp

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SharedPreferencesManager {
    private const val PREFS_NAME = "MyPrefs"
    private const val KEY_IDS = "ids"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveIds(context: Context, ids: List<String>) {
        val json = Gson().toJson(ids)
        val editor = getSharedPreferences(context).edit()
        editor.putString(KEY_IDS, json)
        editor.apply()
    }

    fun getIds(context: Context): List<String> {
        val json = getSharedPreferences(context).getString(KEY_IDS, null)
        return if (json != null) {
            Gson().fromJson(json, object : TypeToken<List<String>>() {}.type)
        } else {
            emptyList()
        }
    }

    fun addId(context: Context, id: String) {
        val currentIds = getIds(context).toMutableList()
        currentIds.add(id)
        saveIds(context, currentIds)
    }

    fun removeId(context: Context, id: String) {
        val currentIds = getIds(context).toMutableList()
        currentIds.remove(id)
        saveIds(context, currentIds)
    }
}