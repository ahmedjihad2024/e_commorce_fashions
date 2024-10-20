package com.example.e_commorce_fashions.app.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStorePreferences(private val dataStore: DataStore<Preferences>) {
    private object PreferencesKeys {
        val ON_BOARDING_VIEW = intPreferencesKey("on_boarding_view")
        val USER_TOKEN = stringPreferencesKey("user_token")
    }

    suspend fun setUserToken(token: String) {
        dataStore.updateData {
            it.toMutablePreferences().apply {
                this[PreferencesKeys.USER_TOKEN] = token
            }
        }
    }

    suspend fun clearUserToken() {
        dataStore.updateData {
            it.toMutablePreferences().apply {
                this.remove(PreferencesKeys.USER_TOKEN)
            }
        }
    }

    fun getUserToken(): Flow<String?> = dataStore.data.map {
        it[PreferencesKeys.USER_TOKEN]
    }

    fun isUserAuth(): Flow<Boolean> = dataStore.data.map {
        it[PreferencesKeys.USER_TOKEN] != null
    }

    suspend fun setFirstLaunch(value: Boolean) {
        dataStore.updateData {
            it.toMutablePreferences().apply {
                this[PreferencesKeys.ON_BOARDING_VIEW] = if (value) 1 else 0
            }
        }
    }

    fun isFirstLaunch(): Flow<Boolean> = dataStore.data.map {
        val temp = it[PreferencesKeys.ON_BOARDING_VIEW]
        temp == null || temp == 0
    }
}