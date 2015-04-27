package util

import java.io._
import java.net.{ URLConnection, HttpURLConnection, URL }

/**
 * Created by goldratio on 1/21/15.
 */
object FileUtil {

  def downloadFile2(address: String, dest: String, rewrite: Boolean = false) = {
    val destFile = new File(dest)
    val url = new URL(address)
    downloadFile(url, destFile)
  }

  /**
   * Use "index.html" if URL ends with "/"
   */
  def targetName(url: URL): String = {
    val path = url.getPath
    val part = path.substring(path.lastIndexOf('/') + 1)
    if (part.nonEmpty) part else "index.html"
  }

  /**
   * Download file from URL to directory.
   */
  def downloadTo(url: URL, dir: File): File = {
    val file = new File(dir, targetName(url))
    downloadFile(url, file)
    file
  }

  /**
   * Download file from URL to given target file.
   */
  def downloadFile(url: URL, file: File): Unit = {
    val conn = url.openConnection
    try {
      downloadFile(conn, file)
    } finally conn match {
      // http://dumps.wikimedia.org/ seems to kick us out if we don't disconnect.
      case conn: HttpURLConnection => conn.disconnect
      // But only disconnect if it's a http connection. Can't do this with file:// URLs.
      case _                       =>
    }
  }

  /**
   * Download file from URL to given target file.
   */
  protected def downloadFile(conn: URLConnection, file: File): Unit = {
    val in = inputStream(conn)
    try {
      val out = outputStream(file)
      try {
        copy(in, out)
      } finally out.close
    } finally in.close
  }

  def copy(in: InputStream, out: OutputStream): Unit = {
    val buf = new Array[Byte](1 << 20) // 1 MB
    while (true) {
      val read = in.read(buf)
      if (read == -1) {
        out.flush
        return
      }
      out.write(buf, 0, read)
    }
  }

  /**
   * Get input stream. Mixins may decorate the stream or open a different stream.
   */
  protected def inputStream(conn: URLConnection): InputStream = conn.getInputStream

  /**
   * Get output stream. Mixins may decorate the stream or open a different stream.
   */
  protected def outputStream(file: File): OutputStream = new FileOutputStream(file)

}
