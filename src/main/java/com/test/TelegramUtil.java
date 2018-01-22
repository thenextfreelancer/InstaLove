/**
 * 
 */
package com.test;

import java.util.Scanner;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * @author amishra
 *
 */
public class TelegramUtil
{

   public static int HOURS_OLD_MESSAGES = 24; //in hours
   
   public static WebElement login(WebDriver driver) throws InterruptedException
   {
      Thread.sleep(10000);
      Utils.giveSpaceInLogs(5);
      System.out.println("########################################## Log in to Telegram ################################## ");
      Utils.giveSpaceInLogs(2);
      System.out.println("Please provide your Cell Number for Telegram and press enter: ");
      Scanner scanMobile = new Scanner(System.in);
      String mobile = scanMobile.nextLine();
      Utils.giveSpaceInLogs(1);
      System.out.println("INFO: Logging you with your cell number: " + mobile);
      Utils.giveSpaceInLogs(2);
      System.out.println("Note:");
      System.out.println("You need to do it everytime you run this utility as it does not save anything anywhere.");

      driver.findElement(By.name("phone_number")).sendKeys(mobile); // populate mobile number
      Thread.sleep(5000);
      driver.findElement(By.className("login_head_submit_btn")).click(); //Next button click

      Thread.sleep(5000);
      driver.findElement(By.className("btn-md-primary")).click();  //alert ok click
      Utils.giveSpaceInLogs(5);
      Thread.sleep(2000);
      System.out.println("Input your OTP in the Given box to proceed. This is one time activity per session.");
      Utils.giveSpaceInLogs(1);
      
      By elementToVarifyLogin = By.className("icon-hamburger-wrap"); // This item acknowledges that dashboard is opened.
      return Utils.fluentWait(elementToVarifyLogin, driver, 120);
   }

//   public static void getConfigurationForPolling() throws InterruptedException
//   {
//      Utils.giveSpaceInLogs(5);
//
//      System.out.println("########################################## Enter configurations for the selected group ################################## ");
//      System.out.println(
//         "Please enter the time in minutes for utility to look for the Instagram Links in the selected group and press enter(Default is 60 minutes, hit with blank to choose default): ");
//      Scanner scanTimePeriodInMinutes = new Scanner(System.in);
//      String timePeriodInMinutesStr = scanTimePeriodInMinutes.nextLine();
//      if (!timePeriodInMinutesStr.isEmpty())
//      {
//         try
//         {
////            timePeriodInMinutes = Integer.parseInt(timePeriodInMinutesStr);
//         }
//         catch (Exception e)
//         {
//            System.out.println("Invalid input. It should be an integer. Setting the default value.");
////            timePeriodInMinutes = defaultTimePeriodInMinutes;
//         }
//
//      }
//      Thread.sleep(2000);
//
//      System.out.println(
//         "Please enter the polling frequency in minutes for utility to poll selected group and press enter(Default is 1 minute, hit with blank to choose default): ");
//      Scanner scanIntervalInMinutes = new Scanner(System.in);
//      String intervalInMinutesStr = scanIntervalInMinutes.nextLine();
//      if (!intervalInMinutesStr.isEmpty())
//      {
//         try
//         {
////            intervalInMinutes = Integer.parseInt(intervalInMinutesStr);
//         }
//         catch (Exception e)
//         {
//            System.out.println("Invalid input. It should be an integer. Setting the default value.");
////            intervalInMinutes = defaultIntervalInMinutes;
//         }
//      }
//      Thread.sleep(2000);
//   }
}
