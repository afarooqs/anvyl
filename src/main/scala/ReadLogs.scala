/**
  * Downloads files from Google Cloud storage and processes them for statistics.
  *
  */
object ReadLogs {

  lazy val helper = new LogsHelper
  lazy val listOfFiles = List("https://storage.googleapis.com/anvyl-interviews/2017-07-18-orders-access.log.gz",
    "https://storage.googleapis.com/anvyl-interviews/2017-07-19-orders-access.log.gz",
    "https://storage.googleapis.com/anvyl-interviews/2017-07-20-orders-access.log.gz")
  lazy val preFilters = List("/ok", "Ruby")
  lazy val microseconds = 1000000
  lazy val lineRegex = ("([0-9]+.[0-9]+.[0-9]+.[0-9]+) - - (\\[.*?\\]) \".* (/.*?) .*\" ([0-9]+) ([0-9]+) \"-\" \"(.*)\" ([0-9]+)").r

  def main(args: Array[String]): Unit = {
    val date: String = helper.getDateFromConsole
    val fileToDownload = listOfFiles.filter(p => p.contains(date))

    if (!helper.isDateValid(date)) {
      println("You have exceeded the tries to start this program...")
    }
    else if (fileToDownload.isEmpty) {
      println("That file does not exist! Good bye.")
    }
    else {
      val fileName = "FileLog.log.gz"
      val downloadedFile = helper.downloadFileFromSource(fileToDownload.head, fileName)
      val lines = helper.readGZip(fileName)
      val linesTuple: Seq[(String, String, String, String, String, String, String)] =
        lines.map { l =>
          l match {
            case lineRegex(ip, timeStamp, endPoint, statusCode, count, client, responseTime) =>
              (ip, timeStamp, endPoint, statusCode, count, client, responseTime)
            case _ => ("", "", "", "", "", "", "")
          }
        }
      val filterOutRubyAndGetOk = linesTuple filter (f => (f._6 != preFilters(0)) && (f._3 != preFilters(1)))
      val allEndpoints: Set[String] = filterOutRubyAndGetOk.groupBy(ep => ep._3).keySet
      val allStatusCodes: List[String] = filterOutRubyAndGetOk.groupBy(status => status._4).keySet.toList.sorted
      val responseTimesInSeconds = filterOutRubyAndGetOk.map(_._7.toDouble / microseconds)

      //Output from here
      println("Max time: " + responseTimesInSeconds.max + " secs")
      println("Average time: " + helper.average(responseTimesInSeconds) + " secs")
      //For each of the endpoints filter their results
      for (endP <- allEndpoints) {
        val filterResultsByEndpoint = filterOutRubyAndGetOk.filter(r => r._3 == endP)
        println("Path: " + endP)
        //Filter another time, now for status codes
        for (sC <- allStatusCodes) {
          val filterFurtherByStatusCodes = filterResultsByEndpoint.filter(s => s._4 == sC)
          val sumOfResponsesByStatusCodes = filterFurtherByStatusCodes.size
          //Only print count if is at least 1 row in the logs for this status code
          if (sumOfResponsesByStatusCodes > 0)
            println("\tCode: " + sC + ": " + sumOfResponsesByStatusCodes)
        }
      }
    }
  }
}