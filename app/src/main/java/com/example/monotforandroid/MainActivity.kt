package com.example.monotforandroid

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.core.view.isInvisible

class MainActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled", "RtlHardcoded", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editText = findViewById<EditText>(R.id.editTextURL)
        val webView = findViewById<WebView>(R.id.webview)
        val searchButton = findViewById<ImageButton>(R.id.searchButton)
        val menuButton = findViewById<ImageButton>(R.id.menuButton)
        val menu = findViewById<LinearLayout>(R.id.menuLayout)
        val noMenu = findViewById<View>(R.id.noMenu)

        menu.visibility = View.INVISIBLE

        // メニュー関連
        /*val popupMenu = PopupWindow()
        val menuLayout = findViewById<LinearLayout>(R.id.menuLayout)
        /*val menuView = FrameLayout(this).also {
            it.addView(menuLayout)
        }*/
        popupMenu.contentView = menuLayout */

        //アドレスバーの初期化
        editText.text.clear()

        // WebView関連
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                editText.setText(url)
                return false
            }
        }
        webView.settings.javaScriptEnabled = true
        webView.settings.userAgentString = webView.settings.userAgentString + "Mobile Monot/1.0.0Beta1"
        webView.loadUrl("https://google.co.jp")

        // 検索ボタン関連
        searchButton.setOnClickListener {
            val urlText: String = editText.text.toString()
            val isItHttp = urlText.startsWith("http")
            val isItUrl = urlText.indexOf(".")

            when {
                isItHttp -> {
                    webView.loadUrl(urlText)
                    editText.setText(urlText)
                }
                isItUrl != -1 -> {
                    val searchUrl = "https://$urlText"
                    webView.loadUrl(searchUrl)
                    editText.setText(searchUrl)
                }
                else -> {
                    val searchUrl = "https://www.google.com/search?q=$urlText"
                    webView.loadUrl(searchUrl)
                    editText.setText(searchUrl)
                }
            }
        }
        menuButton.setOnClickListener {
            /*popupMenu.showAsDropDown(findViewById(R.id.menuButton))
            Log.d("TAG", "Hi")*/
            menu.visibility = View.VISIBLE
            noMenu.setOnClickListener {
                menu.visibility = View.INVISIBLE
                noMenu.isClickable = false
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val webView = findViewById<WebView>(R.id.webview)
        val editText = findViewById<EditText>(R.id.editTextURL)
        // 端末の戻るボタンでWebViewも戻るようにする
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
            editText.setText(webView.url)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
