package meshki.studio.negarname.ui.util

import java.io.File

fun File.copyTo(file: File) {
    inputStream().use { input ->
        file.outputStream().use { output ->
            input.copyTo(output)
        }
    }
}