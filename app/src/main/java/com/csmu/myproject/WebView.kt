package com.csmu.myproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebViewClient
import com.csmu.myproject.databinding.ActivityMainPageBinding
import com.csmu.myproject.databinding.ActivityWebViewBinding

class WebView : AppCompatActivity() {

    lateinit var binding: ActivityWebViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)


        var getUrl = intent.getBundleExtra("bundle")?.getString("key")

        binding.webView.webViewClient= WebViewClient()
        binding.webView.loadUrl(getUrl.toString())
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.setSupportZoom(true)



    }
}