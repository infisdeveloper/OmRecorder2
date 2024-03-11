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
package omrecorder2

import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import kotlinx.coroutines.*

abstract class AbstractRecorder protected constructor(
    @JvmField protected val pullTransport: PullTransport,
    @JvmField protected val file: File
) : Recorder {
    private val scope = CoroutineScope(Dispatchers.IO)
    private var outputStream: OutputStream? = null

    private val recordingTask = suspend {
        try {
            pullTransport.start(outputStream)
        } catch (e: IOException) {
            throw RuntimeException(e)
        } catch (e: IllegalStateException) {
            throw RuntimeException("AudioRecord state has uninitialized state", e)
        }
    }

    override fun startRecording() {
        outputStream = outputStream(file)
        scope.launch {
            recordingTask()
        }
    }

    private fun outputStream(file: File?): OutputStream {
        if (file == null) {
            throw RuntimeException("file is null !")
        }
        return try {
            FileOutputStream(file)
        } catch (e: FileNotFoundException) {
            throw RuntimeException(
                "could not build OutputStream from this file " + file.name, e
            )
        }
    }

    @Throws(IOException::class)
    override fun stopRecording() {
        scope.launch {
            pullTransport.stop()
            outputStream?.apply {
                flush()
                close()
            }
            onStopRecording()
        }
    }

    protected open fun onStopRecording() {}

    override fun pauseRecording() {
        pullTransport.pullableSource().isEnableToBePulled(false)
    }

    override fun resumeRecording() {
        pullTransport.pullableSource().isEnableToBePulled(true)
        scope.launch {
            recordingTask()
        }
    }
}

