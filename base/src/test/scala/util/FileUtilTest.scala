package util

import org.scalatest._

/**
 * Created by goldratio on 1/21/15.
 */
class FileUtilTest extends FlatSpec with Matchers {

  "A File downloader" should "download a file" in {
    FileUtil.downloadFile2("https://data.baltimorecity.gov/api/views/dz54-2aru/rows.csv?accessType=DOWNLOAD", "./data/camera.csv")
  }

}
