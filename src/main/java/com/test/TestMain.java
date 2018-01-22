package com.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestMain
{

   public static void main(String[] args) throws ParseException
   {
      // TODO Auto-generated method stub
      
      String date = "Tuesday, January 16, 2018 6:23:39 PM";
      if(isWithInPast24Hours(date)) {
         System.out.println("true");
      }
   }
   
   public static Date parseDate(String dateStr) throws ParseException {
      String pattern = "EEEEE, MMMMM D, yyyy hh:mm:ss a";
      SimpleDateFormat simpleDateFormat =
              new SimpleDateFormat(pattern);
      Date date = simpleDateFormat.parse(dateStr);
      return date;
   }
   
   public static boolean isWithInPast24Hours(String messageDateTimeStr) throws ParseException {
      
      Date hoursBefore24 = new Date(System.currentTimeMillis() - (24 * 3600 * 1000));
      System.out.println(hoursBefore24);
      Date messageDateTime = parseDate(messageDateTimeStr);
      System.out.println(messageDateTime);
      return hoursBefore24.before(messageDateTime);
   }

}
