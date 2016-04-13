package org.scalajs.jsenv.selenium

import java.io.File

import org.apache.commons.io.FileUtils

import org.openqa.selenium.remote.RemoteWebDriver


object Firefox extends SeleniumBrowser {
  def name: String = "Firefox"

  def newDriver: BrowserDriver = new FirefoxDriver

  private class FirefoxDriver extends BrowserDriver {
    protected def newDriver(): RemoteWebDriver =
      new org.openqa.selenium.firefox.FirefoxDriver()

    override protected def afterClose(): Unit = {
      val tempDir = new File(System.getProperty("java.io.tmpdir"))
      // Delete Firefox temp profiles
      for (file <- tempDir.listFiles()) {
        if (file.getName.matches("anonymous\\d+webdriver-profile"))
          FileUtils.deleteDirectory(file)
      }
    }
  }
}
