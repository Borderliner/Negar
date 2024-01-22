package meshki.studio.negarname.service

import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import linc.com.amplituda.Amplituda
import linc.com.amplituda.Cache
import linc.com.amplituda.callback.AmplitudaErrorListener
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.lang.ref.WeakReference

class VoiceRecorder(ctx: Context, path: String = "") {
    companion object {
        private var _isRecording = false
        private var _isPlaying = false

        suspend fun getAmplitudes(path: String, context: Context): List<Int> = withContext(
            Dispatchers.IO) {
            val audioPath = "${context.filesDir.path}/$path"
            Timber.tag("VoiceRecorder:Waveform").i(audioPath)
            try {
                Amplituda(context).processAudio(audioPath, Cache.withParams(Cache.REUSE))
                    .get(AmplitudaErrorListener {
                        Timber.tag("Amplituda").w(it)
                    })
                    .amplitudesAsList()
            } catch (exc: Exception) {
                Timber.tag("Wave Processing").e(exc)
                listOf()
            }
        }

        fun getAudioFileDuration(path: String, context: Context): Long {
            return try {
                val audioPath = "${context.filesDir.path}/$path"
                val uri = Uri.parse(audioPath)
                val mmr = MediaMetadataRetriever()
                mmr.setDataSource(context, uri)
                val duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!
                duration.toLong()
            } catch (exc: Exception) {
                Timber.tag("Audio Processing").w(exc)
                0
            }
        }
    }

    private val _ctx = WeakReference(ctx)
    private var _path = sanitizePath(path)
    private val _recorder: MediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        MediaRecorder(ctx)
    } else {
        MediaRecorder()
    }
    private var _player: MediaPlayer = MediaPlayer()

    fun startRecording(): Boolean {
        try {
            if (!_isPlaying && !_isRecording) {
                _recorder.apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    setAudioChannels(1)
                    setAudioEncodingBitRate(96000)
                    setAudioSamplingRate(44100)
                    setOutputFile(getPath())
                }
                Timber.tag("VoiceRecorder:Record").i(getPath())
                _recorder.prepare()
                _recorder.start()
                _isRecording = true
                return true
            } else {
                Toast.makeText(
                    _ctx.get()!!,
                    "Please stop playing first.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: IOException) {
            Toast.makeText(
                _ctx.get()!!,
                "Cannot start voice recorder, I/O error occurred: $e",
                Toast.LENGTH_LONG
            ).show()
            Timber.tag("VoiceRecorder:Record").e(e)
        } catch (e: Exception) {
            Toast.makeText(
                _ctx.get()!!,
                "Unknown exception at voice recorder: $e",
                Toast.LENGTH_LONG
            ).show()
            Timber.tag("VoiceRecorder:Record").e(e)
        }
        return false
    }

    fun stopRecording(): Boolean {
        if (_isRecording) {
            _recorder.stop()
            _recorder.reset()
            _isRecording = false
            return true
        } else return false
    }

    fun startPlaying(): Boolean {
        try {
            if (!_isRecording && !_isPlaying) {
                _player = MediaPlayer()
                _player.apply {
                    setDataSource(getPath())
                    prepare()
                }
                _player.start()
                Timber.tag("VoiceRecorder:Play").i(getPath())
                _isPlaying = true
                return true
            } else {
                Toast.makeText(
                    _ctx.get()!!,
                    "Please stop recording before playing it.",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } catch (e: IOException) {
            Toast.makeText(
                _ctx.get()!!,
                "Cannot start voice player, I/O error occurred: $e",
                Toast.LENGTH_LONG
            ).show()
            Timber.tag("VoiceRecorder:Play").e(e)
        }
        return false
    }

    fun stopPlaying(): Boolean {
        if (_isPlaying) {
            _player.reset()
            _isPlaying = false
            return true
        } else {
            return false
        }
    }

    fun seekTo(msec: Int) {
        _player.seekTo(msec)
    }

    fun release() {
        _recorder.release()
        _player.release()
    }

    fun isRecording(): Boolean = _isRecording
    fun isPlaying(): Boolean = _isPlaying

    fun getPath(): String {
        return _path
    }

    fun setPath(path: String) {
        if (path.isNotEmpty()) {
            _path = sanitizePath(path)
        }
    }

    private fun checkAndCreateFolder(folder: String): String {
        Timber.tag("VoiceRecorder:Check").i("Checking record folder")
        val path = "${_ctx.get()!!.filesDir.absolutePath}${File.separator}$folder"
        Timber.tag("VoiceRecorder:Check").i("PATH: $path")
        val file = File(path)
        _ctx.get()!!.fileList().forEach {
            Timber.tag("VoiceRecorder:Check").i("File: $it")
        }

        if (!file.exists() or !file.isDirectory) {
            try {
                Timber.tag("VoiceRecorder:Check").i("Record folder doesn't exist")
                file.mkdirs()
            } catch (e: SecurityException) {
                Toast.makeText(
                    _ctx.get()!!,
                    "Cannot create records directory.",
                    Toast.LENGTH_LONG
                ).show()
                Timber.tag("VoiceRecorder:Check").e(e)
            }
        } else {
            Timber.tag("VoiceRecorder:Check").i("Record folder exists")
        }
        return path
    }

    private fun sanitizePath(path: String): String {
        val _path = "$path.aac"
        val recordsPath = checkAndCreateFolder("records")
        val finalPath = "$recordsPath${File.separator}$_path"
        Timber.tag("VoiceRecorder:Sanitize").i(finalPath)
        return finalPath
    }
}
