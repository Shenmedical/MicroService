package com.csmu.myproject

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.net.Uri.*
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.view.View
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.csmu.myproject.databinding.ActivityMainBinding
import com.github.florent37.runtimepermission.kotlin.askPermission
import java.io.File
import java.io.IOException


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding: ActivityMainBinding
    private val videoView get() = binding.layoutVideoView
    private val SPEECH_REQUEST_CODE = 0
    private var speech = ""
    private var count = 1
    private var originVideo:String = ""

    val REQUEST_IMAGE_CAPTURE = 1
    val IMAGE_PICK_CODE = 2
    private val VIDEO_CAPTURE = 101


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.clMain.visibility = View.VISIBLE


        binding.btnSend.setOnClickListener(this)
        binding.btnWebview.setOnClickListener(this)
        binding.btnPhoto.setOnClickListener(this)
//        binding.btnAlbum.setOnClickListener(this)
        binding.mainPauseButton.setOnClickListener(pauseButtonClickHandler)
        binding.mainPlayButton.setOnClickListener(playButtonClickHandler)
        binding.mainStopButton.setOnClickListener(stopButtonClickHandler)
        binding.layoutVideoView.setOnCompletionListener(videoPlayListenser)

        permission() //檢查權限

    }

    //權限
    private fun permission() {
        val context = this

        askPermission(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,



            ) { _ ->

        }.onDeclined { e ->
            if (e.hasDenied()) {
                Toast.makeText(context, "hasDenied", Toast.LENGTH_LONG).show()
                e.askAgain()
                return@onDeclined
            }
            if (e.hasForeverDenied()) {
                Toast.makeText(context, "hasForeverDenied", Toast.LENGTH_LONG).show()
                e.goToSettings()
            }
        }

    }


    //語音輸入

    private fun displaySpeechRecognizer() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
        }
        startActivityForResult(intent, SPEECH_REQUEST_CODE)

    }

    //語音回傳資料
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val spokenText: String? =
                data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).let { results ->
                    results?.get(0)
                }
            binding.tvSpeech.text = spokenText
            speech = spokenText!!

            if(spokenText=="暫停"){
                videoView.pause()
            }else if(spokenText=="上一步"){
                count--
                changeVideo(originVideo,count)
            }else if(spokenText=="下一步"){
                count++
                changeVideo(originVideo,count)
            }else if(spokenText=="播放"){
                videoView.start()
            }else if(spokenText=="重播"){
                videoView.seekTo(0)
                videoView.start()
            }
            else{
                originVideo = spokenText
                count = 1
                playVideo(originVideo,count) //播放影片

            }


        }
    }

    //播放影片
    private fun playVideo(speech:String,count: Int) {
        //預設路徑加資料夾名稱
        val uri: Uri = parse(
//            Environment.getExternalStorageDirectory().getPath() + "/Movies/" + speech + ".mp4"
            "http://140.128.137.11/MicroService/video/" + speech + "${count}.mp4"
        )
        videoView.setVideoURI(parse(uri.toString()))
        videoView.start()
        binding.guideName.text = "目前播放的導引步驟：\n${originVideo}的第${count}步驟"

        // hide medie controller
        videoView.setMediaController(null)
    }

    private val playButtonClickHandler = View.OnClickListener { _ ->
        videoView.start()
    }

    private val pauseButtonClickHandler = View.OnClickListener { _ ->
        videoView.pause()
    }

    private val stopButtonClickHandler = View.OnClickListener { _ ->
        videoView.seekTo(0)
        videoView.pause()
    }


    fun capturePhoto2() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(intent, VIDEO_CAPTURE)
    }

    //用來調整上一步驟和下一步驟的fn
    private fun changeVideo(name:String,count:Int){


        // hide medie controller
        val pathUri = "http://140.128.137.11/MicroService/video/"
        var filter :MutableList<String> = ArrayList()

        File(pathUri).walk().forEach {
            if(it.toString().contains(name) == true){
                filter.add(it.toString())
                println(filter)
            }
        }
        if(count<=filter.size){
            val uri: Uri = parse(
                "http://140.128.137.11/MicroService/video/" + name + "${count.toString()}.mp4"
            )
            videoView.setVideoURI(parse(uri.toString()))
            videoView.start()
            binding.guideName.text = "目前播放的導引步驟：\n${name}的第${count}步驟"
            // hide medie controller
            videoView.setMediaController(null)
        }else{
            videoView.setMediaController(null)
            println("結束")

        }
        videoView.setMediaController(null)

    }


    fun startRecording() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(intent, VIDEO_CAPTURE)
    }//監聽videoPlay的播放狀態
    /**
     * 如果播放完畢會自動播放下一首
     */
    private val videoPlayListenser = MediaPlayer.OnCompletionListener {
        count++
        changeVideo(originVideo,count)
    }


    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btnSend -> {
                displaySpeechRecognizer()
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

            R.id.btnPhoto -> { //                capturePhoto()
//                startRecording()
                var bundle = Bundle()
                val intent = Intent(this, VideoAdd::class.java)
                bundle.putString(
                    "key",
                    "http://140.128.137.11/MicroService/add.php"
                )
                intent.putExtra("bundle", bundle)
                startActivity(intent)
            }

//            R.id.btnAlbum -> {
//
////                pickImageFromGallery()
//
////                selectImage2()
//
//                val intent = Intent(this, Video::class.java)
//                startActivity(intent)
//            }


        }
    }

    //..................
    val REQUEST_IMAGE_OPEN = 1

    fun selectImage2() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "video/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        // Only the system receives the ACTION_OPEN_DOCUMENT, so no need to test.
        startActivityForResult(intent, REQUEST_IMAGE_OPEN)
    }


}