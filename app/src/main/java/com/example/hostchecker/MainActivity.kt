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
            textSize = 19f
            isSingleLine = true
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI
            setPadding(dp(16), 0, dp(10), 0)
            background = ColorDrawable(Color.TRANSPARENT)
            includeFontPadding = true
        }
        root.addView(edit)
        edit.setOnFocusChangeListener { _, _ -> canvas.invalidate() }
        edit.addTextChangedListener(SimpleTextWatcher { canvas.invalidate() })
        setContentView(root)
        root.post { positionEdit() }
    }

    private fun positionEdit() {
        val lp = edit.layoutParams as FrameLayout.LayoutParams
        lp.leftMargin = dp(18)
        lp.topMargin = dp(90)
        lp.width = dp(228)
        lp.height = dp(34)
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
                text = item; textSize = 18f; setTextColor(Color.WHITE)
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(20), dp(13), dp(20), dp(13))
                setOnClickListener { onPick(item); pw.dismiss() }
            }
            box.addView(t, android.widget.LinearLayout.LayoutParams(-1, dp(52)))
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
            p.typeface = Typeface.create("sans", Typeface.NORMAL)
            p.textSize = dp(size)
            p.color = color
            drawCanvas.drawText(s, dp(x), dp(y), p)
        }

        private fun dp(v: Float) = v * resources.displayMetrics.density

        override fun onDraw(c: Canvas) {
            super.onDraw(c)
            drawCanvas = c

            p.color = teal
            c.drawRect(0f, 0f, width.toFloat(), dp(64f), p)

            p.color = Color.WHITE
            p.strokeWidth = dp(2.5f)
            p.style = Paint.Style.STROKE
            p.strokeCap = Paint.Cap.ROUND
            c.drawLine(dp(20f), dp(32f), dp(36f), dp(32f), p)
            c.drawLine(dp(20f), dp(32f), dp(30f), dp(22f), p)
            c.drawLine(dp(20f), dp(32f), dp(30f), dp(42f), p)
            p.style = Paint.Style.FILL

            text("Host Checker", 22f, Color.WHITE, 52f, 40f)

            p.color = Color.WHITE
            c.drawCircle(dp(340f), dp(24f), dp(2.5f), p)
            c.drawCircle(dp(340f), dp(32f), dp(2.5f), p)
            c.drawCircle(dp(340f), dp(40f), dp(2.5f), p)

            val inputLeft = dp(10f)
            val inputTop = dp(80f)
            val inputRight = dp(248f)
            val inputBottom = dp(124f)
            p.style = Paint.Style.STROKE
            p.strokeWidth = dp(2f)
            p.color = teal
            c.drawRoundRect(inputLeft, inputTop, inputRight, inputBottom, dp(4f), dp(4f), p)
            p.style = Paint.Style.FILL

            p.color = bg
            c.drawRect(dp(14f), dp(76f), dp(170f), dp(84f), p)

            text("URL (eg: www.facebook.com)", 11f, teal, 16f, 82f)

            p.color = teal
            p.strokeWidth = dp(1.5f)
            p.style = Paint.Style.STROKE
            c.drawLine(dp(20f), dp(94f), dp(20f), dp(116f), p)
            p.style = Paint.Style.FILL

            text(method, 16f, primary, 256f, 106f)
            drawDown(c, 312f, 102f)

            p.style = Paint.Style.STROKE
            p.strokeWidth = dp(2f)
            p.color = primary
            c.drawRect(dp(14f), dp(136f), dp(30f), dp(152f), p)
            p.style = Paint.Style.FILL

            text("Proxy", 16f, primary, 36f, 151f)

            text(header, 16f, primary, 210f, 151f)
            drawDown(c, 312f, 147f)

            p.color = teal
            c.drawRoundRect(dp(14f), dp(166f), width - dp(14f), dp(206f), dp(24f), dp(24f), p)
            p.textAlign = Paint.Align.CENTER
            p.textSize = dp(17f)
            p.color = Color.WHITE
            c.drawText("Check", width / 2f, dp(191f), p)
            p.textAlign = Paint.Align.LEFT

            if (checked) drawResponse(c)
        }

        private fun drawDown(c: Canvas, x: Float, y: Float) {
            p.color = primary
            val path = Path()
            path.moveTo(dp(x - 5f), dp(y - 2f))
            path.lineTo(dp(x + 5f), dp(y - 2f))
            path.lineTo(dp(x), dp(y + 4f))
            path.close()
            c.drawPath(path, p)
        }

        private fun drawResponse(c: Canvas) {
            var y = dp(220f)
            p.strokeWidth = dp(1f)
            for ((i, s) in response.withIndex()) {
                if (i == 0) {
                    p.typeface = Typeface.DEFAULT
                    text(s, 16f, secondary, 6f, y / dp(1f))
                } else {
                    text(s, 16f, if (s == "Stopped") primary else secondary, 6f, y / dp(1f))
                }
                p.color = divider
                c.drawRect(dp(6f), y + dp(8f), width - dp(6f), y + dp(9f), p)
                y += dp(20f)
            }
            p.typeface = Typeface.DEFAULT_BOLD
        }

        override fun onTouchEvent(e: android.view.MotionEvent): Boolean {
            if (e.action != MotionEvent.ACTION_UP) return true
            val x = e.x / density
            val y = e.y / density
            when {
                y in 78f..126f && x < 248f -> { showKeyboard(); return true }
                y in 78f..126f && x >= 248f -> { popup(listOf("GET", "POST", "HEAD"), dp(248f), dp(80f), 100) { method = it; invalidate() }; return true }
                y in 130f..158f && x < 200f -> { proxy = !proxy; invalidate(); return true }
                y in 130f..158f && x >= 200f -> { popup(listOf("Header", "User-Agent", "Cookie"), dp(200f), dp(124f), 140) { header = it; invalidate() }; return true }
                y in 162f..210f -> { checked = true; invalidate(); return true }
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
