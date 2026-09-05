package com.sabbirsamol.allahorpoth

import android.content.Context
import android.graphics.Color

data class ThemeColors(
    val bgMain: Int,
    val cardBg: Int,
    val cardStroke: Int,
    val textAccent: Int,
    val btnBg: Int,
    val textMain: Int,
    val textSub: Int
)

object ThemeManager {
    fun getTheme(context: Context): ThemeColors {
        val prefs = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val themeMode = prefs.getString("app_theme", "dark")

        return if (themeMode == "light") {
            ThemeColors(
                bgMain = Color.parseColor("#F8FAFC"),
                cardBg = Color.WHITE,
                cardStroke = Color.parseColor("#CBD5E1"),
                textAccent = Color.parseColor("#047857"),
                btnBg = Color.parseColor("#FACC15"),
                textMain = Color.parseColor("#0F172A"),
                textSub = Color.parseColor("#475569")
            )
        } else {
            ThemeColors(
                bgMain = Color.parseColor("#0F172A"),
                cardBg = Color.parseColor("#1E293B"),
                cardStroke = Color.parseColor("#334155"),
                textAccent = Color.parseColor("#34D399"),
                btnBg = Color.parseColor("#FACC15"),
                textMain = Color.parseColor("#F8FAFC"),
                textSub = Color.parseColor("#94A3B8")
            )
        }
    }
}
