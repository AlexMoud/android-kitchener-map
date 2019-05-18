package gr.hua.it21533.kitchenerMap.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import gr.hua.it21533.kitchenerMap.R
import gr.hua.it21533.kitchenerMap.activities.MapsActivity
import kotlinx.android.synthetic.main.fragment_feedback.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class FeedbackFragment: Fragment() {

    private val TAG = "FEEDBACK_FRAGMENT"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_feedback, container,false)
    }

    override fun onStart() {
        super.onStart()
        backToMenu.setOnClickListener {
            (activity as MapsActivity).replaceMenuFragments("nav_main_menu")
        }

        feedback_upload_btn.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }

    lateinit var currentPhotoPath: String
    val REQUEST_TAKE_PHOTO = 1

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG,"$requestCode")
        if(requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            val takenImage = BitmapFactory.decodeFile(currentPhotoPath)
            takenImage_view.setImageBitmap(takenImage)
            takenImagePath_text.text = currentPhotoPath
            Log.d(TAG,"Inside if")
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
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
            takePictureIntent.resolveActivity((activity as MapsActivity).packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        (activity as MapsActivity),
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