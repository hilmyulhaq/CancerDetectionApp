package com.dicoding.asclepius.view.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.view.result.ResultActivity
import com.yalantis.ucrop.UCrop
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File
import java.text.NumberFormat


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.analyzeButton.setOnClickListener {
            currentImageUri?.let { analyzeImage(it)
            } ?: run { showToast(getString(R.string.image_empty))
            }
        }
    }

    private lateinit var imageClassifierHelper: ImageClassifierHelper

    private fun startGallery() {
        launcherGallery.launch("image/*")
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let {
            currentImageUri = uri
            imagecrop(uri)
        } ?: run { Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.previewImageView.setImageDrawable(null)
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun analyzeImage(uri: Uri) {
        imageClassifierHelper = ImageClassifierHelper(
            context = this,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                    runOnUiThread {
                        results?.let {
                            if (it.isNotEmpty() && it[0].categories.isNotEmpty()) {
                                val sortedCategories = it[0].categories.sortedByDescending { it?.score }
                                val displayResult = sortedCategories.joinToString("\n") {
                                    "${it.label} " + NumberFormat.getPercentInstance().format(it.score).trim()
                                }
                                val intent = Intent(this@MainActivity, ResultActivity::class.java)
                                intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, uri.toString())
                                val labels = sortedCategories.mapNotNull { it.label }
                                val scores = sortedCategories.mapNotNull { it.score }

                                intent.putExtra(ResultActivity.EXTRA_LABELS, ArrayList(labels))
                                intent.putExtra(ResultActivity.EXTRA_SCORES, scores.toFloatArray())
                                startActivity(intent)
                            } else {
                                showToast("No Result")
                            }
                        }
                    }
                }
            }
        )
        imageClassifierHelper.classifyStaticImage(uri)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    private fun imagecrop(uri: Uri) {
        val destinationUri = Uri.fromFile(File(cacheDir, "cropped"))
        UCrop.of(uri, destinationUri)
            .withAspectRatio(13F, 10F)
            .withMaxResultSize(150, 150)
            .start(this)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            currentImageUri = resultUri
            showImage()
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            Log.e("Crop Error", cropError.toString())
        }
    }
}
