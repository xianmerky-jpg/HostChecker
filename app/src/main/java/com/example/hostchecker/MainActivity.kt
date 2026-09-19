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
import kotlin.math.min

class MainActivity : Activity() {
    private lateinit var root: FrameLayout
    private lateinit var canvas: HostCheckerView
    private lateinit var edit: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(24,116,139)
        window.navigationBarColor = Color.BLACK
        root = FrameLayout(this)
        canvas = HostCheckerView(this)
        root.addView(canvas, FrameLayout.LayoutParams(-1,-1))

        edit = EditText(this).apply {
            setTextColor(Color.rgb(232,225,232))
            setHintTextColor(Color.TRANSPARENT)
            textSize = 19f
            singleLine = true
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
        lp.leftMargin = dp(16)
        lp.topMargin = dp(77)
        lp.width = dp(204)
        lp.height = dp(38)
        edit.layoutParams = lp
    }

    private fun dp(v:Int) = (v * resources.displayMetrics.density + .5f).toInt()

    private fun showKeyboard() { edit.requestFocus(); (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT) }

    private fun popup(items: List<String>, x: Float, y: Float, widthDp: Int, onPick:(String)->Unit) {
        val box = android.widget.LinearLayout(this).apply { orientation = android.widget.LinearLayout.VERTICAL; setBackgroundColor(Color.rgb(55,55,55)); elevation=dp(8).toFloat() }
        val pw = PopupWindow(box, dp(widthDp), -2, true)
        items.forEach { item ->
            val t = android.widget.TextView(this).apply { text=item; textSize=18f; setTextColor(Color.WHITE); gravity=Gravity.CENTER_VERTICAL; setPadding(dp(20),dp(13),dp(20),dp(13)); setOnClickListener { onPick(item); pw.dismiss() } }
            box.addView(t, android.widget.LinearLayout.LayoutParams(-1, dp(52)))
        }
        pw.setBackgroundDrawable(ColorDrawable(Color.rgb(55,55,55)))
        pw.showAtLocation(root, Gravity.TOP or Gravity.LEFT, x.toInt(), y.toInt())
    }

    private inner class HostCheckerView(ctx:Context): View(ctx) {
        private val teal=Color.rgb(24,116,139); private val bg=Color.rgb(18,18,18); private val primary=Color.rgb(232,225,232); private val secondary=Color.rgb(200,194,201); private val divider=Color.rgb(58,58,58)
        private val p=Paint(Paint.ANTI_ALIAS_FLAG); private lateinit var drawCanvas: Canvas; private var proxy=false; private var method="GET"; private var header="Header"; private var checked=false
        private val response=listOf(
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
        init { isFocusable=true; setBackgroundColor(bg) }
        private fun text(s:String,size:Float,color:Int,x:Float,y:Float) { p.typeface=Typeface.create("sans",Typeface.NORMAL);p.textSize=dp(size);p.color=color;drawCanvas.drawText(s,dp(x),dp(y),p) }
        private fun dp(v:Float)=v*resources.displayMetrics.density
        override fun onDraw(c:Canvas) { super.onDraw(c); drawCanvas=c; val d=resources.displayMetrics.density
            // Header/status are handled separately by Android; this is the app content below status.
            p.color=teal;c.drawRect(0f,0f,width.toFloat(),dp(64f),p)
            // back arrow
            p.color=Color.WHITE;p.strokeWidth=dp(2.2f);p.style=Paint.Style.STROKE;p.strokeCap=Paint.Cap.SQUARE
            c.drawLine(dp(21f),dp(32f),dp(31f),dp(22f),p);c.drawLine(dp(21f),dp(32f),dp(31f),dp(42f),p);c.drawLine(dp(21f),dp(32f),dp(41f),dp(32f),p);p.style=Paint.Style.FILL
            text("Host Checker",25f,Color.WHITE,54f,42f)
            // vertical menu
            p.color=Color.WHITE;c.drawCircle(dp(338f),dp(20f),dp(2.7f),p);c.drawCircle(dp(338f),dp(32f),dp(2.7f),p);c.drawCircle(dp(338f),dp(44f),dp(2.7f),p)
            // input outline + floating label
            val left=dp(6f);val top=dp(72f);val right=dp(223f);val bottom=dp(116f)
            p.style=Paint.Style.STROKE;p.strokeWidth=dp(2.2f);p.color=teal;c.drawRoundRect(left,top,right,bottom,dp(4f),dp(4f),p);p.style=Paint.Style.FILL
            p.color=bg;c.drawRect(dp(28f),dp(68f),dp(178f),dp(78f),p)
            text("URL (eg: www.facebook.com)",14f,Color.rgb(20,111,133),25f,76f)
            // GET selector
            text(method,18f,primary,236f,91f); drawDown(c,341f,88f)
            // proxy checkbox
            p.style=Paint.Style.STROKE;p.strokeWidth=dp(2f);p.color=Color.rgb(215,208,216);c.drawRoundRect(dp(12f),dp(132f),dp(28f),dp(152f),dp(1f),dp(1f),p);p.style=Paint.Style.FILL
            if(proxy){p.color=teal;c.drawRect(dp(12f),dp(132f),dp(28f),dp(152f),p);p.color=Color.WHITE;p.strokeWidth=dp(2f);p.style=Paint.Style.STROKE;c.drawLine(dp(16f),dp(142f),dp(21f),dp(147f),p);c.drawLine(dp(21f),dp(147f),dp(26f),dp(137f),p);p.style=Paint.Style.FILL}
            text("Proxy",18f,primary,34f,149f)
            text(header,18f,primary,210f,149f);drawDown(c,341f,145f)
            // check button
            p.color=teal;c.drawRoundRect(dp(6f),dp(172f),width-dp(6f),dp(210f),dp(25f),dp(25f),p);p.textAlign=Paint.Align.CENTER;p.textSize=dp(18f);p.color=Color.WHITE;c.drawText("Check",width/2f,dp(196f),p);p.textAlign=Paint.Align.LEFT
            if(checked) drawResponse(c)
        }
        private fun drawDown(c:Canvas,x:Float,y:Float){p.color=Color.rgb(216,210,217);val path=Path();path.moveTo(dp(x-6),dp(y-3));path.lineTo(dp(x+6),dp(y-3));path.lineTo(dp(x),dp(y+4));path.close();c.drawPath(path,p)}
        private fun drawResponse(c:Canvas){ var y=dp(225f); p.strokeWidth=dp(1f); for((i,s) in response.withIndex()){ if(i==0){p.typeface=Typeface.DEFAULT; text(s,16f,secondary,6f,y/dp(1f));} else { text(s,16f, if(s=="Stopped")primary else secondary,6f,y/dp(1f)); }
                p.color=divider;c.drawRect(dp(6f),y+dp(8f),width-dp(6f),y+dp(9f),p); y += dp(20f)
            }
            p.typeface=Typeface.DEFAULT_BOLD
        }
        override fun onTouchEvent(e:android.view.MotionEvent):Boolean { if(e.action!=MotionEvent.ACTION_UP)return true;val x=e.x/density;val y=e.y/density
            when { y in 70f..118f && x<250f -> {showKeyboard();return true}
                y in 70f..120f && x>=250f -> {popup(listOf("GET","POST","HEAD"),dp(245f),dp(72f),100){method=it;invalidate()};return true}
                y in 130f..170f && x<200f -> {proxy=!proxy;invalidate();return true}
                y in 130f..175f && x>=210f -> {popup(listOf("Header","User-Agent","Cookie"),dp(210f),dp(116f),140){header=it;invalidate()};return true}
                y in 168f..216f -> {checked=true;invalidate();return true}
            };return true }
    }
    private val density get()=resources.displayMetrics.density
    private class SimpleTextWatcher(val f:()->Unit):android.text.TextWatcher{override fun beforeTextChanged(s:CharSequence?,st:Int,c:Int,a:Int){};override fun onTextChanged(s:CharSequence?,st:Int,b:Int,c:Int){f()};override fun afterTextChanged(s:android.text.Editable?){} }
}
