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
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader


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

        val searchUrlTop = searchEngine()

        val keyboard = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        menu.visibility = View.INVISIBLE
        kageZurashi.visibility = View.INVISIBLE

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
        webView.loadUrl(searchUrlTop)

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
                finish()
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
            webView.loadUrl(searchUrlTop)
        }
        repoButton.setOnClickListener {
            webView.loadUrl("https://github.com/mncrp/Monot-for-Android")
        }
        // editTextにフォーカスしている状態でEnterを押された際の動作
        editText.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    url()
                    keyboard.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                    return true
                }
                return false
            }
        })
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

    private fun searchEngine(): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val searchEngine = sharedPreferences.getString("search_engine", "ddg")
        val assetManager= resources.assets
        val inputSystem = assetManager.open("engines.mncfg")
        val bufferedReader = BufferedReader(InputStreamReader(inputSystem))
        val engineMncfg: String = bufferedReader.readText()
        val engineMncfgOb = JSONObject(engineMncfg)
        val engineValues = engineMncfgOb.getJSONObject("values")
        val searchUrlTop = engineValues.getString("$searchEngine")
        return(searchUrlTop)
    }

    fun url() {
        val searchUrlTop = searchEngine()
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
