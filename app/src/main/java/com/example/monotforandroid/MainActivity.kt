package com.example.monotforandroid

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader


class MainActivity : AppCompatActivity(), View.OnClickListener {
    @SuppressLint("SetJavaScriptEnabled", "RtlHardcoded", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val addressBar = findViewById<EditText>(R.id.editTextURL)
        val webView = findViewById<WebView>(R.id.webview)
        val menu = findViewById<LinearLayout>(R.id.menuLayout)
        val noMenu = findViewById<View>(R.id.noMenu)
        val kageZurashi = findViewById<View>(R.id.kageZurashi)
        findViewById<ImageButton>(R.id.backButton).setOnClickListener(this)
        findViewById<ImageButton>(R.id.forwardButton).setOnClickListener(this)
        findViewById<ImageButton>(R.id.reloadButton).setOnClickListener(this)
        findViewById<ImageButton>(R.id.homeButton).setOnClickListener(this)

        val keyboard = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        menu.visibility = View.INVISIBLE
        kageZurashi.visibility = View.INVISIBLE

        //アドレスバーの初期化
        addressBar.text.clear()

        // WebView関連
        webView.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                addressBar.setText(url)
                return false
            }
        }
        webView.settings.javaScriptEnabled = true
        webView.settings.userAgentString = webView.settings.userAgentString + "Mobile Monot/1.0.0Beta1"
        webView.loadUrl(searchEngine(false))

        // 検索ボタン関連
        findViewById<ImageButton>(R.id.searchButton).setOnClickListener {
            url()
        }
        findViewById<ImageButton>(R.id.menuButton).setOnClickListener {
            /*popupMenu.showAsDropDown(findViewById(R.id.menuButton))
            Log.d("TAG", "Hi")*/
            menu.visibility = View.VISIBLE
            kageZurashi.visibility = View.VISIBLE
            findViewById<Button>(R.id.settingsButton).setOnClickListener {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                finish()
            }
            noMenu.setOnClickListener {
                menu.visibility = View.INVISIBLE
                kageZurashi.visibility = View.INVISIBLE
                noMenu.isClickable = false
            }
        }
        findViewById<Button>(R.id.repoButton).setOnClickListener {
            webView.loadUrl("https://github.com/mncrp/Monot-for-Android")
        }
        // editTextにフォーカスしている状態でEnterを押された際の動作
        addressBar.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    url()
                    keyboard.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                    return true
                }
                return false
            }
        })
        findViewById<Button>(R.id.versionButton).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Version")
                .setMessage("Monot for Android by monochrome.\n" +
                        "Version: 0.0.4\n" +
                        "Build number: 4\n" +
                        "repo: https://github.com/mncrp/Monot-for-Android\n" +
                        "Copyright © monochrome Project.\n")
                .setPositiveButton("OK", null)
                .setNegativeButton("See Repo") { _, _ ->
                    webView.loadUrl("https://github.com/mncrp/Monot-for-Android")
                }
                .show()
        }
    }

    override fun onClick(v: View) {
        val webView = findViewById<WebView>(R.id.webview)
        when (v.id) {
            R.id.backButton -> {
                webView.goBack()
            }
            R.id.forwardButton -> {
                webView.goForward()
            }
            R.id.reloadButton -> {
                webView.reload()
            }
            R.id.homeButton -> {
                webView.loadUrl(searchEngine(false))
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

    // 検索エンジンをassetsのengines.mncfgでJSONとして管理しています
    private fun searchEngine(mode: Boolean): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val searchEngine = sharedPreferences.getString("search_engine", "ddg")
        val assetManager= resources.assets
        val inputSystem = assetManager.open("engines.mncfg")
        val bufferedReader = BufferedReader(InputStreamReader(inputSystem))
        val engineMncfg: String = bufferedReader.readText()
        val engineMncfgOb = JSONObject(engineMncfg)
        return when (mode) {
            true -> {
                // クエリ取得はtrue
                val engineValues = engineMncfgOb.getJSONObject("queryUrl")
                val searchUrlTop = engineValues.getString("$searchEngine")
                (searchUrlTop)
            }
            false -> {
                // ホーム取得はfalse
                val engineValues = engineMncfgOb.getJSONObject("url")
                val homeUrl = engineValues.getString("$searchEngine")
                (homeUrl)
            }
        }
    }

    fun url() {
        val searchUrlTop = searchEngine(true)
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
                val searchUrl = "$searchUrlTop$urlText"
                webView.loadUrl(searchUrl)
                editText.setText(searchUrl)
            }
            else -> {
                val searchUrl = "$searchUrlTop$urlText"
                webView.loadUrl(searchUrl)
                editText.setText(searchUrl)
            }
        }
    }
}
