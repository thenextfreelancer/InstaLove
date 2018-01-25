/**
 * 
 */
package com.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import com.google.common.base.Function;

/**
 * @author amishra
 *
 */
public class Utils
{
   public static void giveSpaceInLogs(int count)
   {
      for (int ii = 0; ii < count; ii++)
      {
         System.out.println("  ");
      }

   }
   
   public static boolean isElementPresent(By by, WebElement webElement) {
      try {
         List<WebElement> list = webElement.findElements(by);
         if(list.size()>0)
            return true;
         else
            return false;
         
      } catch (NoSuchElementException e) {
        return false;
      }
    }
   
   public static boolean isElementPresentInsta(By by, WebDriver driver) {
      try {
        driver.findElement(by);
        return true;
      } catch (NoSuchElementException e) {
        return false;
      }
    }
   
//   public static synchronized WebElement fluentWait(final By locator,final WebDriver driver, int timeout) {
//      Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
//              .withTimeout(timeout, TimeUnit.SECONDS)
//              .pollingEvery(5, TimeUnit.SECONDS)
//              .ignoring(NoSuchElementException.class);
//
//      WebElement el = wait.until(new Function<WebDriver, WebElement>() {
//          public WebElement apply(WebDriver driver) {
//              return driver.findElement(locator);
//          }
//      });
//
//      return  el;
//   }
   
   
   public static WebElement fluentWait(final By locator, final WebDriver driver, final int timeout, final int polling) throws InterruptedException {
      int ii = polling;
      while(ii < timeout) {
         List<WebElement> allEle = driver.findElements(locator);
         if (allEle.size() > 0)
         {
            return allEle.get(0);
         }
         Thread.sleep(polling*1000);
         ii += polling;
      }
      return null;
   }
   
   public static void scrollUp(final By locator, final WebDriver driver) throws InterruptedException {
      
      JavascriptExecutor jse = (JavascriptExecutor) driver;
      
      WebElement about = driver.findElement(locator);
      jse.executeScript("arguments[0].scrollBy(0, -500);",about);
      Thread.sleep(3000);
   }
   
   public static void scrollWindow(final WebDriver driver) throws InterruptedException {
      
      JavascriptExecutor jse = (JavascriptExecutor) driver;
      jse.executeScript("window.scrollBy(0,350)", "");
      Thread.sleep(1000);
   }
   
   public static void printEndSummaryLogs(Set<String> clicked) {
      Utils.giveSpaceInLogs(5);
      System.out.println("########################################## Exiting Utility! ################################## "); 
      Utils.giveSpaceInLogs(1);
      System.out.println("Information: Following links are clicked in total and hopefully liked by the software!!");
      for (Iterator<String> it = clicked.iterator(); it.hasNext();)
      {
         String link = it.next();
         System.out.println("Insta Links: "+link);
      }
      Utils.giveSpaceInLogs(3);
      System.out.println("########################################## Thank you for using this utility ################################## "); 
   }
   
   
   public static void printIntervalSummaryLogs(Set<String> clickedInInterval, int interval) {
      Utils.giveSpaceInLogs(5);
      System.out.println("########################################## Exiting Utility! ################################## "); 
      Utils.giveSpaceInLogs(1);
      System.out.println("Information: Following links are clicked in total and hopefully liked by the software!!");
      for (Iterator<String> it = clickedInInterval.iterator(); it.hasNext();)
      {
         String link = it.next();
         System.out.println("Insta Links: "+link);
      }
      Utils.giveSpaceInLogs(3);
      System.out.println("INFO: Running "+ interval+ " interval."); 
   }
   
   public static Date parseDate(String dateStr) throws ParseException {
      String pattern = "EEEEE, MMMMM D, yyyy hh:mm:ss a";
      SimpleDateFormat simpleDateFormat =
              new SimpleDateFormat(pattern);

      Date date = simpleDateFormat.parse(dateStr);
      return date;
   }
   
   public static boolean isWithInPast24Hours(String messageDateTimeStr) throws ParseException {
      
      Date hoursBefore24 = new Date(System.currentTimeMillis() - (TelegramUtil.HOURS_OLD_MESSAGES *3600 * 1000));
      
      Date messageDateTime = parseDate(messageDateTimeStr);
      
      return hoursBefore24.before(messageDateTime);
   }
}
