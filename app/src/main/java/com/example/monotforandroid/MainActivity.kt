package com.example.monotforandroid

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.preference.PreferenceManager
import android.view.inputmethod.InputMethodManager as InputMethodManager1


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
        val kageZurashi = findViewById<View>(R.id.kageZurashi)
        val settingButton = findViewById<Button>(R.id.settingsButton)
        val backButton = findViewById<ImageButton>(R.id.backButton)
        val forwardButton = findViewById<ImageButton>(R.id.forwardButton)
        val reloadButton = findViewById<ImageButton>(R.id.reloadButton)
        val homeButton = findViewById<ImageButton>(R.id.homeButton)
        val repoButton = findViewById<Button>(R.id.repoButton)

        val searchUrl_top = searchEngine()

        // val native_like = sharedPreferences.getBoolean("native_like", false)

        menu.visibility = View.INVISIBLE
        kageZurashi.visibility = View.INVISIBLE

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
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                editText.setText(url)
                return false
            }
        }
        webView.settings.javaScriptEnabled = true
        webView.settings.userAgentString = webView.settings.userAgentString + "Mobile Monot/1.0.0Beta1"
        webView.loadUrl(searchUrl_top)

        // 検索ボタン関連
        searchButton.setOnClickListener {
            url()
        }
        menuButton.setOnClickListener {
            /*popupMenu.showAsDropDown(findViewById(R.id.menuButton))
            Log.d("TAG", "Hi")*/
            menu.visibility = View.VISIBLE
            kageZurashi.visibility = View.VISIBLE
            settingButton.setOnClickListener {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
            noMenu.setOnClickListener {
                menu.visibility = View.INVISIBLE
                kageZurashi.visibility = View.INVISIBLE
                noMenu.isClickable = false
            }
        }

        backButton.setOnClickListener {
            webView.goBack()
        }
        forwardButton.setOnClickListener {
            webView.goForward()
        }
        reloadButton.setOnClickListener {
            webView.reload()
        }
        homeButton.setOnClickListener {
            webView.loadUrl(searchUrl_top)
        }
        repoButton.setOnClickListener {
            webView.loadUrl("https://github.com/mncrp/Monot-for-Android")
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

    fun searchEngine(): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val search_engine = sharedPreferences.getString("search_engine", "")
        var searchUrl_top = "null"
        when (search_engine) {
            "Google" -> {
                searchUrl_top = "https://www.google.com/search?q="
            }
            "Yahoo!" -> {
                searchUrl_top = "https://search.yahoo.com/search?p="
            }
            "Yahoo! Japan" -> {
                searchUrl_top = "https://search.yahoo.co.jp/search?p="
            }
            "DuckDuckGo" -> {
                searchUrl_top = "https://duckduckgo.com/?q="
            }
            "Ecosia" -> {
                searchUrl_top = "https://ecosia.org/search?q="
            }
            "Bing" -> {
                searchUrl_top = "https://www.bing.com/search?q="
            }
            "Frea Search" -> {
                searchUrl_top = "https://freasearch.org/search?q="
            }
            else -> {
                searchUrl_top = "https://www.google.com/search?q="
            }
        }
        return(searchUrl_top)
    }

    fun url() {
        val searchUrl_top = searchEngine()
        val webView = findViewById<WebView>(R.id.webview)
        val editText = findViewById<EditText>(R.id.editTextURL)
        val urlText: String = editText.text.toString()
        val isItHttp = urlText.startsWith("http")
        val isItUrl = urlText.indexOf(".")

        when {
            isItHttp -> {
                webView.loadUrl(urlText)
                editText.setText(urlText)
            }
            isItUrl != -1 -> {
                val searchUrl = "$searchUrl_top$urlText"
                webView.loadUrl(searchUrl)
                editText.setText(searchUrl)
            }
            else -> {
                val searchUrl = "$searchUrl_top$urlText"
                webView.loadUrl(searchUrl)
                editText.setText(searchUrl)
            }
        }
    }
}
