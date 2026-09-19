package com.example.hostchecker

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.PopupWindow

class MainActivity : Activity() {
    private lateinit var root: FrameLayout
    private lateinit var canvas: HostCheckerView
    private lateinit var edit: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(24, 116, 139)
        window.navigationBarColor = Color.BLACK
        root = FrameLayout(this)
        canvas = HostCheckerView(this)
        root.addView(canvas, FrameLayout.LayoutParams(-1, -1))

        edit = EditText(this).apply {
            setTextColor(Color.rgb(232, 225, 232))
            setHintTextColor(Color.TRANSPARENT)
            textSize = 15f
            isSingleLine = true
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI
            setPadding(dp(4), 0, dp(4), 0)
            background = ColorDrawable(Color.TRANSPARENT)
            includeFontPadding = false
        }
        root.addView(edit)
        edit.setOnFocusChangeListener { _, _ -> canvas.invalidate() }
        edit.addTextChangedListener(SimpleTextWatcher { canvas.invalidate() })
        setContentView(root)
        root.post { positionEdit() }
    }

    private fun positionEdit() {
        val lp = edit.layoutParams as FrameLayout.LayoutParams
        lp.leftMargin = dp(20)
        lp.topMargin = dp(86)
        lp.width = dp(220)
        lp.height = dp(30)
        edit.layoutParams = lp
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density + .5f).toInt()

    private fun showKeyboard() {
        edit.requestFocus()
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun popup(items: List<String>, x: Float, y: Float, widthDp: Int, onPick: (String) -> Unit) {
        val box = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(55, 55, 55))
            elevation = dp(8).toFloat()
        }
        val pw = PopupWindow(box, dp(widthDp), -2, true)
        items.forEach { item ->
            val t = android.widget.TextView(this).apply {
                text = item; textSize = 16f; setTextColor(Color.WHITE)
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(16), dp(12), dp(16), dp(12))
                setOnClickListener { onPick(item); pw.dismiss() }
            }
            box.addView(t, android.widget.LinearLayout.LayoutParams(-1, dp(46)))
        }
        pw.setBackgroundDrawable(ColorDrawable(Color.rgb(55, 55, 55)))
        pw.showAtLocation(root, Gravity.TOP or Gravity.LEFT, x.toInt(), y.toInt())
    }

    private inner class HostCheckerView(ctx: Context) : View(ctx) {
        private val teal = Color.rgb(24, 116, 139)
        private val bg = Color.rgb(18, 18, 18)
        private val primary = Color.rgb(232, 225, 232)
        private val secondary = Color.rgb(200, 194, 201)
        private val divider = Color.rgb(58, 58, 58)
        private val p = Paint(Paint.ANTI_ALIAS_FLAG)
        private lateinit var drawCanvas: Canvas
        private var proxy = false
        private var method = "GET"
        private var header = "Header"
        private var checked = false
        private val response = listOf(
            "GET - URL: http://ssl.cloudflaressl.com",
            "http://ssl.cloudflaressl.com - Direct",
            "HTTP/1.1 301 Moved Permanently",
            "CF-RAY: a3d51ffdebbb40e4-SIN",
            "Connection: keep-alive",
            "Content-Type: text/html; charset=UTF-8",
            "Date: Sat, 19 Sep 2026 02:22:50 GMT",
            "Location: https://www.cloudflare.com/",
            "Server: cloudflare",
            "set-cookie:",
            "__cf_bm=oY6iSzGQg1otDbKnF6RYrQ6LSE0xWgL825Y2p3MkWVl-",
            "1789784570.5490334-1.0.1.1-vdo6GPwyenLL.PE5po2edSTM",
            "icfvl6cYCTkTymUM0hzXvg61JIP2H2oUN6E5guVG7feOWU719",
            "KBPJ2mRYU34iZ9aZIgDj7j7B.VY1fN764IGfr7tB2FbFV9WUgHfQZ;",
            "HttpOnly; Path=/; Domain=cloudflaressl.com; Expires=Sat, 19 Sep",
            "2026 02:52:50 GMT",
            "Transfer-Encoding: chunked",
            "X-Android-Received-Millis: 1789784569038",
            "X-Android-Response-Source: NETWORK 301",
            "X-Android-Selected-Protocol: http/1.1",
            "X-Android-Sent-Millis: 1789784568992",
            "--------------------------",
            "Stopped"
        )

        init {
            isFocusable = true
            setBackgroundColor(bg)
        }

        private fun text(s: String, size: Float, color: Int, x: Float, y: Float) {
            p.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
            p.textSize = size * resources.displayMetrics.density
            p.color = color
            drawCanvas.drawText(s, x * resources.displayMetrics.density, y * resources.displayMetrics.density, p)
        }

        override fun onDraw(c: Canvas) {
            super.onDraw(c)
            drawCanvas = c
            val d = resources.displayMetrics.density

            p.color = teal
            c.drawRect(0f, 0f, width.toFloat(), 60f * d, p)

            p.color = Color.WHITE
            p.strokeWidth = 2f * d
            p.style = Paint.Style.STROKE
            p.strokeCap = Paint.Cap.ROUND
            c.drawLine(16f * d, 30f * d, 30f * d, 30f * d, p)
            c.drawLine(16f * d, 30f * d, 24f * d, 22f * d, p)
            c.drawLine(16f * d, 30f * d, 24f * d, 38f * d, p)
            p.style = Paint.Style.FILL

            text("Host Checker", 20f, Color.WHITE, 44f, 35f)

            p.color = Color.WHITE
            c.drawCircle(338f * d, 24f * d, 2f * d, p)
            c.drawCircle(338f * d, 30f * d, 2f * d, p)
            c.drawCircle(338f * d, 36f * d, 2f * d, p)

            val iL = 14f * d; val iT = 68f * d; val iR = 242f * d; val iB = 114f * d
            p.style = Paint.Style.STROKE
            p.strokeWidth = 1.5f * d
            p.color = teal
            c.drawRoundRect(iL, iT, iR, iB, 4f * d, 4f * d, p)
            p.style = Paint.Style.FILL

            p.color = bg
            c.drawRect(18f * d, 64f * d, 162f * d, 71f * d, p)

            text("URL (eg: www.facebook.com)", 11f, teal, 20f, 69f)

            text(method, 16f, primary, 254f, 97f)
            p.color = primary
            val dp1 = Path(); dp1.moveTo(302f * d, 93f * d); dp1.lineTo(310f * d, 93f * d); dp1.lineTo(306f * d, 99f * d); dp1.close(); c.drawPath(dp1, p)

            p.style = Paint.Style.STROKE
            p.strokeWidth = 1.5f * d
            p.color = primary
            c.drawRect(18f * d, 124f * d, 32f * d, 138f * d, p)
            p.style = Paint.Style.FILL

            text("Proxy", 16f, primary, 38f, 137f)

            text(header, 16f, primary, 208f, 137f)
            p.color = primary
            val dp2 = Path(); dp2.moveTo(302f * d, 133f * d); dp2.lineTo(310f * d, 133f * d); dp2.lineTo(306f * d, 139f * d); dp2.close(); c.drawPath(dp2, p)

            p.color = teal
            c.drawRoundRect(14f * d, 152f * d, width - 14f * d, 192f * d, 22f * d, 22f * d, p)
            p.textAlign = Paint.Align.CENTER
            p.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
            p.textSize = 16f * d
            p.color = Color.WHITE
            c.drawText("Check", width / 2f, 178f * d, p)
            p.textAlign = Paint.Align.LEFT

            if (checked) drawResponse(c)
        }

        private fun drawResponse(c: Canvas) {
            val d = resources.displayMetrics.density
            var y = 204f * d
            p.strokeWidth = 1f * d
            for ((i, s) in response.withIndex()) {
                if (i == 0) {
                    p.typeface = Typeface.DEFAULT_BOLD
                    val boldPart = "GET - URL:"
                    val rest = " http://ssl.cloudflaressl.com"
                    p.textSize = 13f * d; p.color = secondary; p.typeface = Typeface.DEFAULT_BOLD
                    c.drawText(boldPart, 6f * d, y, p)
                    val bw = p.measureText(boldPart)
                    p.typeface = Typeface.DEFAULT
                    c.drawText(rest, 6f * d + bw, y, p)
                } else {
                    val isBold = s == "Stopped"
                    p.typeface = if (isBold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
                    text(s, 13f, if (isBold) primary else secondary, 6f, y / d)
                }
                p.color = divider
                c.drawRect(6f * d, y + 10f * d, width - 6f * d, y + 11f * d, p)
                y += 22f * d
            }
        }

        override fun onTouchEvent(e: android.view.MotionEvent): Boolean {
            if (e.action != MotionEvent.ACTION_UP) return true
            val d = resources.displayMetrics.density
            val x = e.x / d; val y = e.y / d
            when {
                y in 66f..116f && x < 242f -> { showKeyboard(); return true }
                y in 66f..116f && x >= 242f -> { popup(listOf("GET", "POST", "HEAD"), 242f * d, 68f * d, 90) { method = it; invalidate() }; return true }
                y in 120f..142f && x < 180f -> { proxy = !proxy; invalidate(); return true }
                y in 120f..142f && x >= 180f -> { popup(listOf("Header", "User-Agent", "Cookie"), 180f * d, 114f * d, 120) { header = it; invalidate() }; return true }
                y in 148f..196f -> { checked = true; invalidate(); return true }
            }
            return true
        }
    }

    private val density get() = resources.displayMetrics.density

    private class SimpleTextWatcher(val f: () -> Unit) : android.text.TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
        override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) { f() }
        override fun afterTextChanged(s: android.text.Editable?) {}
    }
}
