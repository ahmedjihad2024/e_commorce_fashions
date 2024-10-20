package com.example.e_commorce_fashions.app

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import com.example.e_commorce_fashions.MainActivity
import com.example.e_commorce_fashions.app.utils.Di
import com.example.e_commorce_fashions.app.utils.Language
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale

class AppState : ViewModel() {
    data class State(
        val isDark: Boolean? = false
    )

    private val _state = MutableStateFlow(State())
    val state get() = _state.asStateFlow()
    val stateCopy get() = _state.value.copy()

    fun setTheme(isDark: Boolean?) {
        _state.update { it.copy(isDark = isDark) }
    }

    fun setLanguage(context: Context, language: Language) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java).applicationLocales =
                LocaleList(Locale.forLanguageTag(language.languageTag))
        } else {
            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(language.languageTag)
            AppCompatDelegate.setApplicationLocales(appLocale)
            if (context is MainActivity) {
                context.recreate()
            }
        }
    }

    fun getLanguage(context: Context): Language {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java).applicationLocales.get(0)?.let {
                Di.appLanguages.languagesGroup.find { language -> it.language == language.languageCode && it.country == language.countryCode }
            } ?: Di.appLanguages.english
        } else {
            AppCompatDelegate.getApplicationLocales().get(0)?.let {
                Di.appLanguages.languagesGroup.find { language -> it.language == language.languageCode && it.country == language.countryCode }
            } ?: Di.appLanguages.english
        }
    }
}