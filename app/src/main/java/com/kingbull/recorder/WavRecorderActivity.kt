/**
 * Copyright 2017 Kailash Dabhi (Kingbull Technology)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kingbull.recorder

import android.media.AudioFormat
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import omrecorder2.AudioChunk
import omrecorder2.AudioRecordConfig
import omrecorder2.OmRecorder.wav
import omrecorder2.PullTransport
import omrecorder2.PullableSource
import omrecorder2.Recorder
import omrecorder2.WriteAction
import java.io.File
import java.io.IOException

/**
 * @author Kailash Dabhi
 * @date 18-07-2016. Copyright (c) 2017 Kingbull Technology. All rights reserved.
 */
class WavRecorderActivity : AppCompatActivity() {
    var recorder: Recorder? = null
    var recordButton: ImageView? = null
    var skipSilence: CheckBox? = null
    private var pauseResumeButton: Button? = null
    val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recorder)
        supportActionBar!!.title = "Wav Recorder"
        setupRecorder()
        skipSilence = findViewById<View>(R.id.skipSilence) as CheckBox
        skipSilence!!.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                setupNoiseRecorder()
            } else {
                setupRecorder()
            }
        }
        recordButton = findViewById<View>(R.id.recordButton) as ImageView
        recordButton!!.setOnClickListener {
            coroutineScope.launch {
                recorder!!.startRecording()
            }
            skipSilence!!.isEnabled = false
        }
        findViewById<View>(R.id.stopButton).setOnClickListener {
            try {
                coroutineScope.launch {
                    recorder!!.stopRecording()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            skipSilence!!.isEnabled = true
            recordButton!!.post { animateVoice(0f) }
        }
        pauseResumeButton = findViewById<View>(R.id.pauseResumeButton) as Button
        pauseResumeButton!!.setOnClickListener(object : View.OnClickListener {
            var isPaused = false
            override fun onClick(view: View) {
                if (recorder == null) {
                    Toast.makeText(
                        this@WavRecorderActivity, "Please start recording first!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                if (!isPaused) {
                    pauseResumeButton!!.text = getString(R.string.resume_recording)
                    coroutineScope.launch {
                        recorder!!.pauseRecording()
                    }
                    pauseResumeButton!!.postDelayed({ animateVoice(0f) }, 100)
                } else {
                    pauseResumeButton!!.text = getString(R.string.pause_recording)
                    coroutineScope.launch {
                        recorder!!.resumeRecording()
                    }
                }
                isPaused = !isPaused
            }
        })
    }

    private fun setupRecorder() {
        recorder = wav(
            PullTransport.Default(mic(), object : PullTransport.OnAudioChunkPulledListener {
                override fun onAudioChunkPulled(audioChunk: AudioChunk?) {
                    animateVoice((audioChunk!!.maxAmplitude() / 200.0).toFloat())
                }
            }), file()
        )
    }

    private fun setupNoiseRecorder() {
        recorder = wav(
            PullTransport.Noise(
                mic(),
                object : PullTransport.OnAudioChunkPulledListener {
                    override fun onAudioChunkPulled(audioChunk: AudioChunk?) {
                        animateVoice((audioChunk!!.maxAmplitude() / 200.0).toFloat())
                    }
                },
                WriteAction.Default(),
                object : Recorder.OnSilenceListener {
                    override fun onSilence(silenceTime: Long) {
                        Log.e("silenceTime", silenceTime.toString())
                        Toast.makeText(
                            this@WavRecorderActivity, "silence of $silenceTime detected",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, 200
            ), file()
        )
    }

    private fun animateVoice(maxPeak: Float) {
        recordButton!!.animate().scaleX(1 + maxPeak).scaleY(1 + maxPeak).setDuration(10).start()
    }

    private fun mic(): PullableSource {
        return PullableSource.Default(
            AudioRecordConfig.Default(
                MediaRecorder.AudioSource.MIC, AudioFormat.ENCODING_PCM_16BIT,
                AudioFormat.CHANNEL_IN_MONO, 44100
            )
        )
    }

    private fun file(): File {
        return File(Environment.getExternalStorageDirectory(), "kailashdabhi.wav")
    }
}
