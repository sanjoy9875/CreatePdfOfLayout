package com.example.createpdf

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var btnCreate : Button
    lateinit var layout : ConstraintLayout
    lateinit var bitmap : Bitmap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnCreate = findViewById(R.id.btnCreatePdf)
        layout = findViewById(R.id.clLayout)

        btnCreate.setOnClickListener {
            bitmap = loadBitmap(layout,layout.width,layout.height)
            createPdf()
        }

    }

    private fun createPdf() {

        val windowManager = getSystemService(Context.WINDOW_SERVICE)
        val displayMetrics = DisplayMetrics()

        this.windowManager.defaultDisplay.getRealMetrics(displayMetrics)

        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        val convertWidth : Int = width
        val convertHeight : Int = height

        val pdfDocument = PdfDocument()

        val pageInfo = PdfDocument.PageInfo.Builder(convertWidth,convertHeight,1).create()

        val page = pdfDocument.startPage(pageInfo)

        val canvas = page.canvas
        val paint = Paint()
        canvas.drawPaint(paint)

        bitmap = Bitmap.createScaledBitmap(bitmap,convertWidth,convertHeight,true)

        canvas.drawBitmap(bitmap,0f,0f,null)

        pdfDocument.finishPage(page)

        val targetPdf = "/sdcard/page.pdf"

        val file = File(targetPdf)
        sharePdf(targetPdf)
        try {
            pdfDocument.writeTo(FileOutputStream(file))
        }
        catch (e : IOException){
            e.printStackTrace()
            Toast.makeText(this,"something wrong try again",Toast.LENGTH_SHORT).show()
            pdfDocument.close()
            Toast.makeText(this,"successfully download",Toast.LENGTH_SHORT).show()

        }

    }

    private fun loadBitmap(v : View, width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        v.draw(canvas)

        return bitmap
    }

    private fun sharePdf(fileName: String) {
        val emailIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
        emailIntent.type = "text/plain"
        emailIntent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        val uris = ArrayList<Uri>()
        val fileIn = File(fileName)
        val u: Uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, fileIn)
        uris.add(u)
        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
        try {
            startActivity(Intent.createChooser(emailIntent, getString(R.string.send_to)))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, getString(R.string.error_file), Toast.LENGTH_SHORT).show()
        }
    }
}