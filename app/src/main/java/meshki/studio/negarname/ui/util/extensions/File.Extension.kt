package meshki.studio.negarname.ui.util.extensions

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel

fun File.copyTo(file: File) {
    inputStream().use { input ->
        file.outputStream().use { output ->
            input.copyTo(output)
        }
    }
}
@Throws(IOException::class)
fun copyFile(sourceFile: File?, destFile: File) {
    if (!destFile.parentFile?.exists()!!) destFile.parentFile?.mkdirs()
    if (!destFile.exists()) {
        destFile.createNewFile()
    }
    var source: FileChannel? = null
    var destination: FileChannel? = null
    try {
        source = FileInputStream(sourceFile).channel
        destination = FileOutputStream(destFile).channel
        destination.transferFrom(source, 0, source.size())
    } finally {
        source?.close()
        destination?.close()
    }
}
