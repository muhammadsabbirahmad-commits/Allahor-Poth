package com.sabbirsamol.allahorpoth

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity
import java.io.File
import java.io.FileOutputStream

class PdfReaderActivity : ComponentActivity() {

    private var pdfRenderer: PdfRenderer? = null
    private var parcelFileDescriptor: ParcelFileDescriptor? = null
    private var currentPageIndex = 0
    private lateinit var imageView: ImageView
    private lateinit var pageInfoText: TextView

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val pdfFileName = intent.getStringExtra("pdf_file") ?: "sample.pdf"
        
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#0F172A"))
        }

        val topBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), dp(12), dp(12), dp(12))
            setBackgroundColor(Color.parseColor("#1E293B"))
        }
        topBar.addView(TextView(this).apply {
            text = "← ফিরে যান"
            textSize = 16f
            setTextColor(Color.WHITE)
            setOnClickListener { finish() }
        })
        topBar.addView(TextView(this).apply {
            text = " 📚 বই রিডার"
            textSize = 18f
            setTextColor(Color.parseColor("#38BDF8"))
        })
        root.addView(topBar, LinearLayout.LayoutParams(-1, -2))

        val scroll = ScrollView(this).apply { isFillViewport = true }
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(8), dp(8), dp(8), dp(8))
        }

        pageInfoText = TextView(this).apply {
            textSize = 14f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, dp(8), 0, dp(8))
        }
        container.addView(pageInfoText)

        imageView = ImageView(this).apply {
            adjustViewBounds = true
        }
        container.addView(imageView, LinearLayout.LayoutParams(-1, -2))
        scroll.addView(container)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))

        val controls = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(dp(8), dp(8), dp(8), dp(8))
            setBackgroundColor(Color.parseColor("#1E293B"))
        }

        val btnPrev = Button(this).apply {
            text = "পূর্ববর্তী পৃষ্ঠা"
            setOnClickListener {
                if (currentPageIndex > 0) {
                    currentPageIndex--
                    showPage(currentPageIndex)
                }
            }
        }
        val btnNext = Button(this).apply {
            text = "পরবর্তী পৃষ্ঠা"
            setOnClickListener {
                pdfRenderer?.let {
                    if (currentPageIndex < it.pageCount - 1) {
                        currentPageIndex++
                        showPage(currentPageIndex)
                    }
                }
            }
        }

        controls.addView(btnPrev, LinearLayout.LayoutParams(0, -2, 1f).apply { setMargins(4, 0, 4, 0) })
        controls.addView(btnNext, LinearLayout.LayoutParams(0, -2, 1f).apply { setMargins(4, 0, 4, 0) })
        root.addView(controls, LinearLayout.LayoutParams(-1, -2))

        setContentView(root)
        openPdfFromAssets(pdfFileName)
    }

    private fun openPdfFromAssets(fileName: String) {
        try {
            val file = File(cacheDir, fileName)
            if (!file.exists()) {
                assets.open(fileName).use { input ->
                    FileOutputStream(file).use { output ->
                        input.copyTo(output)
                    }
                }
            }
            parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            parcelFileDescriptor?.let {
                pdfRenderer = PdfRenderer(it)
                showPage(0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            pageInfoText.text = "পিডিএফ ফাইল পাওয়া যায়নি!"
        }
    }

    private fun showPage(index: Int) {
        pdfRenderer?.let { renderer ->
            if (index < 0 || index >= renderer.pageCount) return
            val page = renderer.openPage(index)
            val bitmap = Bitmap.createBitmap(page.width * 2, page.height * 2, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            imageView.setImageBitmap(bitmap)
            page.close()
            pageInfoText.text = "পৃষ্ঠা: ${index + 1} / ${renderer.pageCount}"
        }
    }

    override fun onDestroy() {
        try {
            pdfRenderer?.close()
            parcelFileDescriptor?.close()
        } catch (_: Exception) {}
        super.onDestroy()
    }
}
