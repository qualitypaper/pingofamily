package com.qualitypaper.fluentfusion.util;

import com.qualitypaper.fluentfusion.model.user.User;
import com.qualitypaper.fluentfusion.util.types.Pair;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class FileUtils {

  private static final String COMMA_DELIMITER = ",";

  private FileUtils() {
  }


  public static String saveFromMultiPartFile(MultipartFile multipartFile, User user, Path to) {
    String fileType = Objects.requireNonNull(multipartFile.getContentType()).split("/")[1];
    String filename = StringUtils.encodeMD5(String.format("%s%s", multipartFile.getOriginalFilename(), user.getEmail()));
    if (fileType != null) {
      filename += "." + fileType;
    }

    char separator = System.getProperty("os.name").contains("Windows") ? '\\' : '/';

    File file = new File(to + (separator + filename));

    try {
      Files.createFile(file.toPath());
      Files.copy(multipartFile.getInputStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return file.getPath();
  }

  public static BufferedReader read(String filepath) throws IOException {
    File file = new File(filepath);
    if (!file.exists()) {
      Files.createFile(file.toPath());
    }

    return new BufferedReader(new FileReader(file));
  }

  public static BufferedWriter write(String filepath) throws IOException {
    File file = new File(filepath);
    if (!file.exists()) {
      Files.createFile(file.toPath());
    }

    return new BufferedWriter(new FileWriter(file));
  }

  public static List<Map<String, String>> readCsvMap(String filepath) throws IOException {
    List<Map<String, String>> result = new ArrayList<>();

    try (BufferedReader br = read(filepath)) {
      String columnLine = br.readLine();
      if (columnLine.isEmpty()) {
          return result;
      }
      String[] columns = columnLine.split(COMMA_DELIMITER);

      for (String line = br.readLine(); line != null; line = br.readLine()) {
        String[] values = line.split(COMMA_DELIMITER);

        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < values.length; i++) {
          map.put(columns[i], values[i]);
        }
        result.add(map);
      }
    }

    return result;
  }

  public static Pair<String[], List<Object[]>> readCsv(String filepath) throws IOException {
    return readCsv(filepath, 0, Integer.MAX_VALUE);
  }

  public static Pair<String[], List<Object[]>> readCsv(String filepath, int offset, int limit) throws IOException {
    List<Object[]> result = new ArrayList<>();
    String[] columns;

    try (BufferedReader br = read(filepath)) {
      String columnLine = br.readLine();
      columns = columnLine.split(COMMA_DELIMITER);

      for (int i = 0; i < offset; i++) {
        String _ = br.readLine();
      }

      int i = 0;

      for (String line = br.readLine(); i < limit && line != null; i++, line = br.readLine()) {
        String[] values = line.split(COMMA_DELIMITER);

        result.add(values);
      }
    }

    return new Pair<>(columns, result);
  }

  public static void writeCsv(List<String[]> data, String filepath) throws IOException {
    try (BufferedWriter bw = write(filepath)) {
      for (String[] line : data) {
        bw.write(String.join(",", line));
      }
    }
  }

}
