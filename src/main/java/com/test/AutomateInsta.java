package com.test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
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
   private String instaPrefixUrl1 = "https://www.instagram.com/p/";
   private String instaPrefixUrl2 = "https://instagram.com/p/";
   private By dateTag = By.className("im_message_date_split_text");
   
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
      
      driver.get(baseUrl);

      driver.manage().window().maximize();

      TelegramUtil.login(driver); // logged in
   }

   @Test
   public void test() throws Exception
   {
      startAutomation();
   }

  
   private void startAutomation() throws InterruptedException, ParseException {

      String instaSuperGroupName = InstaUtil.fetchInstaTargetGroupName(); // User Input

      InstaUtil.selectInstaGroup(driver, instaSuperGroupName); // selected the mentioned group in User Input

      
      likeFirstDayLinksForTheSelectedGroup();
      
      
      likeSecondDayLinksForTheSelectedGroup();
      
      Utils.printEndSummaryLogs(clicked);
      // End of Program

   
   }

   private Set<String> getAllLinksSetFromSelectedMessages(WebElement wb, Set<String> instaLinks)
   {
      List<WebElement> matches = wb.findElements(By.tagName("a"));

      for (WebElement match : matches)
      {
         String clink = match.getText();
         if (clink.startsWith(instaPrefixUrl1) || clink.startsWith(instaPrefixUrl2))
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
         List<String> tabs2 = new ArrayList<String>(driver.getWindowHandles());
         driver.switchTo().window(tabs2.get(1));
         InstaUtil.likeInstaPost(instaLink, driver, clicked, clickedInInterval);
         driver.close();
         driver.switchTo().window(tabs2.get(0));
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

   
   private void likeFirstDayLinksForTheSelectedGroup() throws ParseException {

      // Now we need to scroll up to load at least previous day chats
      try
      {
         Stack<String> allDateTg = Utils.isDateElementsFoundByScroll(dateTag, driver, 2);
         List<String> allDateTags = new ArrayList<String>();
         for(int ii = 0; ii < allDateTg.size(); ii++) {
            String cDate = allDateTg.pop();
            if(!"".equals(cDate.trim())){
               allDateTags.add(cDate);
            }
         }
         commonStepsToLike(allDateTags.get(0));
      }
      catch (InterruptedException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }
   
   private void likeSecondDayLinksForTheSelectedGroup() throws ParseException {
      // Now we need to scroll up to load at least previous day chats
      try
      {
         Stack<String> allDateTg = Utils.isDateElementsFoundByScroll(dateTag, driver, 2);
         List<String> allDateTags = new ArrayList<String>();
         for(int ii = 0; ii < allDateTg.size(); ii++) {
            String cDate = allDateTg.pop();
            if(!"".equals(cDate.trim())){
               allDateTags.add(cDate);
            }
         }
         if(allDateTags.size() > 1)
         commonStepsToLike(allDateTags.get(1));
      }
      catch (InterruptedException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }
   
   
   private void commonStepsToLike(String msgDate) throws InterruptedException, ParseException {
      
      List<WebElement> messagesToProcess = new ArrayList<WebElement>();
      Set<String> instaLinks = new HashSet<String>();
      
         Utils.giveSpaceInLogs(2);
         
         
         By byopen = By.className("im_history_messages_peer");
         //WebElement parentOfMessages = Utils.fluentWait(byopen, driver, 30, 5);
         Utils.giveSpaceInLogs(2);
         
         List<WebElement> allMessags = driver.findElements(By.className("im_history_message_wrap"));
         Stack<WebElement> msgStack = new Stack<WebElement>();
         for(int kk=0; kk < allMessags.size(); kk++) {
            msgStack.push(allMessags.get(kk));
         }
         
         System.out.println("Messages total count: "+allMessags.size());
         
         Utils.giveSpaceInLogs(2);
         System.out.println("Processing all the messages in the group. This process may take longer than usual based on the number of messages. Please wait ...");
         for (int ii = (msgStack.size() -1); ii >= 0; ii--)
         {
            WebElement message = msgStack.pop();

            if (Utils.isElementPresent(dateTag, message))
            {
               String date = message.findElement(dateTag).getText().trim();
               if("".equals(date)) {
                  continue;
               }
               
               if(date.equals(msgDate))
                  break;

            }

            messagesToProcess.add(message);
            System.out.println(".");
         }
         
         Utils.giveSpaceInLogs(2);
         System.out.println("Thanks for waiting! All messages have been processed. Now, automatic like will start in few moments.");
         for ( WebElement message: messagesToProcess)
         {
            getAllLinksSetFromSelectedMessages(message, instaLinks); // filled in instaLinks
         }

         Utils.giveSpaceInLogs(2);
         System.out.println("Below Insta links will be processed shortly. If not, there is something to look into:");
         Utils.giveSpaceInLogs(1);
         for (String link : instaLinks)
         {
            System.out.println("Insta Link: " + link);
         }

         startLikingLinks(instaLinks);

   }
   
   
   @AfterClass(alwaysRun = true)
   public void tearDown() throws Exception
   {
      Utils.giveSpaceInLogs(5);
      System.out.print("WARNING: Program is going to be terminated. Do you want to proceed?(y/n)?:");
      Scanner scanMobile = new Scanner(System.in);
      String switchOffOption = scanMobile.nextLine();
      
      if("y".equalsIgnoreCase(switchOffOption)) {
         driver.quit();
      } else if("n".equalsIgnoreCase(switchOffOption)){
         startAutomation();
      } else {
         tearDown();
      }
   }
   
}
