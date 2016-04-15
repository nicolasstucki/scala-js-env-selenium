package org.scalajs.jsenv.selenium

<<<<<<< HEAD
import java.io.File
=======
import org.scalajs.core.tools.io._
import org.openqa.selenium.remote._
import java.{util => ju}
import java.io.{File, FilenameFilter}
>>>>>>> wip

import org.json.simple._
import org.json.simple.parser.JSONParser
import org.apache.commons.io.FileUtils
import org.openqa.selenium.Cookie

<<<<<<< HEAD
import org.openqa.selenium.remote.RemoteWebDriver
=======
import scala.annotation.tailrec
import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer
import scala.io.Source
>>>>>>> wip


object Firefox extends SeleniumBrowser {
  def name: String = "Firefox"

  def newDriver: BrowserDriver = new FirefoxDriver

  private class FirefoxDriver extends BrowserDriver {
    protected def newDriver(): RemoteWebDriver =
      new org.openqa.selenium.firefox.FirefoxDriver()

<<<<<<< HEAD
    override protected def afterClose(): Unit = {
=======
    protected def newConsoleLogsIterator(): Iterator[String] = {
      val buf = new ArrayBuffer[String]
      @tailrec def addRemainingLogsToBuffer(): Unit = {
        getWebDriver.executeAsyncScript(popHijackedConsoleScript) match {
          case logs: ju.List[_] =>
            logs.foreach(log => buf.append(log.toString))
            if (logs.size() != 0)
              addRemainingLogsToBuffer()

          case msg => BrowserDriver.illFormattedScriptResult(msg)
        }
      }
      addRemainingLogsToBuffer()
      buf.toArray.toIterator
    }

    /** Closes the instance of the browser. */
    override def close(): Unit = {
      val sessionId = getWebDriver.getSessionId.toString
      if (isOpened) {
        getWebDriver.executeScript(
            s"sessionStorage.setItem('seleniumJSEnvSessionId', '$sessionId');")

        val tempProfile = getTempProfile(sessionId)

        super.close()

        println("deleting " + tempProfile)
        // FileUtils.deleteDirectory(tempProfile)
      } else {
        super.close()
      }
    }

    private def getTempProfile(sessionId: String): File = {
>>>>>>> wip
      val tempDir = new File(System.getProperty("java.io.tmpdir"))
      val profiles = tempDir.listFiles(new FilenameFilter {
        def accept(dir: File, name: String): Boolean =
          name.matches("anonymous\\d+webdriver-profile")
      })

      @tailrec def findTempProfile(): File = {
        val jsonParser = new JSONParser
        profiles.find { profile =>
          val profileFile =
            new File(profile.getAbsolutePath + "/sessionstore.js")
          profileFile.exists && {
            try {
              val jsonStr = Source.fromFile(profileFile).mkString
              val store = jsonParser.parse(jsonStr).asInstanceOf[JSONObject]
              val windows = store.get("windows").asInstanceOf[JSONArray]
              val window = windows.get(0).asInstanceOf[JSONObject]
              val tabs = window.get("tabs").asInstanceOf[JSONArray]
              val tab = tabs.get(0).asInstanceOf[JSONObject]
              val storage = tab.get("storage").asInstanceOf[JSONObject]
              storage.values().exists {
                case data: JSONObject => data.get("seleniumJSEnvSessionId") == sessionId
                case _ => false
              }
            } catch {
              case _: NullPointerException           => false
              case _: ArrayIndexOutOfBoundsException => false
              case _: ClassCastException             => false
            }
          }
        } match {
          case Some(profileToDelete) => profileToDelete

          case None =>
            println("waiting ")
            Thread.sleep(300)
            findTempProfile()

        }
      }

      findTempProfile()
    }
  }
}
