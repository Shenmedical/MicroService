package com.csmu.myproject

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.csmu.myproject.databinding.ActivityVideoBinding
import com.github.florent37.runtimepermission.kotlin.askPermission
import java.io.File
import java.io.FileWriter


class Video : AppCompatActivity() {

    lateinit var binding: ActivityVideoBinding

    val REQUEST_IMAGE_GET = 1
    val VIDEO_CAPTURE = 101
    private val videoView get() = binding.videoView
    var videoUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //listen to gallery button click
        binding.gallery .setOnClickListener {
            pickPhotoFromGallery()
        }

        binding.photo.setOnClickListener {
            startRecording()
        }
        permission() //檢查權限
    }




    //...........................................

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

    private fun pickPhotoFromGallery() {
        val pickImageIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        )

        startActivityForResult(pickImageIntent, REQUEST_IMAGE_GET)
    }

    fun startRecording() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(intent, VIDEO_CAPTURE)
    }

    //override function that is called once the photo has been taken
    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        data: Intent?
    ) {
        if (resultCode == Activity.RESULT_OK
            && requestCode == REQUEST_IMAGE_GET
        ) {
            //photo from gallery
            val fileUri = data?.data
            Glide
                .with(this)
                .load(fileUri)
                .centerCrop()

                .into(binding.imageView);

//            renameFile(fileUri.toString(),fileUri.toString()+"QQQQQ")
//            binding.tvName.text = fileUri.toString()

            binding.tvName.text = fileUri?.lastPathSegment.toString()

            videoUrl = fileUri?.lastPathSegment.toString()

            videoView.visibility = View.VISIBLE

//            val uri: Uri = Uri.parse(
//                Environment.getExternalStorageDirectory().getPath() + "/Movies/"
//            )
            playVideo()

//            if (uri!=null) {
//                val from = File(uri.toString(), "abc.mp3")
//                val to = File(uri.toString(), "def.mp3")
//                val b: Boolean = from.renameTo(to)
//                 Log.e("3455", b.toString())
//            }



        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }



    private fun renameFile(oldPath: String, newPath: String): File? {
        if (TextUtils.isEmpty(oldPath)) {
            return null
        }
        if (TextUtils.isEmpty(newPath)) {
            return null
        }
        val oldFile = File(oldPath)
        val out = FileWriter(oldFile)
        out.close()

        val newFile = File(newPath)
        val b: Boolean = oldFile.renameTo(newFile)
        return File(newPath)

    }

    //播放影片
    private fun playVideo() {
        //預設路徑加資料夾名稱
        val uri: Uri = Uri.parse(
            videoUrl
        )
        videoView.setVideoURI(Uri.parse(uri.toString()))
        videoView.start()

        // hide medie controller
        videoView.setMediaController(null)
    }


}


