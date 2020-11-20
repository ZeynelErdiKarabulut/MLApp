package com.example.android.firstmlapp

import android.support.annotation.RequiresApi
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.support.design.widget.FloatingActionButton
import android.graphics.Bitmap
import android.widget.TextView
import android.support.v7.widget.RecyclerView
import com.google.firebase.ml.vision.label.FirebaseVisionLabel
import android.os.Bundle
import com.google.firebase.FirebaseApp
import android.widget.Toast
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.DividerItemDecoration
import android.content.Intent
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.FirebaseVision
import java.util.ArrayList


class MainActivity : AppCompatActivity() {
    var cameraButton: FloatingActionButton? = null
    var imageBitmap: Bitmap? = null
    var imageView: ImageView? = null
    //var result = 0
    var detectButton: Button? = null
    var textTextView: TextView? = null
    var confidenceTextView: TextView? = null
    var recyclerView: RecyclerView? = null
    var mainAdapter: MainAdapter? = null
    var firebaseVisionLabels = ArrayList<FirebaseVisionLabel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
        cameraButton = findViewById(R.id.camera_fab)
        imageView = findViewById(R.id.captured_image)
        detectButton = findViewById(R.id.detect_button)
        textTextView = findViewById(R.id.text)
        confidenceTextView = findViewById(R.id.confidence)
        cameraButton!!.setOnClickListener(View.OnClickListener { dispatchTakePictureIntent() })
        detectButton!!.setOnClickListener(View.OnClickListener {
            Log.e(TAG, "Value of firebaseImageLabels " + firebaseVisionLabels.size)
            runImageRecognition()
            if (firebaseVisionLabels.isEmpty()) {
                Toast.makeText(applicationContext, "Can't Identify Image, Try different one", Toast.LENGTH_SHORT).show()
            }
        })
        recyclerView = findViewById(R.id.rec_view)
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView!!.setLayoutManager(linearLayoutManager)
        recyclerView!!.setHasFixedSize(true)
        val dividerItemDecoration = DividerItemDecoration(recyclerView!!.getContext(), linearLayoutManager.orientation)
        recyclerView!!.addItemDecoration(dividerItemDecoration)
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val extras = data.extras
            imageBitmap = extras["data"] as Bitmap
            imageView!!.setImageBitmap(imageBitmap)
        }
    }

    private fun runImageRecognition() {

        // FirebaseVisionImage image = FirebaseVisionImage.fromMediaImage(imageBitmap, result);
        val image = FirebaseVisionImage.fromBitmap(imageBitmap!!)

        /**  FirebaseVisionLabelDetectorOptions options =
         * new FirebaseVisionLabelDetectorOptions.Builder()
         * .setConfidenceThreshold(0.8f)
         * .build();  */
        val detector = FirebaseVision.getInstance()
                .visionLabelDetector
        val resultImage = detector.detectInImage(image)
                .addOnSuccessListener { labels ->
                    // Task completed successfully
                    // ...
                    detectButton!!.isEnabled = false
                    for (label in labels) {
                        val text = label.label
                        val entityId = label.entityId
                        val confidence = label.confidence
                        firebaseVisionLabels.add(label)

                        //   textTextView.setText(text);
                        // confidenceTextView.setText(String.valueOf(confidence));
                        mainAdapter = MainAdapter(applicationContext, firebaseVisionLabels)
                        recyclerView!!.adapter = mainAdapter
                    }
                }
                .addOnFailureListener { // Task failed with an exception
                    // ...
                    detectButton!!.isEnabled = true
                    Toast.makeText(applicationContext, "Something Went wrong! Try Again", Toast.LENGTH_SHORT).show()
                }
    }

    override fun onResume() {
        super.onResume()
        detectButton!!.isEnabled = true
        detectButton!!.setOnClickListener { runImageRecognition() }
        firebaseVisionLabels.clear()
    }

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
        private val TAG = MainActivity::class.java.simpleName
    }
}