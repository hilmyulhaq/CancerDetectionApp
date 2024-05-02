package com.dicoding.asclepius.view.result

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dicoding.asclepius.databinding.ActivityResultBinding
import java.text.NumberFormat

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_LABELS = "extra_labels"
        const val EXTRA_SCORES = "extra_scores"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI))
        imageUri?.let { binding.resultImage.setImageURI(it) }
        val labels = intent.getStringArrayListExtra(EXTRA_LABELS)
        val scores = intent.getFloatArrayExtra(EXTRA_SCORES)

        if (labels != null && scores != null && labels.size == scores.size) {
            val maxIndex = scores.indices.maxByOrNull { scores[it] } ?: -1

            if (scores[maxIndex] >= 0.5f) { val resultText = "The Result for this image \n" +
                        "${labels[maxIndex]}: ${NumberFormat.getPercentInstance().format(scores[maxIndex])}"
                binding.resultText.text = resultText
            } else { val resultText = "${labels[0]}: ${NumberFormat.getPercentInstance().format(scores[0])}\n" +
                        "${labels[1]}: ${NumberFormat.getPercentInstance().format(scores[1])}"
                binding.resultText.text = resultText
            }
        }
    }
}
