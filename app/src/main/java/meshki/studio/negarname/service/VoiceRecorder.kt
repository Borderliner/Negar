package meshki.studio.negarname.service

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.widget.Toast
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.lang.ref.WeakReference

class VoiceRecorder(ctx: Context, path: String = "") {
    companion object {
        private var _isRecording = false
        private var _isPlaying = false
    }

    private val _ctx = WeakReference(ctx)
    private val _path = setPath(path)
    private var _recorder: MediaRecorder? = null
    private var _player: MediaPlayer? = null

    fun startRecording(): Boolean {
        try {
            if (!_isPlaying) {
                _recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    MediaRecorder(_ctx.get()!!)
                } else {
                    MediaRecorder()
                }
                _recorder?.apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    setAudioChannels(1)
                    setAudioEncodingBitRate(96000)
                    setAudioSamplingRate(44100)
                    setOutputFile(getPath())
                }
                Timber.tag("VoiceRecorder:Record").i(getPath())
                _recorder?.prepare()
                _recorder?.start()
                _isRecording = true
                return true
            } else {
                Toast.makeText(
                    _ctx.get()!!,
                    "Please stop record playing first.",
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
            _recorder?.apply {
                stop()
                release()
            }
            _recorder = null
            _isRecording = false
            return true
        } else return false
    }

    fun startPlaying(): Boolean {
        try {
            if (!_isRecording) {
                _player = MediaPlayer()
                _player?.apply {
                    setDataSource(getPath())
                    prepare()
                    start()
                }
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
            _player?.release()
            _player = null
            _isPlaying = false
            return true
        } else {
            return false
        }
    }

    fun setPath(path: String): String {
        return sanitizePath(path)
    }

    fun getPath(): String {
        return _path
    }

    private fun checkAndCreateFolder(folder: String): String {
        val path = "${_ctx.get()!!.filesDir}/$folder"
        val file = File(path)
        if (!file.exists()) {
            try {
                file.mkdir()
            } catch (e: SecurityException) {
                Toast.makeText(
                    _ctx.get()!!,
                    "Cannot create records directory.",
                    Toast.LENGTH_LONG
                ).show()
                Timber.tag("VoiceRecorder:Check").e(e)
            }
        }
        return path
    }

    private fun sanitizePath(path: String): String {
        val _path = "$path.aac"
        val recordsPath = checkAndCreateFolder("records")
        val finalPath = "$recordsPath/$_path"
        Timber.tag("VoiceRecorder:Sanitize").i(finalPath)
        return finalPath
    }
}
