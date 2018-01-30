/**
 * 
 */
package com.test;

import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

/**
 * @author amishra
 *
 */
public class InstaUtil
{

   public static final String instaLoginPageUrlKey = "accounts/login";

   public static final int PAGE_LIKE_THRESHOLD = 50; // limit to like this number of pages in an interval(continuously)

   public static final int PAGE_LIKE_PAUSE_IN_MINUTES = 8; // In Minutes - Pause the application for like the message by this minutes

   public static void loginToInstaThenLike(String instaLink, WebDriver driver, Set<String> clicked, Set<String> clickedInInterval) throws InterruptedException
   {
      // Need login in Instagram
      Thread.sleep(2000);
      Utils.giveSpaceInLogs(5);
      System.out.println("########################################## Log in to Instagram ################################## ");
      System.out.println("Please provide your User Id for Instagram and press enter: ");
      Scanner scanInstaId = new Scanner(System.in);
      String instaId = scanInstaId.nextLine();
      driver.findElement(By.name("username")).sendKeys(instaId);
      Thread.sleep(2000);
      System.out.println("----------------------------------------------------------------------------------------------------");
      Utils.giveSpaceInLogs(5);
      System.out.println("Please provide your password for Instagram and press enter: ");
      Scanner scanInstaPass = new Scanner(System.in);
      String instaPass = scanInstaPass.nextLine();
      driver.findElement(By.name("password")).sendKeys(instaPass);
      Thread.sleep(2000);

      WebElement wb = driver.findElement(By.cssSelector("span[id=react-root]"));
      Utils.giveSpaceInLogs(5);
      System.out.println("Logging you with with User Id: " + instaId);
      Utils.giveSpaceInLogs(2);
      System.out.println("Note:");
      System.out.println("You need to do it everytime you run this utility as it does not save anything anywhere.");

      wb.findElement(By.tagName("button")).click();

      likeInstaPost(instaLink, driver, clicked, clickedInInterval);
   }

   public static void likeInstaPost(String instaLink, WebDriver instaDriver, Set<String> clicked, Set<String> clickedInInterval) throws InterruptedException
   {
      Utils.scrollWindow(instaDriver);
      By heartButton = By.className("coreSpriteHeartOpen");
      boolean isHeartPresent = Utils.isElementPresentInsta(heartButton, instaDriver);
      if (isHeartPresent)
      {
         try
         {
            WebElement heart = instaDriver.findElement(heartButton);
            WebElement parent = heart.findElement(By.xpath(".."));
            try {
               parent.click();
            } catch(WebDriverException e) {
               heart.click();
            }
            

            String previousURL = instaDriver.getCurrentUrl();

            if (previousURL.contains(instaLoginPageUrlKey))
            {
               loginToInstaThenLike(instaLink, instaDriver, clicked, clickedInInterval);
               clicked.add(instaLink);
            }
            clicked.add(instaLink);
            clickedInInterval.add(instaLink);
         }
         catch (WebDriverException e)
         {
            System.out.println("WARNING: The following Insta link is not liked by software. Please take care manually:" + instaLink);
         }

      }
      else
      {
         Utils.giveSpaceInLogs(2);
         // move on. I assume it is already liked! .. logging a message
         System.out.println("Log Message: The post on insta: " + instaLink + " is already liked!");
      }
      
      Thread.sleep(500);
   }

   public static String fetchInstaTargetGroupName() throws InterruptedException
   {
      Thread.sleep(1000);
      Utils.giveSpaceInLogs(5);
      System.out.println("########################################## Enter group Name to inspect for Instagram URLs ################################## ");
      Utils.giveSpaceInLogs(2);
      System.out.println("Please enter group Name to inspect for Instagram URLs(it is case in-sensitive) and press enter: ");
      Scanner scanGroup = new Scanner(System.in);
      String instaSuperGroupName = scanGroup.nextLine();
      Utils.giveSpaceInLogs(2);

      return instaSuperGroupName;
   }

   public static void selectInstaGroup(WebDriver driver, String instaSuperGroupName)
   {
      Utils.giveSpaceInLogs(2);
      System.out.println("Searching Super Group: " + instaSuperGroupName + " for Instagram links...");

      List<WebElement> els = driver.findElements(By.className("im_dialog_peer")); // list of groups in telegram
      WebElement grp = null;
      for (WebElement el : els)
      {
         WebElement span = el.findElement(By.tagName("span"));
         if (span.getText().contains(instaSuperGroupName))
         {
            grp = el;
            break;
         }
      }

      if (grp != null)
      {
         grp.click(); // mentioned group selected
      }
      
      try
      {
         Utils.scrollDown(driver);
      }
      catch (InterruptedException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }
}
