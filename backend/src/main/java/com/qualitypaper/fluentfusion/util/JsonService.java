package com.qualitypaper.fluentfusion.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JsonService {

  public static String findJson(String str) {
    int startIndex;

    int startPIndex = str.indexOf('{');
    int startAIndex = str.indexOf('[');
    if (startPIndex == -1 && startAIndex == -1) return "";
    if (startPIndex == -1) {
      startIndex = startAIndex;
    } else if (startAIndex == -1) {
      startIndex = startPIndex;
    } else {
      startIndex = Math.min(startPIndex, startAIndex);
    }

    int lastPIndex = str.lastIndexOf('}');
    int lastAIndex = str.lastIndexOf(']');
    if (lastPIndex == -1 && lastAIndex == -1) return "";

    return str.substring(startIndex, Math.max(lastPIndex, lastAIndex) + 1);
  }

}