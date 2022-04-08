package com.csmu.myproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.csmu.myproject.databinding.ActivityMainPageBinding


class MainPage : AppCompatActivity(), View.OnClickListener {


    lateinit var binding: ActivityMainPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnPhoto.setOnClickListener(this)
        binding.btnSpeech.setOnClickListener(this)
        binding.btnWebview.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btnSpeech -> {
                startActivity(Intent(this@MainPage, MainActivity::class.java))
            }
            R.id.btnPhoto -> {

            }
            R.id.btnWebview -> {
                var bundle = Bundle()
                val intent = Intent(this, WebView::class.java)
                bundle.putString(
                    "key",
                    "http://140.128.137.11/MicroService/chart.php"
                )
                intent.putExtra("bundle", bundle)
                startActivity(intent)
            }

        }
    }
}