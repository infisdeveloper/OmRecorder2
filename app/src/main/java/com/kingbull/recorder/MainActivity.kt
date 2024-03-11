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

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

/**
 * @author Kailash Dabhi
 * @date 18-07-2016.
 * Copyright (c) 2017 Kingbull Technology. All rights reserved.
 */
class MainActivity : AppCompatActivity() {
    var listView: ListView? = null
    var demoArray = arrayOf("Pcm Recorder", "Wav Recorder")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listView = findViewById<View>(android.R.id.list) as ListView
        listView!!.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, demoArray)
        listView!!.onItemClickListener = OnItemClickListener { adapterView, view, i, l ->
            when (demoArray[i]) {
                DEMO_PCM -> startActivity(
                    Intent(
                        this@MainActivity,
                        PcmRecorderActivity::class.java
                    )
                )

                DEMO_WAV -> startActivity(
                    Intent(
                        this@MainActivity,
                        WavRecorderActivity::class.java
                    )
                )
            }
        }
    }

    companion object {
        private const val DEMO_PCM = "Pcm Recorder"
        private const val DEMO_WAV = "Wav Recorder"
    }
}
