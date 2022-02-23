package com.example.monotforandroid

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editText = findViewById<EditText>(R.id.editTextURL)
        val webView = findViewById<WebView>(R.id.webview)
        val searchButton = findViewById<ImageButton>(R.id.searchButton)

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