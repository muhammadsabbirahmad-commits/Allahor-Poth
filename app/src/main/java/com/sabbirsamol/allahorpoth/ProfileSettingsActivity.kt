package com.sabbirsamol.allahorpoth

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth

class ProfileSettingsActivity : ComponentActivity() {

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
    private val auth = FirebaseAuth.getInstance()

    private val themeColors by lazy { ThemeManager.getTheme(this) }
    private val bgMain get() = themeColors.bgMain
    private val cardBg get() = themeColors.cardBg
    private val cardStroke get() = themeColors.cardStroke
    private val textYellow get() = themeColors.textAccent
    private val textMain get() = themeColors.textMain
    private val textSub get() = themeColors.textSub
    private val btnYellow get() = themeColors.btnBg

    private fun getCardDrawable() = GradientDrawable().apply {
        setColor(cardBg); setStroke(dp(1), cardStroke); cornerRadius = dp(10).toFloat()
    }
    private fun getBtnDrawable(color: Int) = GradientDrawable().apply {
        setColor(color); cornerRadius = dp(6).toFloat()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bgMain)
        }

        val top = LinearLayout(this).apply {
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), dp(12), dp(12), dp(12))
            background = getCardDrawable()
        }
        top.addView(TextView(this).apply {
            text = "← হোম"
            textSize = 16f; setTextColor(textMain); setPadding(0, 0, dp(12), 0)
            setOnClickListener { finish() }
        })
        top.addView(TextView(this).apply {
            text = "👤 প্রফাইল ও সেটিংস"
            textSize = 18f; setTextColor(textYellow); setTypeface(null, Typeface.BOLD)
        }, LinearLayout.LayoutParams(0, -2, 1f))
        root.addView(top)

        val scroll = ScrollView(this).apply { isFillViewport = true }
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(16), dp(14), dp(80))
        }

        val userCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = getCardDrawable()
            setPadding(dp(14), dp(14), dp(14), dp(14))
            layoutParams = LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(14) }
        }
        userCard.addView(TextView(this).apply {
            text = "👤 ইউজার অ্যাকাউন্ট"
            textSize = 16f; setTextColor(textYellow); setTypeface(null, Typeface.BOLD)
            setPadding(0, 0, 0, dp(8))
        })
        val userEmail = auth.currentUser?.email ?: auth.currentUser?.uid ?: "অতিথি ব্যবহারকারী (Anonymous)"
        userCard.addView(TextView(this).apply {
            text = "আইডি/ইমেইল: $userEmail"
            textSize = 14f; setTextColor(textMain); setPadding(0, 0, 0, dp(12))
        })
        userCard.addView(Button(this).apply {
            text = "লগ আউট / সাইন আউট"
            isAllCaps = false; setTextColor(Color.WHITE)
            background = getBtnDrawable(Color.parseColor("#DC2626"))
            layoutParams = LinearLayout.LayoutParams(-1, dp(40))
            setOnClickListener {
                auth.signOut()
                Toast.makeText(this@ProfileSettingsActivity, "লগ আউট সফল হয়েছে", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
        content.addView(userCard)

        val themeCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = getCardDrawable()
            setPadding(dp(14), dp(14), dp(14), dp(14))
            layoutParams = LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(14) }
        }
        themeCard.addView(TextView(this).apply {
            text = "🎨 থিম সেটিংস"
            textSize = 16f; setTextColor(textYellow); setTypeface(null, Typeface.BOLD)
            setPadding(0, 0, 0, dp(8))
        })
        themeCard.addView(TextView(this).apply {
            text = "অ্যাপের থিম পরিবর্তন করুন:"
            textSize = 14f; setTextColor(textSub); setPadding(0, 0, 0, dp(10))
        })

        val themeBtnRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            weightSum = 2f
        }
        themeBtnRow.addView(Button(this).apply {
            text = "🌙 অন্ধকার থিম"
            isAllCaps = false; setTextColor(Color.BLACK)
            background = getBtnDrawable(btnYellow)
            layoutParams = LinearLayout.LayoutParams(0, dp(40), 1f).apply { rightMargin = dp(6) }
            setOnClickListener {
                getSharedPreferences("AppSettings", Context.MODE_PRIVATE).edit().putString("app_theme", "dark").apply()
                Toast.makeText(this@ProfileSettingsActivity, "থিম পরিবর্তন করা হয়েছে। অ্যাপ রিস্টার্ট করুন।", Toast.LENGTH_SHORT).show()
            }
        })
        themeBtnRow.addView(Button(this).apply {
            text = "☀️ আলো থিম"
            isAllCaps = false; setTextColor(Color.BLACK)
            background = getBtnDrawable(btnYellow)
            layoutParams = LinearLayout.LayoutParams(0, dp(40), 1f).apply { leftMargin = dp(6) }
            setOnClickListener {
                getSharedPreferences("AppSettings", Context.MODE_PRIVATE).edit().putString("app_theme", "light").apply()
                Toast.makeText(this@ProfileSettingsActivity, "থিম পরিবর্তন করা হয়েছে। অ্যাপ রিস্টার্ট করুন।", Toast.LENGTH_SHORT).show()
            }
        })
        themeCard.addView(themeBtnRow)
        content.addView(themeCard)

        scroll.addView(content)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))

        val bottomNav = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#0F172A"))
            setPadding(dp(2), dp(4), dp(2), dp(4))
            elevation = dp(8).toFloat()
        }

        val navItems = listOf(
            Pair("🏠\nহোম", MainActivity::class.java),
            Pair("📿\nতাসবিহ", TasbihActivity::class.java),
            Pair("📚\nলাইব্রেরী", LibraryActivity::class.java),
            Pair("📖\nআমল", MasnunAmolActivity::class.java),
            Pair("📝\nনোটপ্যাড", NotepadActivity::class.java),
            Pair("🔄\nসিঙ্ক", null),
            Pair("👤\nপ্রোফাইল", ProfileSettingsActivity::class.java)
        )

        navItems.forEach { (label, _) ->
            bottomNav.addView(Button(this).apply {
                text = label
                textSize = 10f
                isAllCaps = false
                minHeight = 0
                minWidth = 0
                setPadding(0, 0, 0, 0)
                gravity = Gravity.CENTER
                setTextColor(if (label.contains("প্রোফাইল")) Color.parseColor("#10B981") else Color.parseColor("#9CA3AF"))
                background = GradientDrawable()
                setOnClickListener {
                    when {
                        label.contains("হোম") -> { startActivity(Intent(this@ProfileSettingsActivity, MainActivity::class.java)); finish() }
                        label.contains("তাসবিহ") -> { startActivity(Intent(this@ProfileSettingsActivity, TasbihActivity::class.java)); finish() }
                        label.contains("লাইব্রেরী") -> { startActivity(Intent(this@ProfileSettingsActivity, LibraryActivity::class.java)); finish() }
                        label.contains("আমল") -> { startActivity(Intent(this@ProfileSettingsActivity, MasnunAmolActivity::class.java)); finish() }
                        label.contains("নোটপ্যাড") -> { startActivity(Intent(this@ProfileSettingsActivity, NotepadActivity::class.java)); finish() }
                        label.contains("সিঙ্ক") -> { Toast.makeText(this@ProfileSettingsActivity, "প্রোফাইল ডেটা সিঙ্ক করা হয়েছে!", Toast.LENGTH_SHORT).show() }
                        label.contains("প্রোফাইল") -> {}
                    }
                }
            }, LinearLayout.LayoutParams(0, dp(52), 1f).apply { setMargins(dp(2), 0, dp(2), 0) })
        }
        root.addView(bottomNav, LinearLayout.LayoutParams(-1, dp(60)))

        setContentView(root)
    }
}
