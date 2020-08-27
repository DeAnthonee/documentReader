package com.deanthonee.documentreader.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.SparseIntArray
import android.view.Surface
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.deanthonee.documentreader.R
import com.deanthonee.documentreader.networking.MicrosoftServices
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import io.reactivex.disposables.Disposable
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class CogServicesActivity : AppCompatActivity() {

    var currentPhotoPath: String? = null
    lateinit var imageView: ImageView
    lateinit var button: Button
    lateinit var processButton: Button
    lateinit var storage: StorageReference
    lateinit var progressBar: ProgressBar
    var isReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cog_services)
        initViews()
        onClicks()
    }

    private fun tryMagic() {
        val viewmodel = CogViewModel()
        viewmodel.starthere()
    }

    private fun onClicks() {
        button.setOnClickListener {
//            dispatchToTakePhoto()
            tryMagic()
        }
        processButton.setOnClickListener { if (isReady) startTextProcessing() }
    }

    private fun initViews() {
        button = findViewById(R.id.button)
        processButton = findViewById(R.id.process_button)
        imageView = findViewById(R.id.image_view)
        progressBar = findViewById(R.id.progress_bar)
        storage = FirebaseStorage.getInstance().reference
    }

    private fun startTextProcessing() {
        progressBar.visibility = View.VISIBLE
        processButton.isEnabled = false
        button.isEnabled = false
        stepOne()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            when (requestCode) {
                FirebaseActivity.HIGH_QUALITY_REQUEST_CODE -> {

                    val file = File(currentPhotoPath)
                    Picasso.get().load(Uri.fromFile(file)).into(imageView)
                    isReady = true
                }
            }
        }
    }

    fun stepOne() {
        val file = File(currentPhotoPath)
        val uri = Uri.fromFile(file)
        val ref = currentPhotoPath?.let { storage.child(it) }
        ref?.putFile(uri)
            ?.addOnSuccessListener {
                progressBar.visibility = View.INVISIBLE
                processButton.isEnabled = true
                button.isEnabled = true
                ref.downloadUrl.addOnSuccessListener {
                    beginProcessing(it)
                }
            }?.addOnFailureListener {
                progressBar.visibility = View.INVISIBLE
                processButton.isEnabled = true
                button.isEnabled = true
                Toast.makeText(this@CogServicesActivity, "Could not process image", Toast.LENGTH_SHORT).show()
            }
    }

    private fun beginProcessing(uri: Uri?) {
        //start microsoft processing
//        startActivity(ResultsActivity.newIntent(uri.toString(), this@CogServicesActivity))
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun getRotation(cameraId: String, activity: Activity, context: Context) {
        val deviceRotation = activity.windowManager.defaultDisplay.rotation
        var rotationCompensation = ORIENTATIONS.get(deviceRotation)
        val cameraManager: CameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val sensorOrientation = cameraManager.getCameraCharacteristics(cameraId)
            .get(CameraCharacteristics.SENSOR_ORIENTATION)
        rotationCompensation = (rotationCompensation + sensorOrientation!! + 270) % 360
        val result = when (rotationCompensation) {
            0 -> {
                FirebaseVisionImageMetadata.ROTATION_0
            }
            90 -> {
                FirebaseVisionImageMetadata.ROTATION_90
            }
            180 -> {
                FirebaseVisionImageMetadata.ROTATION_180
            }
            270 -> {
                FirebaseVisionImageMetadata.ROTATION_270
            }
            else -> {
                Log.e("freefree", "Bad rotation value: " + rotationCompensation)
                FirebaseVisionImageMetadata.ROTATION_0;
            }
        }

    }

    private fun dispatchToTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                // exception caught here
            }
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(
                    this,
                    "com.deanthonee.documentreader.fileprovider",
                    photoFile
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(
                    intent,
                    FirebaseActivity.HIGH_QUALITY_REQUEST_CODE
                )
            }
        }
    }

    @Throws(IOException::class)
    fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName: String = "JPEG" + timeStamp + "_"
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file: File = File.createTempFile(
            imageFileName,
            ".jpg",
            storageDirectory
        )
        currentPhotoPath = file.absolutePath
        return file
    }

    fun getHeader():Map<String,Any>{
        val headerMap = mutableMapOf<String, Any>()
        headerMap["Content-Type"] = "application/json"
        headerMap["Ocp-Apim-Subscription-Key"] = MY_KEY
        return headerMap
    }


    companion object {
        fun newIntent(callingActivity: Context) = Intent(callingActivity, CogServicesActivity::class.java)

        const val TEST_PIC_URL = "https://firebasestorage.googleapis.com/v0/b/document-reader-3b0f1.appspot.com/o/myuploads%2FuploadTest.jpeg?alt=media&token=7ffc6e20-f1fa-4e4f-bc01-d674cc8c1a85"
        const val BASE_URL = "https://westus.api.cognitive.microsoft.com/"
        const val MY_BASE_URL = "https://deanthoneeking.cognitiveservices.azure.com/"
        const val MY_KEY = "b04207f6f3f64fa1b3ecd3c538db9faf"
        const val HIGH_QUALITY_REQUEST_CODE = 101
        private val ORIENTATIONS = SparseIntArray().apply {
            append(Surface.ROTATION_0, 90)
            append(Surface.ROTATION_90, 0)
            append(Surface.ROTATION_180, 270)
            append(Surface.ROTATION_270, 180)
        }

    }
}