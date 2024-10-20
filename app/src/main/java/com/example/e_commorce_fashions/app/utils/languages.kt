package com.example.e_commorce_fashions.app.utils


class AppLanguages {
    val arabic by lazy { Language("Arabic", "ar", "EG") }
    val english by lazy { Language("English", "en", "US") }
    val languagesGroup by lazy {
        listOf(
            arabic, english
        )
    }
}

data class Language(
    val name: String,
    val languageCode: String,
    val countryCode: String
){
    val languageTag get() = "$languageCode-$countryCode"
}