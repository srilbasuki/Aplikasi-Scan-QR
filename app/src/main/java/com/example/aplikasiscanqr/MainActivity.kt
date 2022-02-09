package com.example.aplikasiscanqr

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    /**Deklarasi Variabel*/
    companion object{
        private const val CAMERA_REQ = 101
    }
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**Call Function*/
        getPermission()
        QRScanner()
        reScan()

        /**Read Menus Status*/
        cardRescan.setOnClickListener {
            reScan()
        }
        cardCopy.setOnClickListener {
            copyQR()
        }
        cardShare.setOnClickListener {
            shareQR()
        }
    }

    /**Main Function*/
    private fun getPermission(){
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
        if (permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQ)
        }
    }
    private fun QRScanner(){
        codeScanner = CodeScanner(this, scanView)
        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS
            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.SINGLE
            isAutoFocusEnabled = true
            isFlashEnabled = false
            decodeCallback = DecodeCallback {
                runOnUiThread{
                    textQR.text = it.text
                }
            }
            errorCallback = ErrorCallback {
                runOnUiThread {
                }
            }
            scanView.setOnClickListener{
                reScan()
            }
        }
    }

    /**Menu Handler Function*/
    private fun reScan(){
        codeScanner.startPreview()
        textQR.text = "scanning..."
    }
    private fun copyQR(){
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("simple text", textQR.text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "- ${textQR.text} - is copied to clipboard", Toast.LENGTH_SHORT).show()
    }
    private fun shareQR(){
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, textQR.text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    /**Relase Resource*/
    override fun onPause(){
        codeScanner.releaseResources()
        super.onPause()
    }
}
