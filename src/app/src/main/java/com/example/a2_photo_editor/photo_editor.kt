package com.example.a2_photo_editor

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import coil.load
import com.example.a2_photo_editor.databinding.ActivityMainBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


private val CAMERA_REQUEST_CODE = 1
private val GALLERY_REQUEST_CODE = 2
private lateinit var img: ImageView
private lateinit var editText: TextView
private lateinit var bitmap: Bitmap
private var count: Int = 0

class photo_editor : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_editor)

        val btnCamera:Button = findViewById(R.id.btnCamera)
        val btnGallery:Button = findViewById(R.id.btnGallery)
        val btnSave: Button = findViewById(R.id.saveImg)

        img = findViewById(R.id.imageView)
        editText= findViewById(R.id.editText)

        editText.visibility = View.INVISIBLE

        btnCamera.setOnClickListener {
            cameraCheckPermission()
        }

        btnGallery.setOnClickListener {
            galleryCheckPermission()
        }

        btnSave.setOnClickListener {
            saveImgToStorage()
        }

        //when you click on the image
        val editBt:Button = findViewById(R.id.editBt)
        editBt.setOnClickListener {
            val pictureDialog = AlertDialog.Builder(this)
            pictureDialog.setTitle("Photo Editor")
            val pictureDialogItem = arrayOf("Enter text to overlay the photo",
                "Draw on the image to annotate it", "Rotate photo")
            pictureDialog.setItems(pictureDialogItem) { dialog, which ->

                when (which) {
                    0 -> editText()
                    1 -> draw()
                    2 -> rotate()
                }
            }

            pictureDialog.show()
        }
    }

    private fun galleryCheckPermission() {

        Dexter.withContext(this).withPermission(
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(object : PermissionListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                gallery()
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                Toast.makeText(
                    this@photo_editor,
                    "You have denied the storage permission to select image",
                    Toast.LENGTH_SHORT
                ).show()
                showRotationalDialogForPermission()
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: PermissionRequest?, p1: PermissionToken?) {
                showRotationalDialogForPermission()
            }
        }).onSameThread().check()
    }

    private fun gallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun cameraCheckPermission() {

        Dexter.withContext(this)
            .withPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA).withListener(

                object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {

                            if (report.areAllPermissionsGranted()) {
                                camera()
                            }

                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        p1: PermissionToken?) {
                        showRotationalDialogForPermission()
                    }

                }
            ).onSameThread().check()
    }

    private fun camera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            when (requestCode) {

                CAMERA_REQUEST_CODE -> {

                    val insertBitmap = data?.extras?.get("data") as Bitmap

                    //we are using coroutine image loader (coil)
                    img.load(insertBitmap) {
                        crossfade(true)
                        crossfade(1000)
                    }
                }

                GALLERY_REQUEST_CODE -> {

                    img.load(data?.data) {
                        crossfade(true)
                        crossfade(1000)
                    }

                }
            }

        }

    }

    private fun showRotationalDialogForPermission() {
        AlertDialog.Builder(this)
            .setMessage("It looks like you have turned off permissions"
                    + "required for this feature. It can be enable under App settings!!!")

            .setPositiveButton("Go TO SETTINGS") { _, _ ->

                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)

                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }

            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }


    @SuppressLint("SetTextI18n")
    private fun editText() {

        editText.visibility = View.VISIBLE

    }

    private fun draw() {
        true
    }
    private fun rotate() {
        img.rotation += 90f
    }

    private fun saveImgToStorage() {

        bitmap = img.drawable.toBitmap()
        count++
        MediaStore.Images.Media.insertImage(contentResolver,bitmap,"Saved Image+{$count}","")
        Toast.makeText(applicationContext,"saved image",Toast.LENGTH_SHORT).show()


    }

}