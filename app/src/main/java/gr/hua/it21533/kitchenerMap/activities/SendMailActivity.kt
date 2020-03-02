package gr.hua.it21533.kitchenerMap.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_send_mail.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class SendMailActivity : AppCompatActivity() {

    private val TAG = "SEND_MAIL_ACTIVITY"
    private val REQUEST_STORAGE_PERMISSIONS = 1
    private val REQUEST_TAKE_PHOTO = 1
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var userComment: String? = null
    lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(gr.hua.it21533.kitchenerMap.R.layout.activity_send_mail)
        latitude = intent.getDoubleExtra("latitude", 0.0)
        longitude = intent.getDoubleExtra("longitude", 0.0)
        feedback_takePhoto_btn.setOnClickListener {
            checkForCameraPermission()
        }
    }

    private fun checkForCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_STORAGE_PERMISSIONS)
        } else {
            dispatchTakePictureIntent()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_STORAGE_PERMISSIONS -> {
                if (grantResults.isNotEmpty()) {
                    var permissionsGranted = true
                    grantResults.forEach { i ->
                        if (i != PackageManager.PERMISSION_GRANTED) {
                            permissionsGranted = false
                        }
                    }
                    if (permissionsGranted) {
                        dispatchTakePictureIntent()
                    }
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            val takenImage = BitmapFactory.decodeFile(currentPhotoPath)
            takenImage_view.setImageBitmap(takenImage)
            initSendMailButton()
        }
    }

    private fun initSendMailButton() {
        feedback_sendMail_btn.visibility = View.VISIBLE
        feedback_sendMail_btn.setOnClickListener {
            userComment = user_email_comment.text.toString()
            if (userComment.equals("")) {
                Toast.makeText(this, "You must provide us with a comment...", Toast.LENGTH_SHORT).show()
            } else {
                val emailIntent = Intent(Intent.ACTION_SEND)
                emailIntent.type = "image/*"
                emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("kitchenermaps@hua.gr"))
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Coordinates $latitude, $longitude")
                emailIntent.putExtra(Intent.EXTRA_TEXT, userComment)
                emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                val fileName = File(currentPhotoPath.substringAfterLast("/"))
                emailIntent.putExtra(
                    Intent.EXTRA_STREAM,
                    Uri.parse("content://gr.hua.it21533.kitchenerMap.fileprovider/my_images/$fileName")
                )
                startActivity(Intent.createChooser(emailIntent, "Send mail..."))
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file language
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(this.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "gr.hua.it21533.kitchenerMap.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }
}
