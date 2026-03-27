package com.qualitypaper.fluentfusion.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StringUtils {

  private StringUtils() {
  }

  public static String encodeMD5(String text) {
    try {
      MessageDigest messageDigest = MessageDigest.getInstance("MD5");
      byte[] digest = messageDigest.digest(text.getBytes(StandardCharsets.UTF_8));

      return IntStream.range(0, digest.length)
              .mapToObj(i -> Integer.toString((digest[i] & 0xff) + 0x100, 16)
                      .substring(1))
              .collect(Collectors.joining());
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }


  public static String removeSpecialCharacters(String s) {
    return s.replaceAll("[\\-+.^:,]", "");
  }

  public static String[] splitByCamelCase(String str) {
    List<String> list = new ArrayList<>();

    for (int i = 0; i < str.length(); i++) {
      if (Character.isUpperCase(str.charAt(i))) {
        list.add(str.substring(i, i + 1).toLowerCase());
      }
    }

    return list.toArray(new String[0]);
  }
}