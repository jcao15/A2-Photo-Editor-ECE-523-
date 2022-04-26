package com.example.a2_photo_editor

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import coil.load
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import android.graphics.Bitmap

import android.graphics.BitmapFactory
import android.view.MotionEvent
import java.io.IOException


class photo_editor : AppCompatActivity(),View.OnTouchListener {

    private val CAMERA_REQUEST_CODE = 1
    private val GALLERY_REQUEST_CODE = 2
    private lateinit var img: ImageView
    private lateinit var editText: TextView
    private lateinit var bitmap: Bitmap
    private lateinit var insertBitmap: Bitmap

    private var count: Int = 0
    private var curX = 0
    private var curY = 0
    private var isBitmapInited = false
    private lateinit var myBitmap: Bitmap
    private lateinit var bitmapCanvas: Canvas
    private lateinit var myPaint: Paint

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_editor)

        val btnCamera: Button = findViewById(R.id.btnCamera)
        val btnGallery: Button = findViewById(R.id.btnGallery)
        val btnSave: Button = findViewById(R.id.saveImg)

        myPaint = Paint()
        myPaint.setARGB(255, 150, 200, 215)

        img = findViewById(R.id.imageView)
        editText = findViewById(R.id.editText)

        editText.visibility = View.INVISIBLE

        //img.setOnTouchListener{ v, event -> return this.drawListener(v,event)}
        img.setOnTouchListener(this)

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
        val editBt: Button = findViewById(R.id.editBt)
        editBt.setOnClickListener {
            val pictureDialog = AlertDialog.Builder(this)
            pictureDialog.setTitle("Photo Editor")
            val pictureDialogItem = arrayOf(
                "Draw on the image",
                "add Text on the image to annotate it", "Rotate photo"
            )
            pictureDialog.setItems(pictureDialogItem) { dialog, which ->

                when (which) {
                    0 -> draw()
                    1 -> addText()
                    2 -> rotate()
                }
            }

            pictureDialog.show()
        }

    }

    private fun initBitmap() {
        if (img.drawable == null) {
            isBitmapInited = false
            return
        }

        if (img.width > 0 && img.height > 0) {
            //myBitmap = Bitmap.createBitmap(img.width, img.height, Bitmap.Config.ARGB_8888)
            myBitmap = img.drawable.toBitmap()
            bitmapCanvas = Canvas(myBitmap)
            isBitmapInited = true
            img.setImageBitmap(myBitmap)
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
                p0: PermissionRequest?, p1: PermissionToken?
            ) {
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
                android.Manifest.permission.CAMERA
            ).withListener(

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
                        p1: PermissionToken?
                    ) {
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

                    insertBitmap = data?.extras?.get("data") as Bitmap

                    //we are using coroutine image loader (coil)
                    img.load(insertBitmap) {
                        crossfade(true)
                        crossfade(1000)
                    }
                    initBitmap()
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
            .setMessage(
                "It looks like you have turned off permissions"
                        + "required for this feature. It can be enable under App settings!!!"
            )

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
    private fun draw() {

        editText.visibility = View.VISIBLE

    }
    override fun onTouch(v: View, event:MotionEvent):Boolean{

        if (!isBitmapInited) {
            initBitmap()
        }
        val action = event.action
        when(action){
            MotionEvent.ACTION_UP -> {
                v.performClick()
            }

            MotionEvent.ACTION_DOWN -> {
                curX= event.x.toInt()
                curY= event.y.toInt()
                bitmapCanvas.drawOval(RectF(curX.toFloat(), curY.toFloat(), curX + 50f, curY + 50f), myPaint)
                //invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                curX = event.x.toInt()
                curY= event.y.toInt()
                bitmapCanvas.drawOval(RectF(curX.toFloat(), curY.toFloat(), curX + 50f, curY + 50f), myPaint)
                //invalidate()
            }
            else ->{
            }

        }
        v.invalidate()
        return true
    }

    private fun addText() {

        bitmap = img.drawable.toBitmap()

        img.setImageBitmap(bitmap.drawText())
        bitmap.recycle()

    }
    // extension function to get bitmap from assets
    private fun Context.assetsToBitmap(fileName:String):Bitmap?{
        return try {
            val stream = assets.open(fileName)
            BitmapFactory.decodeStream(stream)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    // extension function to draw text on bitmap
    private fun Bitmap.drawText(
        text:String = "UW HUSKY",
        textSize:Float = 15f,
        color:Int = Color.MAGENTA
    ):Bitmap?{
        val bitmap = copy(config,true)
        val canvas = Canvas(bitmap)

        Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            this.color = color
            this.textSize = textSize
            typeface = Typeface.SERIF
            setShadowLayer(1f,0f,1f,Color.WHITE)
            canvas.drawText(text,20f,height - 20f,this)
        }

        return bitmap
    }

    private fun rotate() {
        bitmap = img.drawable.toBitmap()
        img.setImageBitmap(bitmap.rotate(90F))
        bitmap.recycle()

    }
    private fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    private fun saveImgToStorage() {
        count++
        MediaStore.Images.Media.insertImage(contentResolver, img.drawable.toBitmap(), "Saved Image+{$count}", "")
        Toast.makeText(applicationContext, "saved image", Toast.LENGTH_SHORT).show()

    }

}