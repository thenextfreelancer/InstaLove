package com.test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AutomateInsta
{
   private WebDriver driver;

   // System Defined
   private String baseUrl = "https://web.telegram.org";
   private String instaPrefixUrl = "https://www.instagram.com/p/";

   private String loggingDateFormat = "yyyy/MM/dd HH:mm:ss";

   // User Input for testing only
   private String telegram_mobile_number_test = "";
   private String instaSuperGroupName_test = "My_Group";
   private String instaId_test = "abc@gmail.com";
   private String instaPass_test = "1234";

   private Set<String> clicked = new HashSet<String>(); // Set of total links which are clicked;

   /**
    * Set of links which are clicked in the given interval. This size should not exceed to {@link InstaUtil.PAGE_LIKE_THRESHOLD_IN_INTERVAL}
    */

   @BeforeClass(alwaysRun = true)
   public void setUp() throws Exception
   {
      System.setProperty("webdriver.chrome.driver", "lib/chromedriver.exe");
      driver = new ChromeDriver();
      driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
   }

   @Test
   public void test() throws Exception
   {
      driver.get(baseUrl);

      driver.manage().window().maximize();

      TelegramUtil.login(driver); // logged in

      String instaSuperGroupName = InstaUtil.fetchInstaTargetGroupName(); // User Input

      InstaUtil.selectInstaGroup(driver, instaSuperGroupName); // selected the mentioned group in User Input

      // Now we need to scroll up to load at least previous day chats
      Utils.scrollUp(By.cssSelector("div.im_history_scrollable_wrap.nano-content"), driver);

      By byopen = By.className("im_history_messages_peer");
      WebElement parentOfMessages = Utils.fluentWait(byopen, driver, 30);

      List<WebElement> allMessages = parentOfMessages.findElements(By.className("im_history_message_wrap"));
      List<WebElement> messagesToProcess = new ArrayList<WebElement>();
      boolean startAdd = false;
      Set<String> instaLinks = new HashSet<String>();

      By dateTag = By.className("im_message_date_split_text");
      List<WebElement> allDateTags = parentOfMessages.findElements(dateTag);
      int size = allDateTags.size();

      if (size > 0)
      {

         // check if last element is blank, if yes, reduce the size by 1
         String lastElText = allDateTags.get(size - 1).getText();
         if ("".equals(lastElText))
         {
            size = size - 1;
         }

         int index = 0;
         if (size > 1) // the start starts today or you deleted all chats
            index = size - 2;

         for (int ii = 0; ii < size; ii++)
         {
            System.out.println(allDateTags.get(ii).getText());
         }
         WebElement secondLastDateEl = allDateTags.get(index);
         String dStr = secondLastDateEl.getText();
         String dateTemp = null;

         /**
          * Message filter to get messages only from just previous day
          * 
          */
         for (WebElement message : allMessages)
         {
            if (Utils.isElementPresent(dateTag, message))
            {
               WebElement el = message.findElement(dateTag);
               dateTemp = el.getText();
               if (dStr.equalsIgnoreCase(dateTemp))
               {
                  startAdd = true;
               }
               if (startAdd && !"".equals(dateTemp))
               {
                  dStr = dateTemp;
               }
               System.out.println(dateTemp);
            }

            if (startAdd)
            {
               /**
                * 24 Hours old filter applied
                * 
                */
               if (is24HoursOld(message, dStr))
               {
                  messagesToProcess.add(message); // Message Filter to get only 24 hours old messages
               }
            }
         }

         for (WebElement message : messagesToProcess)
         {
            getAllLinksSetFromSelectedMessages(message, instaLinks); // filled in instaLinks
         }

         startLikingLinks(instaLinks);
      }

      Utils.printEndSummaryLogs(clicked);
   }

   @AfterClass(alwaysRun = true)
   public void tearDown() throws Exception
   {
      driver.quit();
   }

   private Set<String> getAllLinksSetFromSelectedMessages(WebElement wb, Set<String> instaLinks)
   {
      List<WebElement> matches = wb.findElements(By.tagName("a"));

      for (WebElement match : matches)
      {
         String clink = match.getText();
         if (clink.startsWith(instaPrefixUrl))
         {
            boolean doAdd = true;
            for (Iterator<String> it = clicked.iterator(); it.hasNext();)
            {
               String link = it.next();
               if (link.equals(clink))
               {
                  doAdd = false;
                  break;
               }
            }

            if (doAdd)
            {
               instaLinks.add(clink);
            }

         }

      }

      return instaLinks;
   }

   private void startLikingLinks(Set<String> instaLinks) throws InterruptedException
   {
      // Starting opening the Insta inks and liking it!

      int count = 0, interval = 1;
      Set<String> clickedInInterval = new HashSet<String>();
      for (String instaLink : instaLinks)
      {
         driver.findElement(By.linkText(instaLink)).click();
         Thread.sleep(5000);
         List<String> tabs2 = new ArrayList<String>(driver.getWindowHandles());
         driver.switchTo().window(tabs2.get(1));
         InstaUtil.likeInstaPost(instaLink, driver, clicked, clickedInInterval);
         driver.close();
         Thread.sleep(2000);
         driver.switchTo().window(tabs2.get(0));
         Thread.sleep(2000);
         count++;

         // Put constraint for InstaUtil.PAGE_LIKE_THRESHOLD messages per loop
         if (count == InstaUtil.PAGE_LIKE_THRESHOLD)
         {
            Thread.sleep(InstaUtil.PAGE_LIKE_PAUSE_IN_MINUTES * 60 * 1000);
            Utils.printIntervalSummaryLogs(clickedInInterval, interval);
            interval++;
            clickedInInterval = new HashSet<String>();
         }
      }
   }

   private boolean is24HoursOld(WebElement message, String dateStr)
   {
      By timeTag = By.className("im_message_date_text");
      if (Utils.isElementPresent(timeTag, message))
      {
         WebElement el = message.findElement(timeTag);
         String timeTxt = el.getAttribute("data-content");
         if (timeTxt.isEmpty())
         {
            timeTxt = "00:00:01 AM";
         }

         String dateTime = dateStr + " " + timeTxt;

         try
         {
            if (Utils.isWithInPast24Hours(dateTime))
            {
               return true;
            }
         }
         catch (ParseException e)
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
         }
         System.out.println(dateTime);
      }

      return false;
   }

}
