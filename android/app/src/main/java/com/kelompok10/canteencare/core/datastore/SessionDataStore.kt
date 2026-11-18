package com.kelompok10.canteencare.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.kelompok10.canteencare.domain.model.Role
import com.kelompok10.canteencare.domain.model.UserSession
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.sessionStore: DataStore<Preferences> by preferencesDataStore(name = "session")

/** Menyimpan sesi login (token, id, nama, peran) secara permanen di HP memakai DataStore. */
@Singleton
class SessionDataStore @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val store = context.sessionStore

    /** Sesi saat ini, atau null jika belum login. Otomatis memancarkan nilai baru saat sesi berubah. */
    val session: Flow<UserSession?> = store.data.map { prefs ->
        val token = prefs[KEY_TOKEN] ?: return@map null
        UserSession(
            token = token,
            userId = prefs[KEY_USER_ID] ?: return@map null,
            fullName = prefs[KEY_FULL_NAME].orEmpty(),
            role = prefs[KEY_ROLE]?.let { runCatching { Role.valueOf(it) }.getOrNull() } ?: return@map null,
        )
    }

    val token: Flow<String?> = store.data.map { it[KEY_TOKEN] }

    suspend fun save(session: UserSession) {
        store.edit { prefs ->
            prefs[KEY_TOKEN] = session.token
            prefs[KEY_USER_ID] = session.userId
            prefs[KEY_FULL_NAME] = session.fullName
            prefs[KEY_ROLE] = session.role.name
        }
    }

    suspend fun clear() {
        store.edit { it.clear() }
    }

    private companion object {
        val KEY_TOKEN = stringPreferencesKey("token")
        val KEY_USER_ID = intPreferencesKey("user_id")
        val KEY_FULL_NAME = stringPreferencesKey("full_name")
        val KEY_ROLE = stringPreferencesKey("role")
    }
}
