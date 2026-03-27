package com.qualitypaper.fluentfusion.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateService {

  public static final Map<String, Integer> monthDays = new HashMap<>();

  static {
    monthDays.put("January", 31);
    monthDays.put("February", 28);
    monthDays.put("FebruaryLeap", 29);
    monthDays.put("March", 31);
    monthDays.put("April", 30);
    monthDays.put("May", 31);
    monthDays.put("June", 30);
    monthDays.put("July", 31);
    monthDays.put("August", 31);
    monthDays.put("September", 30);
    monthDays.put("October", 31);
    monthDays.put("November", 30);
    monthDays.put("December", 31);
  }

  public static boolean isToday(Long millis) {
    return isSameDay(new Date(millis), new Date());
  }

  public static boolean isYesterday(Long millis) {
    return isSameDay(new Date(millis), new Date(System.currentTimeMillis() - 86400000));
  }

  public static boolean isSameDay(Date date1, Date date2) {
    LocalDate localDate1 = date1.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
    LocalDate localDate2 = date2.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();

    return localDate1.isEqual(localDate2);
  }


  public static String now() {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    return LocalDateTime.now().format(dateTimeFormatter);
  }

  // returns 1 even if the difference is 0
  public static int getDifferenceInDays(long firstMillis, long secondMillis) {
    long firstDays = firstMillis / (100 * 60 * 60 * 24);
    long secondDays = secondMillis / (100 * 60 * 60 * 24);
    long abs = Math.abs(firstDays - secondDays);
    return Math.toIntExact(abs == 0 ? 1 : abs);
  }

  // returns the difference in milliseconds
  public static Duration minus(LocalDateTime first, LocalDateTime second) {
    return Duration.ofMillis(first.toInstant(ZoneOffset.UTC).toEpochMilli() -
            second.toInstant(ZoneOffset.UTC).toEpochMilli());
  }

}
