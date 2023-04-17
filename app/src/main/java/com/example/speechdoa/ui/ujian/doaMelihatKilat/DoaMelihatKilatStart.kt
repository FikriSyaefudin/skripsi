package com.example.speechdoa.ui.ujian.doaMelihatKilat

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.speechdoa.R
import com.example.speechdoa.base.BaseActivity
import com.example.speechdoa.data.DoaMelihatKilatEntity
import com.example.speechdoa.databinding.ActivityDoaMelihatKilatStartBinding
import com.example.speechdoa.ui.ujian.doaMelihatKilat.DoaMelihatKilatDone
import com.example.speechdoa.ui.ujian.doaMelihatKilat.DoaMelihatKilatModel

class DoaMelihatKilatStart : BaseActivity() {

    private lateinit var binding: ActivityDoaMelihatKilatStartBinding
    var listDoaMelihatKilat = ArrayList<DoaMelihatKilatEntity>()

    var current_position = 0
    var hasil = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoaMelihatKilatStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        println(this::binding.isInitialized)

        val viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
            DoaMelihatKilatModel::class.java)
        val doaMelihatKilat = viewModel.getDoaMelihatKilat()

        listDoaMelihatKilat.addAll(doaMelihatKilat)

        binding.btnMic.setOnClickListener {
            checkAudioPermission()
            binding.btnMic.setColorFilter(ContextCompat.getColor(this, R.color.mic_enabled_color))
            startSpeechToText()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

    }

    private fun startSpeechToText() {
        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar")

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(p0: Bundle?) {}

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(p0: Float) {}

            override fun onBufferReceived(p0: ByteArray?) {}

            override fun onEndOfSpeech() {
                binding.btnMic.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.mic_disabled_color
                    )
                )
            }

            override fun onError(p0: Int) {}
            override fun onResults(bundle: Bundle) {
                val result = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (result != null) {
                    println(result[0])
                    if (current_position < listDoaMelihatKilat.size - 1) {
                        if (result[0] == listDoaMelihatKilat[current_position].doa) {
                            println(result[0])
                            current_position++
                            hasil = "Benar"
                            Toast.makeText(applicationContext, "Benar", Toast.LENGTH_SHORT).show()
                        } else {
                            current_position++
                            hasil = "Salah"
                            Toast.makeText(applicationContext, "Kurang Tepat", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else if (result[0] == listDoaMelihatKilat[current_position].doa) {
                        println(result[0])
                        hasil = "Benar"
                        Toast.makeText(applicationContext, "Benar", Toast.LENGTH_SHORT).show()
                        val intent = Intent(applicationContext, DoaMelihatKilatDone::class.java)
                        intent.putExtra(DoaMelihatKilatDone.HASIL, hasil)
                        intent.putExtra(DoaMelihatKilatDone.SOAL, listDoaMelihatKilat[current_position].doa)
                        intent.putExtra(DoaMelihatKilatDone.JAWABAN, result[0])
                        startActivity(intent)
                        finish()
                    } else {
                        hasil = "Salah"
                        Toast.makeText(applicationContext, "Kurang Tepat", Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(applicationContext, DoaMelihatKilatDone::class.java)
                        intent.putExtra(DoaMelihatKilatDone.HASIL, hasil)
                        intent.putExtra(DoaMelihatKilatDone.SOAL, listDoaMelihatKilat[current_position].doa)
                        intent.putExtra(DoaMelihatKilatDone.JAWABAN, result[0])
                        startActivity(intent)
                        finish()
                    }
                }
            }

            override fun onPartialResults(p0: Bundle?) {}

            override fun onEvent(p0: Int, p1: Bundle?) {}

        })
        speechRecognizer.startListening(speechRecognizerIntent)
    }

    private fun checkAudioPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  // M = 23
            if (ContextCompat.checkSelfPermission(this, "android.permission.RECORD_AUDIO") != PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:com.example.speechdoa"))
                startActivity(intent)
                Toast.makeText(this, "Allow Microphone Permission", Toast.LENGTH_SHORT).show()
            }
        }
    }
}