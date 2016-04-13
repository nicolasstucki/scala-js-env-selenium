package org.scalajs.jsenv.selenium

import java.io.File

import org.apache.commons.io.FileUtils

import org.openqa.selenium.chrome.ChromeDriverService
import org.openqa.selenium.remote._

object Chrome extends SeleniumBrowser {
  def name: String = "Chrome"

  def newDriver: BrowserDriver = new ChromeDriver

  private class ChromeDriver extends BrowserDriver {
    protected def newDriver(): RemoteWebDriver = {
      val caps = DesiredCapabilities.chrome()
      val service = {
        /* Activate the silent ChromeDriverService silent mode,
         * see ChromeDriverService.createDefaultService
         */
        new ChromeDriverService.Builder().withSilent(true).usingAnyFreePort.build
      }
      new org.openqa.selenium.chrome.ChromeDriver(service, caps)
    }

    override protected def afterClose(): Unit = {
      val tempDir = new File(System.getProperty("java.io.tmpdir"))
      // Delete Google Chrome temp profiles
      for (file <- tempDir.listFiles()) {
        if (file.getName.matches("chrome-[0-9a-zA-Z]{6}"))
          FileUtils.deleteDirectory(file)
      }
    }
  }
}
