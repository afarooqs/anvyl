import java.io.{File, FileInputStream}
import java.net.URL
import java.util.zip.GZIPInputStream

import scala.sys.process._

/**
  * Helper methods for processing log files.
  */
class LogsHelper {

  /**
    * Averages a collection of Numeric values
    *
    * @param col Collection of [T]
    * @param num
    * @tparam T
    * @return
    */
  def average[T](col: Iterable[T])(implicit num: Numeric[T]) = new java.math.BigDecimal(num.toDouble(col.sum) / col.size).setScale(6, java.math.BigDecimal.ROUND_HALF_UP)

  /**
    * Read a gzip file into a Stream.
    *
    * @param file
    * @return
    */
  def readGZip(file: String): Stream[String] = {
    val lines = scala.collection.mutable.ListBuffer[String]()
    val in = new GZIPInputStream(new FileInputStream(file))
    for (line <- scala.io.Source.fromInputStream(in).getLines()) {
      lines += line
    }
    lines.toStream
  }

  /**
    * Reads in a date from console and retries 5 times if
    * user doesn't enter input in yyyy-MM-dd format
    *
    * @return date in yyyy-MM-dd format
    */
  def getDateFromConsole: String = {
    val maxTriesForInput = 5
    var count = 1
    var dateInput = io.StdIn.readLine("Please enter a date: ")
    while (!isDateValid(dateInput.trim) && count <= maxTriesForInput) {
      dateInput = io.StdIn.readLine("Please enter a date in the right format (yyyy-mm-dd): ")
      count += 1
    }
    dateInput
  }

  def isDateValid(input: String): Boolean = {
    lazy val dateFormat = ("[0-9]{4}-[0-9]{2}-[0-9]{2}").r
    input match {
      case dateFormat() => true
      case _ => false
    }
  }

  /**
    * Downloads file from Google Cloud Storage
    *
    * @param url
    * @param filename
    * @return Saved file
    */
  def downloadFileFromSource(url: String, filename: String): String =
    new URL(url) #> new File(filename) !!
}
